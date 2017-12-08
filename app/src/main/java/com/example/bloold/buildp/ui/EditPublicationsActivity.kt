package com.example.bloold.buildp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.DocumentEditAdapter
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.PhotoHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.components.ChooseImageActivity
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.ActivityEditPublicationsBinding
import com.example.bloold.buildp.model.DocModel
import com.example.bloold.buildp.model.NameCodeInterface
import com.example.bloold.buildp.model.PropertyFile
import com.example.bloold.buildp.utils.PermissionUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*
import kotlin.collections.ArrayList

class EditPublicationsActivity : ChooseImageActivity() {
    private lateinit var mBinding: ActivityEditPublicationsBinding
    private lateinit var documentEditAdapter: DocumentEditAdapter
    private var catalogObject: CatalogObject? = null
    private var objectId: Int = -1
    private var uploadedPublications: ArrayList<Long> = ArrayList()
    private var coverFile:String?=null
    private var coverFileId:Long?=null

    companion object {
        private val REQUEST_CODE_CHOOSE_FILE=125
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_publications)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        objectId=intent.getIntExtra(IntentHelper.EXTRA_OBJECT_ID, -1)

        documentEditAdapter= DocumentEditAdapter(OnItemClickListener {
            it.getSrcFile()?.let {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse(it)), getString(R.string.choose_application)))
            }})
        mBinding.rvPublications.layoutManager=LinearLayoutManager(this)
        mBinding.rvPublications.adapter=documentEditAdapter
        loadObjectDetails(objectId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&
                requestCode==REQUEST_CODE_CHOOSE_FILE)
        {
            data?.let {
                PhotoHelper.getPath(it.data)?.let {
                    val document=DocModel()
                    document.propertyFile=PropertyFile().apply { src=it }
                    document.name=it.substring(it.lastIndexOf("/")+1)
                    documentEditAdapter.addData(document)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when {
        item.itemId==R.id.action_done -> {
            if(mBinding.flLoading.visibility!=View.VISIBLE) {
                var error = UIHelper.setErrorIfEmpty(mBinding.etPublicationName, getString(R.string.need_fill_this_field))
                if(!error) uploadData()
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateUI()
    {
        catalogObject?.let {
            documentEditAdapter.addAll(it.publicationsData?.toList())
        }
    }

    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
    private fun loadObjectDetails(objectId:Int)
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getCatalogObjects(HashMap<String,String>().apply { put("filter[ID][0]", objectId.toString()) },
                1, 1,  selectParams = ApiHelper.fullParams)
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .doFinally {
                    if(catalogObject!=null)
                    {
                        updateUI()
                        showProgress(false)
                    }
                    else finish()
                }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<CatalogObject>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<CatalogObject>) {
                        catalogObject=result.data?.items?.firstOrNull()
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))
    }
    override fun onImageChosen(imagePath: String) {
        coverFile=imagePath
        mBinding.tvCover.text=imagePath.substring(imagePath.lastIndexOf("/")+1)
        mBinding.tvCover.visibility=View.VISIBLE
        mBinding.bAddCover.setText(R.string.change_cover)
    }
    fun onAddCoverClick(v:View?)
    {
        chooseImage()
    }
    fun onAddPublicationFileClick(v: View?)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL)
        }
        else startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                .setType("*/*"), REQUEST_CODE_CHOOSE_FILE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_EXTERNAL) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                onAddPublicationFileClick(null)
            }
        }
    }
    private fun uploadData()
    {
        //Загружаем файлы документов
        //Сохраняем данные и записываем id документов
        showProgress(true)
        val firstNotUploadedDoc:NameCodeInterface?=if(!documentEditAdapter.notUploadedDocs.isEmpty()) documentEditAdapter.mValues[documentEditAdapter.notUploadedDocs.first()] else null
        when {
            firstNotUploadedDoc!=null -> firstNotUploadedDoc.getSrcFile()?.let {
                sendPublications(it, System.currentTimeMillis(),
                        object : OnFileUploadListener {
                            override fun onFileUploaded(name: String, key: Long?, success: Boolean) {
                                if(success)
                                {
                                    if(key!=null) {
                                        documentEditAdapter.setDocUploaded(documentEditAdapter.notUploadedDocs.first())
                                        uploadedPublications.add(key)
                                        uploadData()//рекурсивно вызываем для выполнения дальше
                                    }
                                }
                                else showProgress(false)
                            }
                        })
            }
            !coverFile.isNullOrEmpty()&&coverFileId==null -> coverFile?.let {
                sendPublications(it, System.currentTimeMillis(),
                        object : OnFileUploadListener {
                            override fun onFileUploaded(name: String, key: Long?, success: Boolean) {
                                if(success)
                                {
                                    if(key!=null) {
                                        coverFileId=key
                                        uploadData()//рекурсивно вызываем для выполнения дальше
                                    }
                                }
                                else showProgress(false)
                            }
                        })
            }
            else -> compositeDisposable.add(ServiceGenerator.serverApi.addPublication(objectId,
                    mBinding.etPublicationName.text.toString(),
                    coverFileId,
                    ApiHelper.generatePublicationParams(uploadedPublications))
                    .compose(RxHelper.applySchedulers())
                    .doFinally { showProgress(false) }
                    .subscribeWith(object : DisposableSingleObserver<Response<Void>>() {
                        override fun onSuccess(result: Response<Void>) {
                            if(result.isSuccessful)
                            {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            else
                                UIHelper.showServerError(result, this@EditPublicationsActivity)
                        }
                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            if (e is UnknownHostException || e is ConnectException)
                                Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        }
                    }))
        }
    }
    private fun sendPublications(filePath: String, key:Long, onFileUploadListener: OnFileUploadListener)
    {
        compositeDisposable.add(Observable.fromCallable({ Base64.encodeToString(File(filePath).readBytes(), Base64.DEFAULT) })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if(it.isEmpty())
                    {
                        onFileUploadListener.onFileUploaded(filePath, key,false)
                        Toast.makeText(this, R.string.cant_convert_image_file, Toast.LENGTH_LONG).show()
                    }
                    !it.isEmpty()
                }
                .map { ServiceGenerator.serverApi.uploadFile(
                        ApiHelper.generateUploadFileParams(it.substring(it.lastIndexOf("/")+1), it, key))
                        .compose(RxHelper.applySchedulers())
                        .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithDataObject<Long>>>() {
                            override fun onSuccess(result: Response<BaseResponseWithDataObject<Long>>) {
                                if(result.isSuccessful)
                                {
                                    //Получаем ключ загруженного файла
                                    onFileUploadListener.onFileUploaded(filePath, result.body()?.data?.items?.first(),true)
                                }
                                else {
                                    UIHelper.showServerError(result, this@EditPublicationsActivity)
                                    onFileUploadListener.onFileUploaded(filePath, key, false)
                                }
                            }
                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                if (e is UnknownHostException || e is ConnectException)
                                    Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                                else
                                    Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                                onFileUploadListener.onFileUploaded(filePath, key,false)
                            }
                        })}
                .subscribe())
    }
    interface OnFileUploadListener
    {
        fun onFileUploaded(name:String, key:Long?, success:Boolean)
    }
}

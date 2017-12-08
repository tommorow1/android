package com.example.bloold.buildp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.adapter.AudioEditAdapter
import com.example.bloold.buildp.adapter.PhotoEditAdapter
import com.example.bloold.buildp.adapter.VideoEditAdapter
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.*
import com.example.bloold.buildp.common.*
import com.example.bloold.buildp.components.ChooseImageActivity
import com.example.bloold.buildp.components.OnItemClickListener
import com.example.bloold.buildp.components.SpinnerWithoutLPaddingAdapter
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.ActivityEditStateBinding
import com.example.bloold.buildp.databinding.DialogAddVideoUrlBinding
import com.example.bloold.buildp.model.AudioModel
import com.example.bloold.buildp.model.ConditionMark
import com.example.bloold.buildp.model.PhotoModel
import com.example.bloold.buildp.model.VideoModel
import com.example.bloold.buildp.utils.PermissionUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class EditStateActivity : ChooseImageActivity() {
    private lateinit var mBinding: ActivityEditStateBinding
    private lateinit var photoEditAdapter: PhotoEditAdapter
    private lateinit var videoEditAdapter: VideoEditAdapter
    private lateinit var audioEditAdapter: AudioEditAdapter
    private var player: MediaPlayer = MediaPlayer().apply { setAudioStreamType(AudioManager.STREAM_MUSIC) }


    private var catalogObject: CatalogObject? = null
    private var objectId: Int = -1
    private var uploadedPhotos: ArrayList<Long> = ArrayList()
    private var uploadedAudios: ArrayList<Long> = ArrayList()

    companion object {
        val REQUEST_CODE_EDIT_STATE_OBJECT=124
        private val REQUEST_CODE_CHOOSE_FILE=125
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_state)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        objectId=intent.getIntExtra(IntentHelper.EXTRA_OBJECT_ID, -1)
        player.setOnPreparedListener { it.start() }

        UIHelper.makeEditTextScrollable(mBinding.etStateDescription)
        mBinding.tvConditionCriteria.movementMethod = LinkMovementMethod.getInstance()
        mBinding.tvPhotoRules.movementMethod = LinkMovementMethod.getInstance()

        photoEditAdapter= PhotoEditAdapter(OnItemClickListener {
                    startActivity(Intent(this, ImageViewActivity::class.java)
                            .putExtra(IntentHelper.EXTRA_IMAGE_URL, if(it.id==-1L) it.src else it.fullPath()))
                },
                OnItemClickListener { item ->
                        catalogObject?.photosData?.filterNot { it.src == item.src }
                        photoEditAdapter.remove(item)
                })
        mBinding.rvPhotos.layoutManager=LinearLayoutManager(this)
        mBinding.rvPhotos.adapter=photoEditAdapter

        videoEditAdapter= VideoEditAdapter(OnItemClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.getYoutubeLink())))
                },
                OnItemClickListener { item ->
                    catalogObject?.videoData?.filterNot { it.youtubeCode == item.youtubeCode }
                    videoEditAdapter.remove(item)
                })
        mBinding.rvVideos.layoutManager=LinearLayoutManager(this)
        mBinding.rvVideos.adapter=videoEditAdapter

        audioEditAdapter= AudioEditAdapter(OnItemClickListener {
            audioEditAdapter.getPlayAudioModelNow()?.let { item ->
                    if(item==it)
                    {
                        audioEditAdapter.stopPlay()
                        player.reset()
                        return@OnItemClickListener
                    }
                }
                try {
                    audioEditAdapter.startPlay(it)
                    if(player.isPlaying)
                        player.reset()
                    player.setDataSource(it.fullPath())
                    player.prepareAsync()
                    //player.start()
                } catch (e: Exception) {
                    audioEditAdapter.stopPlay()
                    e.printStackTrace()
                }
                },
                OnItemClickListener { item ->
                    catalogObject?.audioData?.filterNot { it.src == item.src }
                    audioEditAdapter.remove(item)
                })
        mBinding.rvAudios.layoutManager=LinearLayoutManager(this)
        mBinding.rvAudios.adapter=audioEditAdapter

        loadConditionMarks()
    }

    override fun onPause() {
        if(player.isPlaying)
        {
            audioEditAdapter.stopPlay()
            player.reset()
        }
        super.onPause()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
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
                    val audio=AudioModel()
                    audio.src=it
                    audio.name=it.substring(it.lastIndexOf("/")+1)
                    audioEditAdapter.addData(audio)
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
            if(mBinding.flLoading.visibility!=View.VISIBLE)
                uploadData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun updateUI()
    {
        catalogObject?.let {
            mBinding.etStateDescription.setText(it.condition)
            photoEditAdapter.addAll(it.photosData?.toList())
            videoEditAdapter.addAll(it.videoData?.toList())
            audioEditAdapter.addAll(it.audioData?.toList())
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
    private fun loadConditionMarks()
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getConditionMark()
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .subscribeWith(object : DisposableSingleObserver<BaseResponse<ConditionMark>>() {
                    override fun onSuccess(result: BaseResponse<ConditionMark>) {
                        result.data?.let {
                            val notSetCondition=ConditionMark()
                            notSetCondition.value=getString(R.string.choose_condition)
                            mBinding.spConditionMark.adapter = SpinnerWithoutLPaddingAdapter(this@EditStateActivity,
                                    android.R.layout.simple_spinner_dropdown_item, arrayOf(notSetCondition)+ it)
                            loadCurrentCondition()
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }))
    }
    private fun loadCurrentCondition()
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getObjectConditionMark(objectId)
                .compose(RxHelper.applySchedulers())
                .doFinally {
                    showProgress(false)
                    updateUI()
                }
                .subscribeWith(object : DisposableSingleObserver<CurrentUserCondition>() {
                    override fun onSuccess(result: CurrentUserCondition) {
                        result.data?.items?.firstOrNull()?.let {
                            for(i in 0..mBinding.spConditionMark.adapter.count)
                                if(i==it.conditionId)
                                {
                                    mBinding.spConditionMark.setSelection(i)
                                    break
                                }
                        }
                        //Пока решили не отображать текущие данные объекта
                        //loadObjectDetails(objectId)
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
        val photo=PhotoModel()
        photo.src=imagePath
        photoEditAdapter.addData(photo)
    }

    fun onAddPhotoClick(v: View)
    {
        chooseImage()
    }
    fun onAddVideoClick(v: View)
    {
        val builder = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate<DialogAddVideoUrlBinding>(LayoutInflater.from(this), R.layout.dialog_add_video_url, null, false)
        builder.setView(binding.root)
                .setPositiveButton(R.string.add, { _, _ ->
                    if(UIHelper.getYoutubePreviewImage(binding.etName.text.toString())!=null) {
                        val videoModel=VideoModel()
                        videoModel.youtubeCode=UIHelper.extractYoutubeId(binding.etName.text.toString())
                        videoEditAdapter.addData(videoModel)
                    } else Toast.makeText(this@EditStateActivity, R.string.need_only_youtube_link, Toast.LENGTH_LONG).show()
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setTitle(R.string.add_video_title)
        builder.create().show()
    }

    fun onAddAudioClick(v: View?)
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
                onAddAudioClick(null)
            }
        }
    }
    private fun uploadData()
    {
        //Загружаем файлы фото
        //Загружаем файлы аудио
        //Удаляем аудио, видео и фото
        //Отправляем состояние(оценку и описание)
        //Сохраняем данные и записываем id фото, аудио и отправляем ссылки на видео
        showProgress(true)
        val firstNotUploadedPhoto=photoEditAdapter.mValues.firstOrNull { it.id==-1L }
        if(firstNotUploadedPhoto!=null)
        {
            compressPhotoAndSend(firstNotUploadedPhoto.src, System.currentTimeMillis(),
                    object : OnFileUploadListener {
                        override fun onFileUploaded(name: String, key: Long?, success: Boolean) {
                            if(success)
                            {
                                if(key!=null) {
                                    uploadedPhotos.add(key)
                                    photoEditAdapter.setIdForUploadedPhoto(firstNotUploadedPhoto.src, key)
                                    uploadData()//рекурсивно вызываем для выполнения дальше
                                }
                            }
                            else showProgress(false)
                        }
                    })
        }
        else if(audioEditAdapter.mValues.firstOrNull { it.id==-1L }!=null)
        {
            audioEditAdapter.mValues.firstOrNull { it.id==-1L }?.let {
                encodeAudioAndSend(it.src, System.currentTimeMillis(),
                        object : OnFileUploadListener {
                            override fun onFileUploaded(name: String, key: Long?, success: Boolean) {
                                if(success)
                                {
                                    if(key!=null) {
                                        uploadedAudios.add(key)
                                        audioEditAdapter.setIdForUploadedPhoto(it.src, key)
                                        uploadData()//рекурсивно вызываем для выполнения дальше
                                    }
                                }
                                else showProgress(false)
                            }
                        })
            }
        }
        else
        {
            compositeDisposable.add(ServiceGenerator.serverApi.editObject(objectId,
                    mBinding.etStateDescription.text.toString(),
                    (mBinding.spConditionMark.selectedItem as ConditionMark).id,
                    ApiHelper.generateChangedPhotosParams(uploadedPhotos)+
                            ApiHelper.generateChangedAudiosParams(uploadedAudios)+
                            ApiHelper.generateChangedVideoParams(videoEditAdapter.mValues.filter { it.name==null&&it.youtubeCode==null }))
                    .compose(RxHelper.applySchedulers())
                    .doFinally { showProgress(false) }
                    .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                        override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                            if(result.isSuccessful&&result.body()?.code==200)
                            {
                                //TODO
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            else
                                UIHelper.showServerError(result, this@EditStateActivity)
                        }
                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            if (e is UnknownHostException || e is ConnectException)
                                Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                            //TODO
                        }
                    }))
        }

        //else sendConditionMark()
    }
    private fun sendConditionMark()
    {
        //TODO Сообщение не должно быть пустое
        compositeDisposable.add(ServiceGenerator.serverApi.sendCondition(objectId, mBinding.etStateDescription.text.toString(),
                (mBinding.spConditionMark.selectedItem as ConditionMark).id,
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()))
                .compose(RxHelper.applySchedulers())
                .doFinally { showProgress(false) }
                .doOnSubscribe { showProgress(true) }
                .subscribeWith(object : DisposableSingleObserver<Response<Void>>() {
                    override fun onSuccess(result: Response<Void>) {
                        if(result.isSuccessful)
                        {
                            //TODO
                        }
                        else
                            UIHelper.showServerError(result, this@EditStateActivity)
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        //TODO
                    }
                }))

    }
    private fun compressPhotoAndSend(filePath: String, key:Long, onFileUploadListener: OnFileUploadListener)
    {
        compositeDisposable.add(Observable.fromCallable({
            val bytes = ImageUtils.compressPhoto(filePath)
            if (bytes != null) {
                val imgBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                if (!TextUtils.isEmpty(imgBase64)) imgBase64
                else ""
            } else ""})
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
                            UIHelper.showServerError(result, this@EditStateActivity)
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
    private fun encodeAudioAndSend(filePath: String, key:Long, onFileUploadListener: OnFileUploadListener)
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
                                    UIHelper.showServerError(result, this@EditStateActivity)
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

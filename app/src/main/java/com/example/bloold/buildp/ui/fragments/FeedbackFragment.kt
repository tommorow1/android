package com.example.bloold.buildp.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.FeedbackResponse
import com.example.bloold.buildp.common.PhotoHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.ChooseImageActivity
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentFeedbackBinding
import com.example.bloold.buildp.ui.EditPublicationsActivity
import com.example.bloold.buildp.utils.PermissionUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Response
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException


class FeedbackFragment : NetworkFragment() {
    private lateinit var mBinding:FragmentFeedbackBinding
    private var file:String?=null
    private var sentFileId:Long?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
        mBinding.listener=this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.etPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.title=getString(R.string.feedback)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&
                requestCode== REQUEST_CODE_CHOOSE_FILE)
        {
            data?.let {
                PhotoHelper.getPath(it.data)?.let {
                    file=it
                    mBinding.etFile.text=it.substring(it.lastIndexOf("/")+1)
                }
            }
        }
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ChooseImageActivity.REQUEST_READ_EXTERNAL) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                onChooseFileClick(null)
            }
        }
    }

    fun onSendClick(v:View)
    {
        if(mBinding.etEmail.text.isEmpty()&&mBinding.etPhone.text.isEmpty())
        {
            Toast.makeText(activity, R.string.need_fill_email_or_phone, Toast.LENGTH_LONG).show()
            return
        }
        if(mBinding.etMessage.text.isEmpty())
        {
            Toast.makeText(activity, R.string.need_input_message, Toast.LENGTH_LONG).show()
            return
        }
        showProgress(true)
        if(!file.isNullOrEmpty())
        {
            uploadFile(file!!, 123, object : EditPublicationsActivity.OnFileUploadListener {
                override fun onFileUploaded(name: String, key: Long?, success: Boolean) {
                    if(!success) showProgress(false)
                    else {
                        sentFileId=key
                        sendMessage()
                    }
                }
            })
        }
        else sendMessage()
    }
    private fun sendMessage()
    {
        getCompositeDisposable().add(ServiceGenerator.serverApi.sendFeedback(mBinding.etName.text.toString(),
                mBinding.etEmail.text.toString(),
                mBinding.etPhone.text.toString().replace("[\\s\\-()]".toRegex(), ""),
                mBinding.etMessage.text.toString(), sentFileId)
                .compose(RxHelper.applySchedulers())
                .doFinally { showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<Response<FeedbackResponse>>() {
                    override fun onSuccess(result: Response<FeedbackResponse>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            mBinding.etName.setText("")
                            mBinding.etEmail.setText("")
                            mBinding.etPhone.setText("")
                            mBinding.etMessage.setText("")
                            mBinding.etFile.setText(R.string.choose_file)
                            file=null
                            sentFileId=null
                            Toast.makeText(activity, R.string.feedback_sent, Toast.LENGTH_SHORT).show()
                        }
                        else if(result.body()?.code!=200) Toast.makeText(activity, result.body()?.data?.first(), Toast.LENGTH_SHORT).show()
                        else UIHelper.showServerError(result, activity)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))

    }
    fun onChooseFileClick(v:View?)
    {
        activity?.baseContext?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ChooseImageActivity.REQUEST_READ_EXTERNAL)
            }
            else startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                    .setType("*/*"), REQUEST_CODE_CHOOSE_FILE)
        }
    }

    private fun uploadFile(filePath: String, key:Long, onFileUploadListener: EditPublicationsActivity.OnFileUploadListener)
    {
        getCompositeDisposable().add(Observable.fromCallable({ Base64.encodeToString(File(filePath).readBytes(), Base64.DEFAULT) })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter {
                    if(it.isEmpty())
                    {
                        onFileUploadListener.onFileUploaded(filePath, key,false)
                        Toast.makeText(activity, R.string.cant_convert_image_file, Toast.LENGTH_LONG).show()
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
                                    UIHelper.showServerError(result, activity)
                                    onFileUploadListener.onFileUploaded(filePath, key, false)
                                }
                            }
                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                if (e is UnknownHostException || e is ConnectException)
                                    Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                                else
                                    Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show()
                                onFileUploadListener.onFileUploaded(filePath, key,false)
                            }
                        })}
                .subscribe())
    }

    companion object {
        private val REQUEST_CODE_CHOOSE_FILE=125
        fun newInstance(): FeedbackFragment
                = FeedbackFragment()
    }
}

package com.example.bloold.buildp.ui.fragments

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.BaseResponseWithoutData
import com.example.bloold.buildp.api.data.NotificationSettings
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.NetworkFragment
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.FragmentMySettingsBinding
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_catalog_object_details.*
import kotlinx.android.synthetic.main.fragment_my_settings.*
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

class MySettingsFragment : NetworkFragment(), CompoundButton.OnCheckedChangeListener {
    private lateinit var mBinding: FragmentMySettingsBinding

    private val BESIDE_QUEST="BESIDE_QUEST"
    private val BESIDE_FAVORITE="BESIDE_FAVORITE"
    private val NEW_MESSAGE="NEW_MESSAGE"
    private val NEW_NOTICE="NEW_NOTICE"

    companion object {
        fun newInstance() = MySettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_settings, container, false)
        mBinding.listener=this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadNotificationSettings(true)
    }

    override fun onResume() {
        super.onResume()
        activity?.toolbar?.setTitle(R.string.my_settings)
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
    private fun clearListeners()
    {
        scObjectQuestNear.setOnCheckedChangeListener(null)
        scFavObjectNear.setOnCheckedChangeListener(null)
        scNewMessage.setOnCheckedChangeListener(null)
        scNewNotification.setOnCheckedChangeListener(null)
    }
    private fun configureListeners()
    {
        scObjectQuestNear.setOnCheckedChangeListener(this)
        scFavObjectNear.setOnCheckedChangeListener(this)
        scNewMessage.setOnCheckedChangeListener(this)
        scNewNotification.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        sendNotificationSettings()
    }

    private fun loadNotificationSettings(showProgress: Boolean)
    {
        clearListeners()
        getCompositeDisposable().add(ServiceGenerator.serverApi.getNotificationSettings()
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { if(showProgress) showProgress(true)}
                .doFinally {
                    if(showProgress) showProgress(false)
                    configureListeners()
                }
                .subscribeWith(object : DisposableSingleObserver<Response<NotificationSettings>>() {
                    override fun onSuccess(result: Response<NotificationSettings>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            result.body()?.data?.let {
                                scObjectQuestNear.isChecked=it.contains(BESIDE_QUEST)
                                scFavObjectNear.isChecked=it.contains(BESIDE_FAVORITE)
                                scNewMessage.isChecked=it.contains(NEW_MESSAGE)
                                scNewNotification.isChecked=it.contains(NEW_NOTICE)
                            }
                        }
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
    private fun sendNotificationSettings()
    {
        val enabledNotices=ArrayList<String>()
        if(scObjectQuestNear.isChecked) enabledNotices.add(BESIDE_QUEST)
        if(scFavObjectNear.isChecked) enabledNotices.add(BESIDE_FAVORITE)
        if(scNewMessage.isChecked) enabledNotices.add(NEW_MESSAGE)
        if(scNewNotification.isChecked) enabledNotices.add(NEW_NOTICE)
        getCompositeDisposable().add(ServiceGenerator.serverApi.setNotificationSettings(enabledNotices)
                .compose(RxHelper.applySchedulers())
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                    override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                        if(result.isSuccessful&&result.body()?.code==200)
                        {
                            //Всё хорошо, ничего не делаем
                        }
                        else
                        {
                            UIHelper.showServerError(result, activity)
                            loadNotificationSettings(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(activity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show()
                        loadNotificationSettings(false)
                    }
                }))
    }

    fun onClearImageCacheClick(v:View)
    {
        Glide.get(activity).clearMemory()
        Toast.makeText(activity, R.string.cache_cleared, Toast.LENGTH_SHORT).show()
    }
}

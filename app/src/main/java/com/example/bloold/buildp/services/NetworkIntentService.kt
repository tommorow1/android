package com.example.bloold.buildp.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import android.text.TextUtils
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.BaseResponseWithoutData
import com.example.bloold.buildp.common.IntentHelper
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * Created by sagus on 19.11.2017.
 */
class NetworkIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            when (action) {
                IntentHelper.ACTION_TOGGLE_FAVOURITE -> toggleFavourite(intent.getIntExtra(IntentHelper.EXTRA_OBJECT_ID,-1))
                IntentHelper.ACTION_SEND_PUSH_TOKEN -> sendPushToken(intent.getStringExtra(IntentHelper.EXTRA_PUSH_TOKEN))
                IntentHelper.ACTION_SEND_NOTIFICATION_READ -> setNotificationRead()
            }
        }
    }

    private fun toggleFavourite(objectId: Int) {
        ServiceGenerator.serverApi.toggleFavourite(objectId)
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<Void>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<Void>) {
                      /*  if (result.isSuccessful)
                            sendEvent(IntentHelper.ACTION_ARCHIVE_PRODUCT, null)
                        else {
                            val error = ErrorUtils.parseError(result)
                            if (error != null && !TextUtils.isEmpty(error.error_text))
                                sendEvent(IntentHelper.ACTION_ARCHIVE_PRODUCT, error.error_text)
                            else
                                sendEvent(IntentHelper.ACTION_ARCHIVE_PRODUCT, R.string.server_error)
                        }*/
                    }

                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        if (error is UnknownHostException || error is ConnectException)
                            sendEvent(IntentHelper.ACTION_TOGGLE_FAVOURITE, getString(R.string.error_check_internet), objectId)
                        else
                            sendEvent(IntentHelper.ACTION_TOGGLE_FAVOURITE, getString(R.string.server_error), objectId)
                    }
                })
    }

    private fun sendPushToken(pushToken:String)
    {
        ServiceGenerator.serverApi.setPushToken(pushToken)
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                    override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                    }
                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        if (error is UnknownHostException || error is ConnectException)
                            sendEvent(IntentHelper.ACTION_SEND_PUSH_TOKEN, getString(R.string.error_check_internet))
                        else
                            sendEvent(IntentHelper.ACTION_SEND_PUSH_TOKEN, getString(R.string.server_error))
                    }
                })
    }
    private fun setNotificationRead()
    {
        ServiceGenerator.serverApi.setNotificationsRead()
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                    override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                    }
                    override fun onError(error: Throwable) {
                        error.printStackTrace()
                        if (error is UnknownHostException || error is ConnectException)
                            sendEvent(IntentHelper.ACTION_SEND_NOTIFICATION_READ, getString(R.string.error_check_internet))
                        else
                            sendEvent(IntentHelper.ACTION_SEND_NOTIFICATION_READ, getString(R.string.server_error))
                    }
                })
    }

    private fun detectAndSendError(action: String, error: Throwable) {
        if (error is UnknownHostException || error is ConnectException)
            sendEvent(action, R.string.error_check_internet)
        else
            sendEvent(action, R.string.server_error)
    }

    /*** Отправка сообщения о завершении загрузки  */
    private fun sendEvent(action: String, errorMsg: String?) {
        val intent = Intent(action)
        if (!TextUtils.isEmpty(errorMsg)) intent.putExtra(IntentHelper.EXTRA_ERROR_MSG, errorMsg)
        sendBroadcast(intent)
    }

    private fun sendEvent(action: String, errorMsg: String, value: Int) {
        val intent = Intent(action)
        if (!TextUtils.isEmpty(errorMsg)) intent.putExtra(IntentHelper.EXTRA_ERROR_MSG, errorMsg)
        intent.putExtra(IntentHelper.EXTRA_VALUE, value)
        sendBroadcast(intent)
    }

    private fun sendEvent(action: String, @StringRes errorId: Int) {
        sendEvent(action, getString(errorId))
    }

    companion object {

        private val TAG = "NetworkIntentService"

        fun toogleFavourite(context: Context, objectId: Int) {
            context.startService(Intent(context, NetworkIntentService::class.java)
                    .setAction(IntentHelper.ACTION_TOGGLE_FAVOURITE)
                    .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
        }
        fun sendPushToken(context: Context, pushToken: String) {
            context.startService(Intent(context, NetworkIntentService::class.java)
                    .setAction(IntentHelper.ACTION_SEND_PUSH_TOKEN)
                    .putExtra(IntentHelper.EXTRA_PUSH_TOKEN, pushToken))
        }
        fun setAllNotificationsRead(context: Context) {
            context.startService(Intent(context, NetworkIntentService::class.java)
                    .setAction(IntentHelper.ACTION_SEND_NOTIFICATION_READ))
        }
    }
}
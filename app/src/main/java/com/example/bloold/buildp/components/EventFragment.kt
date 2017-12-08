package com.example.bloold.buildp.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.bloold.buildp.common.IntentHelper


/**
 * Created by Leonov Oleg, http://pandorika-it.com
 * Следит за событиями отправляемые с sendBroadcast
 */
abstract class EventFragment : NetworkFragment() {
    private var broadcastReceiver: BroadcastReceiver? = null

    /*** В родительском классе возвращает список ACTION на которые надо подписаться  */
    protected abstract val actions: Array<String>

    /*** Получение события родительским классом  */
    protected abstract fun onEventReceived(action: String, errorMsg: String?, data: Intent?)

    override fun onResume() {
        super.onResume()
        registerBroadcastReceiver(actions)
    }

    override fun onPause() {
        activity?.unregisterReceiver(broadcastReceiver)
        super.onPause()
    }

    private fun registerBroadcastReceiver(actions: Array<String>) {
        if (broadcastReceiver == null) {
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    onEventReceived(intent.action, intent.getStringExtra(IntentHelper.EXTRA_ERROR_MSG), intent)
                }
            }
        }

        val intentFilter = IntentFilter()
        for (action in actions) intentFilter.addAction(action)
        activity?.registerReceiver(broadcastReceiver, intentFilter)
    }
}


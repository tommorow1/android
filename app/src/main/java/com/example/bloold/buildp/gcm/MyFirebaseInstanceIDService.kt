package com.example.bloold.buildp.gcm

import android.util.Log
import com.example.bloold.buildp.common.Settings

import com.example.bloold.buildp.services.NetworkIntentService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken)
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        Log.d(TAG, "sendRegistrationToServer() called with: token = [$token]")
        token?.let {
            Settings.pushToken = it
            NetworkIntentService.sendPushToken(this, it)
        }
    }

    companion object {
        private val TAG = "MyFirebaseIIDService"
    }
}
package com.example.bloold.buildp

import android.support.multidex.MultiDexApplication
import com.vk.sdk.VKSdk

/**
 * Created by yourockinc on 19.10.17.
 */

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(this)
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
    }

}

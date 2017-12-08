package com.example.bloold.buildp.common

import android.content.SharedPreferences
import android.text.TextUtils

import com.example.bloold.buildp.MyApplication

import java.util.HashSet

/**
 * Created by Leonov Oleg, http://pandorika-it.com
 */

object Settings {
    val URL_NEWS = "https://ruin.net.ru/news/?clear_for_app=y"
    val URL_VOTING = "https://ruin.net.ru/voting/?clear_for_app=y"
    val URL_ABOUT = "https://ruin.net.ru/landing/?clear_for_app=y"
    val URL_AGREEMENT = "https://ruin.net.ru/terms/?clear_for_app=y"

    val DIRECTIONS_API_KEY = "AIzaSyA0SGbiZ28WwwukGWgPwmqIKTd7GJMUDfY"
    private val KEY_CATALOG_FILTERS = "catalogFilters"
    private val KEY_USER_TOKEN = "AuthToken"
    private val KEY_UDID = "udid"
    private val KEY_PUSH_TOKEN = "pushTOken"

    fun getUdid(): String? {
        var udid:String? = prefs.getString(KEY_UDID, "")
        if (TextUtils.isEmpty(udid)) {
            udid = android.provider.Settings.System.getString(MyApplication.instance.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            if(udid.isNullOrEmpty())
                udid=System.currentTimeMillis().toString()
            udid="android_$udid"
            prefs.edit()
                    .putString(KEY_UDID, udid)
                    .apply()
        }
        return udid
    }
    /*** Фильтры для каталога  */
    var catalogFilters: Set<String>?
        get() = prefs.getStringSet(KEY_CATALOG_FILTERS, HashSet<String>())
        set(value) = prefs.edit().putStringSet(KEY_CATALOG_FILTERS, value).apply()

    /*** Пуш токен  */
    var pushToken: String?
        get() = prefs.getString(KEY_PUSH_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_PUSH_TOKEN, value).apply()

    /*** Токен авторизации  */
    var userToken: String?
        get() = prefs.getString(KEY_USER_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_USER_TOKEN, userToken).apply()

    private val prefs: SharedPreferences
        get() = MyApplication.instance.getSharedPreferences("main", 0)
}

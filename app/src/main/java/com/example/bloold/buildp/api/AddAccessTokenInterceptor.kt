package com.example.bloold.buildp.api

import com.example.bloold.buildp.common.Settings
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by Leonov Oleg, http://pandorika-it.com
 * Добавляет токен авторизации ко всем запросам
 */
class AddAccessTokenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        //Добавляем хедер, передающий ключ, полученный во время авторизации
        val originalRequest = chain.request()

        val builder = originalRequest.newBuilder()
        builder.addHeader("Device-Id", Settings.getUdid())
        if(!Settings.userToken.isNullOrEmpty())
            builder.addHeader("Auth-Token", Settings.userToken)
        //builder.addHeader("Authorization", "Basic"+String(android.util.Base64.encode("defa:defa".toByteArray(), android.util.Base64.NO_WRAP)))

        return chain.proceed(builder.build())
    }
}

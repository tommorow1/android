package com.example.bloold.buildp.api

import com.example.bloold.buildp.api.deserializers.AudioDataDeserializer
import com.example.bloold.buildp.api.deserializers.DiffBodyDeserializer
import com.example.bloold.buildp.api.deserializers.PhotoModelDeserializer
import com.example.bloold.buildp.model.AudioModel
import com.example.bloold.buildp.model.PhotoModel
import com.example.bloold.buildp.model.Suggestion
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule

import java.util.TimeZone
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 24.05.16.
 */
object ServiceGenerator {
    val SITE_URL = "http://ruinnet.idefa.ru"
    val BASE_URL = SITE_URL+"/api_app/"
    private val httpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            //.authenticator(new TokenAuthenticator())
            .addNetworkInterceptor(AddAccessTokenInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

    private val builder = Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(
                    ObjectMapper().registerModule(SimpleModule()
                            .addDeserializer(AudioModel::class.java, AudioDataDeserializer())
                            .addDeserializer(PhotoModel::class.java, PhotoModelDeserializer())
                            .addDeserializer(Suggestion.SuggestionDiff.DiffBody::class.java, DiffBodyDeserializer()))
                            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                            .setTimeZone(TimeZone.getDefault())
            ))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    /*** API для работы с сервером  */
    val serverApi: ServerApi
        get() = retrofit(BASE_URL).create(ServerApi::class.java)

    fun retrofit(domain: String): Retrofit =
            builder.client(httpClient.build()).baseUrl(domain).build()
}

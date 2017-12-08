package com.example.bloold.buildp.api

import android.content.Intent
import com.example.bloold.buildp.MyApplication
import com.example.bloold.buildp.api.data.BaseResponse
import com.example.bloold.buildp.api.data.BaseResponseWithoutData
import com.example.bloold.buildp.common.IntentHelper
import retrofit2.Response

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 26.09.17.
 */

object ErrorUtils {
    fun parseError(response: Response<*>?): BaseResponseWithoutData? {
        val converter = ServiceGenerator.retrofit(ServiceGenerator.BASE_URL)
                .responseBodyConverter<BaseResponse<*>>(BaseResponse::class.java, arrayOfNulls(0))

        if (response?.body() != null && response.body() is BaseResponseWithoutData) {
            val errorResponse = response.body() as? BaseResponseWithoutData
            if (errorResponse != null) {
                if(errorResponse.code==401) MyApplication.instance.sendBroadcast(Intent(IntentHelper.ACTION_NEED_REAUTH))
                return errorResponse
            }
        }

        try {
            val res = converter.convert(response!!.errorBody())
            return res
        } catch (e: Exception) {
        }
        return null
    }
}

package com.example.bloold.buildp.model

import com.example.bloold.buildp.api.ServiceGenerator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 30.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class NotificationInfo
{
    @get: JsonProperty("ID")
    var id: Int=0
    @get: JsonProperty("NOTICE_DATA")
    lateinit var noticeData: NoticeData
    @get: JsonProperty("NOTIFY_READ")
    lateinit var notifyRead: String
    @get: JsonProperty("DATE_CREATE")
    lateinit var dateCreate: String

    @JsonIgnoreProperties(ignoreUnknown = true)
    class NoticeData {
        @get: JsonProperty("TYPE")
        var type: Int=0
        @get: JsonProperty("FROM")
        var from: Int=0
        @get: JsonProperty("TO")
        var to: Int=0
        @get: JsonProperty("MESSAGE")
        lateinit var message: String
        @get: JsonProperty("FROM_IMAGE")
        var userFromImage: String?=null

        fun getFullUserFromAvatarUrl(): String?
                = if(userFromImage!=null) ServiceGenerator.SITE_URL+userFromImage else null
    }

}
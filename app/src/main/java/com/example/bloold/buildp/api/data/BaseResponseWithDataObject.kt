package com.example.bloold.buildp.api.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 18.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class BaseResponseWithDataObject<T> {
    @get: JsonProperty("USER_ID")
    var userId:Int?=null
    @get: JsonProperty("CODE")
    var code = 0
    @get: JsonProperty("MESSAGE")
    var message:String?=null
    @get: JsonProperty("DATA")
    var data:DataObject<T>?=null
}
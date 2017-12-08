package com.example.bloold.buildp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 28.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ObjectCategory()
{
    @get: JsonProperty("ID")
    var id:String?=null
    @get: JsonProperty("VALUE")
    var value:String=""
    @get: JsonProperty("CODE")
    var code:String=""

    override fun toString(): String {
        return value
    }
}
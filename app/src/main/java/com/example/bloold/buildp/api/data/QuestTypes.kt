package com.example.bloold.buildp.api.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 18.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class QuestTypes:BaseResponseWithoutData() {
    @get: JsonProperty("DATA")
    var data:QuestTypes?=null
    class QuestTypes
    {
        @get: JsonProperty("ITEMS")
        var items:Map<String,String>?=null
    }
}
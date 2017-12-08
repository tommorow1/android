package com.example.bloold.buildp.api.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class CurrentCondition
{
    @get: JsonProperty("CONDITION")
    var conditionId:Int?=null
}
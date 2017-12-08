package com.example.bloold.buildp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by bloold on 20.10.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class ConditionMark
{
    @get: JsonProperty("ID")
    var id: Int=-1
    @get: JsonProperty("VALUE")
    var value: String? = null

    override fun toString(): String = value?:""
}
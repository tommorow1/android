package com.example.bloold.buildp.model

import com.example.bloold.buildp.api.ServiceGenerator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 18.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class FavouriteObject
{
    @get: JsonProperty("OBJECT_ID")
    var id = 0
    @get: JsonProperty("NAME")
    var name: String =""
    @get: JsonProperty("ADDRESS")
    var address: String =""
    @get: JsonProperty("PICTURE")
    var picture: String?=null

    fun getImageLink(): String?
            = if(picture!=null) ServiceGenerator.SITE_URL+picture else null

}
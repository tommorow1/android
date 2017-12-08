package com.example.bloold.buildp.model

import com.example.bloold.buildp.api.data.CatalogObject
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 30.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Quest
{
    @get: JsonProperty("ID")
    var id: Int=0
    @get: JsonProperty("NAME")
    lateinit var name: String
    @get: JsonProperty("DETAIL_PICTURE")
    lateinit var pictureDetail: PictureDetail
    @get: JsonProperty("PREVIEW_TEXT")
    lateinit var previewText: String
    @get: JsonProperty("DETAIL_TEXT")
    var detailText: String?=null
    @get: JsonProperty("PROPERTY_STATUS")
    lateinit var status: String
    @get: JsonProperty("PROPERTY_POINTS")
    var points: Double = 0.0
    @get: JsonProperty("PROPERTY_PARTICIPANTS")
    var participants: Double = 0.0
    @get: JsonProperty("PROPERTY_TYPE")
    lateinit var type: String
    @get: JsonProperty("PARTICIPATE")
    var isParticipate: Boolean=false
    @get: JsonProperty("OBJECTS_DATA")
    var objects: ArrayList<CatalogObject>?=null
    @get: JsonProperty("PARTICIPANTS_DATA")
    var participantsData: ArrayList<Participant>?=null
}
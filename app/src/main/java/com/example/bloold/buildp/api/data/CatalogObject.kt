package com.example.bloold.buildp.api.data

import android.os.Parcel
import android.os.Parcelable
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.model.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.android.gms.maps.model.LatLng

/**
 * Created by sagus on 18.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class CatalogObject() : Parcelable {
    @get: JsonProperty("ID")
    var id = 0
    @get: JsonProperty("NAME")
    var name: String =""
    @get: JsonProperty("CODE")
    var code: String =""
    @get: JsonProperty("PROPERTY_ADDRESS")
    var propertyAddress: String?=null
    @get: JsonProperty("PREVIEW_TEXT")
    var previewText: String?=null
    @get: JsonProperty("DETAIL_PAGE_URL")
    var detailPageUrl: String?=null
    @get: JsonProperty("DETAIL_PICTURE")
    var detailPicture: DetailPictureModel?=null
    @get: JsonProperty("PHOTOS_DATA")
    var photosData: ArrayList<PhotoModel>?=null
    @get: JsonProperty("DOCS_DATA")
    var docsData: Array<DocModel>?=null
    @get: JsonProperty("VIDEO_DATA")
    var videoData: Array<VideoModel>?=null
    @get: JsonProperty("AUDIO_DATA")
    var audioData: Array<AudioModel>?=null
    @get: JsonProperty("PUBLICATIONS_DATA")
    var publicationsData: Array<PublicationsModel>?=null
    @get: JsonProperty("PROPERTY_MAP")
    var propertyMap: String?=null
    @get: JsonProperty("IS_FAVORITE")
    var isFavourite: Boolean? = false
    @get: JsonProperty("PROPERTY_CONDITION")
    var condition: String? = null //Состояние объекта
    @get: JsonProperty("PROPERTY_UNESCO")
    var isUnesco: String? = null
    @get: JsonProperty("PROPERTY_VALUABLE")
    var isValuable: String? = null
    @get: JsonProperty("PROPERTY_TYPE")
    var propertyType: String? = null
    @get: JsonProperty("PROPERTY_VALUE_CATEGORY")
    var propertyCategory: String? = null
    @get: JsonProperty("PROPERTY_LAT")
    var latitude: Double? = null
    @get: JsonProperty("PROPERTY_LNG")
    var longitude: Double? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString()
        code = parcel.readString()
        propertyAddress = parcel.readString()
        previewText = parcel.readString()
        detailPageUrl = parcel.readString()
        detailPicture = parcel.readParcelable(DetailPictureModel::class.java.classLoader)
        photosData = parcel.createTypedArrayList(PhotoModel)
        docsData = parcel.createTypedArray(DocModel)
        videoData = parcel.createTypedArray(VideoModel)
        audioData = parcel.createTypedArray(AudioModel)
        publicationsData = parcel.createTypedArray(PublicationsModel)
        propertyMap = parcel.readString()
        isFavourite = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        condition = parcel.readString()
        isUnesco = parcel.readString()
        isValuable = parcel.readString()
        propertyType = parcel.readString()
        propertyCategory = parcel.readString()
        latitude = parcel.readValue(Double::class.java.classLoader) as? Double
        longitude = parcel.readValue(Double::class.java.classLoader) as? Double
    }

    fun getLocation():LatLng?
    {
        propertyMap?.let {
            val coords=it.split(",")
            if(coords.size==2)
            {
                try {
                    return LatLng(coords[0].toDouble(), coords[1].toDouble())
                }
                catch (ex:Exception)
                {
                    ex.printStackTrace()
                }
            }
        }
        return null
    }
    /*
    @get: JsonProperty("DOCS_DATA")
    var docsData: Object =""
    @get: JsonProperty("PUBLICATIONS_DATA")
    var publicationsData: Object =""
    @get: JsonProperty("VIDEO_DATA")
    var videoData: Object =""
    @get: JsonProperty("AUDIO_DATA")
    var audioData: Object =""
    @get: JsonProperty("IS_FAVORITE")
    var isFavourite: Object =""*/
    fun getFullLink(): String?
            = if(detailPageUrl!=null) ServiceGenerator.SITE_URL+detailPageUrl else null

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeString(propertyAddress)
        parcel.writeString(previewText)
        parcel.writeString(detailPageUrl)
        parcel.writeParcelable(detailPicture, flags)
        parcel.writeTypedList(photosData)
        parcel.writeTypedArray(docsData, flags)
        parcel.writeTypedArray(videoData, flags)
        parcel.writeTypedArray(audioData, flags)
        parcel.writeTypedArray(publicationsData, flags)
        parcel.writeString(propertyMap)
        parcel.writeValue(isFavourite)
        parcel.writeString(condition)
        parcel.writeString(isUnesco)
        parcel.writeString(isValuable)
        parcel.writeString(propertyType)
        parcel.writeString(propertyCategory)
        parcel.writeValue(latitude)
        parcel.writeValue(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CatalogObject> {
        override fun createFromParcel(parcel: Parcel): CatalogObject {
            return CatalogObject(parcel)
        }

        override fun newArray(size: Int): Array<CatalogObject?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable
import com.example.bloold.buildp.api.ServiceGenerator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by bloold on 20.10.17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PublicationsModel() : Parcelable, NameCodeInterface {
    @get: JsonProperty("NAME")
    var name: String? = null
    @get: JsonProperty("PROPERTY_FILE")
    var propertyFile: PropertyFile? = null

    override fun getDocName(): String? = name
    override fun getDocCode(): String? = null
    override fun getSrcFile(): String? = if(!propertyFile?.src.isNullOrEmpty()) ServiceGenerator.SITE_URL+propertyFile?.src else null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PublicationsModel> {
        override fun createFromParcel(parcel: Parcel): PublicationsModel = PublicationsModel(parcel)

        override fun newArray(size: Int): Array<PublicationsModel?> = arrayOfNulls(size)
    }
}
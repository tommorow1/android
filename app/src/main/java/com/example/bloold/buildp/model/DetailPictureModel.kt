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
class DetailPictureModel(): Parcelable {
    @get: JsonProperty("ID")
    var id: Long=-1
    @get: JsonProperty("SRC")
    lateinit var src: String

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        src = parcel.readString()
    }
    fun fullPath() = ServiceGenerator.SITE_URL+src

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(src)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DetailPictureModel> {
        override fun createFromParcel(parcel: Parcel): DetailPictureModel = DetailPictureModel(parcel)

        override fun newArray(size: Int): Array<DetailPictureModel?> = arrayOfNulls(size)
    }
}
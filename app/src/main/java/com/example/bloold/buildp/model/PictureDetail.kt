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
class PictureDetail() : Parcelable
{
    @get: JsonProperty("ID")
    var id: Long=-1
    @get: JsonProperty("SRC")
    var src: String?=null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        src = parcel.readString()
    }

    fun fullPath():String?
            = if(src!=null) ServiceGenerator.SITE_URL+src
            else null

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(src)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PictureDetail> {
        override fun createFromParcel(parcel: Parcel): PictureDetail {
            return PictureDetail(parcel)
        }

        override fun newArray(size: Int): Array<PictureDetail?> {
            return arrayOfNulls(size)
        }
    }
}
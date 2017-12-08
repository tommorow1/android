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
class AudioModel(): Parcelable {
    var id: Long=-1
    var name: String? = null
    lateinit var src: String

    fun fullPath() = if(id!=-1L) ServiceGenerator.SITE_URL+src else src

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        src = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(src)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AudioModel> {
        override fun createFromParcel(parcel: Parcel): AudioModel {
            return AudioModel(parcel)
        }

        override fun newArray(size: Int): Array<AudioModel?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable
import com.example.bloold.buildp.api.ServiceGenerator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by bloold on 20.10.17.
 */
class PhotoModel(): Parcelable {
    var id: Long=-1
    var name: String? = null
    lateinit var src: String

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        src = parcel.readString()
    }
    fun fullPath() = ServiceGenerator.SITE_URL+src

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(src)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PhotoModel> {
        override fun createFromParcel(parcel: Parcel): PhotoModel = PhotoModel(parcel)

        override fun newArray(size: Int): Array<PhotoModel?> = arrayOfNulls(size)
    }
}
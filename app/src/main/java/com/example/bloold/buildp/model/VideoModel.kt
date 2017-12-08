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
class VideoModel(): Parcelable {
    @get: JsonProperty("NAME")
    var name: String? = null
    @get: JsonProperty("CODE")
    var youtubeCode: String? =null

    fun getYoutubeLink():String
    {
        return "https://www.youtube.com/watch?v="+youtubeCode
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(name)
        parcel.writeString(youtubeCode)
    }

    override fun describeContents(): Int = 0

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        youtubeCode = parcel.readString()
    }

    companion object CREATOR : Parcelable.Creator<VideoModel> {
        override fun createFromParcel(parcel: Parcel): VideoModel = VideoModel(parcel)

        override fun newArray(size: Int): Array<VideoModel?> = arrayOfNulls(size)
    }
}
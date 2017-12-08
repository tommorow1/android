package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by bloold on 18.10.17.
 */
class CatalogObjectsModel() : Parcelable, BaseModel() {
    var property_address: String? = null
    var preview_text: String? = null
    var src: String? = null
    var photos: ArrayList<PhotoModel>? = null
    var docs: Array<DocModel>? = null
    var publications: Array<DocModel>? = null
    var videos: Array<VideoModel>? = null
    var audios: Array<AudioModel>? = null
    var isFavorite: Boolean? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        id = parcel.readString()
        property_address = parcel.readString()
        preview_text = parcel.readString()
        src = parcel.readString()
        photos = ArrayList<PhotoModel>()
        parcel.readTypedList(photos, PhotoModel.CREATOR)
        parcel.readParcelableArray(DocModel::class.java.classLoader)
        parcel.readParcelableArray(DocModel::class.java.classLoader)
        parcel.readParcelableArray(VideoModel::class.java.classLoader)
        parcel.readParcelableArray(AudioModel::class.java.classLoader)
        isFavorite= parcel.readString().toBoolean()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
        parcel.writeString(property_address)
        parcel.writeString(preview_text)
        parcel.writeString(src)
        parcel.writeTypedList(photos)
        parcel.writeParcelableArray(docs, 0)
        parcel.writeParcelableArray(publications, 0)
        parcel.writeParcelableArray(videos, 0)
        parcel.writeParcelableArray(audios, 0)
        parcel.writeString(isFavorite.toString())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CatalogObjectsModel> {
        override fun createFromParcel(parcel: Parcel): CatalogObjectsModel {
            return CatalogObjectsModel(parcel)
        }

        override fun newArray(size: Int): Array<CatalogObjectsModel?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by bloold on 21.10.17.
 */
class AllFiltersModel():
        FilterModel<HightFilterModelLevel>(){

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(items, HightFilterModelLevel.CREATOR)
        name = parcel.readString()
        id = parcel.readString()
        depth = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(depth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllFiltersModel> {
        override fun createFromParcel(parcel: Parcel): AllFiltersModel {
            return AllFiltersModel(parcel)
        }

        override fun newArray(size: Int): Array<AllFiltersModel?> {
            return arrayOfNulls(size)
        }
    }
}

class HightFilterModelLevel():
        FilterModel<SubFilterModelLevel>(){

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(items, SubFilterModelLevel.CREATOR)
        name = parcel.readString()
        id = parcel.readString()
        depth = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(depth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HightFilterModelLevel> {
        override fun createFromParcel(parcel: Parcel): HightFilterModelLevel {
            return HightFilterModelLevel(parcel)
        }

        override fun newArray(size: Int): Array<HightFilterModelLevel?> {
            return arrayOfNulls(size)
        }
    }
}

class SubFilterModelLevel():
        FilterModel<CatalogObjectsModel>() {

    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(items, CatalogObjectsModel.CREATOR)
        name = parcel.readString()
        id = parcel.readString()
        depth = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(depth)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SubFilterModelLevel> {
        override fun createFromParcel(parcel: Parcel): SubFilterModelLevel {
            return SubFilterModelLevel(parcel)
        }

        override fun newArray(size: Int): Array<SubFilterModelLevel?> {
            return arrayOfNulls(size)
        }
    }
}

abstract class FilterModel<T: Parcelable>(): Parcelable, BaseModel() {

    var items: ArrayList<T>? = null
    var depth: String? = null

    fun get(pos: Int): T? {
        return items?.get(pos)
    }

    fun size(): Int {
        return items?.size ?: 0
    }
}

open class BaseModel() : Parcelable {
    @SerializedName("ID")
    var id: String? = null

    @SerializedName("NAME")
    var name: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BaseModel> {
        override fun createFromParcel(parcel: Parcel): BaseModel = BaseModel(parcel)

        override fun newArray(size: Int): Array<BaseModel?> = arrayOfNulls(size)
    }

}
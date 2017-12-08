package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 28.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Category() : Parcelable {
    @get: JsonProperty("ID")
    var id:String?=null
    @get: JsonProperty("NAME")
    var name:String=""
    @get: JsonProperty("CODE")
    var code:String=""
    @get: JsonProperty("CHILD")
    var children:Array<Category>?=null

    constructor(name: String, id:String?=null) : this()
    {
        this.name=name
        this.id=id
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        code = parcel.readString()
        children = parcel.createTypedArray(CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(code)
        parcel.writeTypedArray(children, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}
package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by bloold on 29.10.17.
 */
class SortObject() : Parcelable, BaseModel()
{
    var child: Array<SortObject>? = null

    constructor(name: String, id:String?) : this()
    {
        this.name=name
        this.id=id
    }

    constructor(parcel: Parcel) : this() {
        child = parcel.createTypedArray(CREATOR)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SortObject> {
        override fun createFromParcel(parcel: Parcel): SortObject = SortObject(parcel)

        override fun newArray(size: Int): Array<SortObject?> = arrayOfNulls(size)
    }
}
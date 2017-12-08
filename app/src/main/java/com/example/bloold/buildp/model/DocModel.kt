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
class DocModel() : Parcelable, NameCodeInterface {
    @get: JsonProperty("NAME")
    var name: String? = null
    @get: JsonProperty("PROPERTY_DOCUMENT_CODE")
    var code: String? = null
    @get: JsonProperty("PROPERTY_FILE")
    var propertyFile: PropertyFile? = null

    override fun getDocName(): String? = name
    override fun getDocCode(): String? = code
    //Если code пустой, то считаем, что файл локальный и возвращаем локальный путь
    override fun getSrcFile(): String? = if(!propertyFile?.src.isNullOrEmpty()) (if(code.isNullOrEmpty()) propertyFile?.src else ServiceGenerator.SITE_URL+propertyFile?.src) else null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        code = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(code)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DocModel> {
        override fun createFromParcel(parcel: Parcel): DocModel = DocModel(parcel)

        override fun newArray(size: Int): Array<DocModel?> = arrayOfNulls(size)
    }
}
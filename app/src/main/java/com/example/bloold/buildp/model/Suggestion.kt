package com.example.bloold.buildp.model

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

/**
 * Created by sagus on 30.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class Suggestion() : Parcelable
{
    @get: JsonProperty("ID")
    var id: Int=0
    @get: JsonProperty("OBJECT_ID")
    var objId: Int = 0
    @get: JsonProperty("OBJECT_NAME")
    lateinit var objectName: String
    @get: JsonProperty("OBJECT_PICTURE")
    var objectPicture: PictureDetail?=null
    @get: JsonProperty("STATE_NAME")
    lateinit var stateName: String
    @get: JsonProperty("DATE_CREATE")
    @get: JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    var dateCreate: Date?=null
    @JsonIgnoreProperties(ignoreUnknown = true)
    @get: JsonProperty("DIFF")
    var diffList: Array<SuggestionDiff>?=null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        objId = parcel.readInt()
        objectName = parcel.readString()
        objectPicture = parcel.readParcelable(PictureDetail::class.java.classLoader)
        stateName = parcel.readString()
        diffList = parcel.createTypedArray(SuggestionDiff)
    }

    class SuggestionDiff() : Parcelable {
        @get: JsonProperty("CODE")
        lateinit var code: String
        @get: JsonProperty("TITLE")
        lateinit var title: String
        @get: JsonProperty("TYPE") //string|info|condition|element|photos|videos|audio|docs|elements|array
        lateinit var type: String
        @get: JsonProperty("VALUE")
        lateinit var diffBody: DiffBody

        constructor(parcel: Parcel) : this() {
            code = parcel.readString()
            title = parcel.readString()
            type = parcel.readString()
            diffBody = parcel.readParcelable(DiffBody::class.java.classLoader)
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        class DiffBody() : Parcelable
        {
            var wasValue: String?=null
            var newValue: String?=null

            constructor(parcel: Parcel) : this() {
                wasValue = parcel.readString()
                newValue = parcel.readString()
            }

            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(wasValue)
                parcel.writeString(newValue)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<DiffBody> {
                override fun createFromParcel(parcel: Parcel): DiffBody {
                    return DiffBody(parcel)
                }

                override fun newArray(size: Int): Array<DiffBody?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(code)
            parcel.writeString(title)
            parcel.writeString(type)
            parcel.writeParcelable(diffBody, flags)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SuggestionDiff> {
            override fun createFromParcel(parcel: Parcel): SuggestionDiff {
                return SuggestionDiff(parcel)
            }

            override fun newArray(size: Int): Array<SuggestionDiff?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(objId)
        parcel.writeString(objectName)
        parcel.writeParcelable(objectPicture, flags)
        parcel.writeString(stateName)
        parcel.writeTypedArray(diffList, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Suggestion> {
        override fun createFromParcel(parcel: Parcel): Suggestion {
            return Suggestion(parcel)
        }

        override fun newArray(size: Int): Array<Suggestion?> {
            return arrayOfNulls(size)
        }
    }

}
package com.enipro.data.remote.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Education
constructor(@field:SerializedName("school")
            @field:Expose
            var school: String,
            @field:SerializedName("course")
            @field:Expose
            var course: String,
            @field:SerializedName("degree")
            @field:Expose
            var degree: String,
            @field:SerializedName("from")
            @field:Expose
            var from: String,
            @field:SerializedName("to")
            @field:Expose
            var to: String) : Parcelable {

    @SerializedName("_id")
    @Expose
    var _id: ObjectId? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(school)
        parcel.writeString(course)
        parcel.writeString(degree)
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeParcelable(_id, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Education> {
        override fun createFromParcel(parcel: Parcel): Education {
            return Education(parcel)
        }

        override fun newArray(size: Int): Array<Education?> {
            return arrayOfNulls(size)
        }
    }
}

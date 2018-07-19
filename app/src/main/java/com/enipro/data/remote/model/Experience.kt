package com.enipro.data.remote.model


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Experience
constructor(@field:SerializedName("industry")
            @field:Expose
            var industry: String, @field:SerializedName("organisation")
            @field:Expose
            var organisation: String, @field:SerializedName("role")
            @field:Expose
            var role: String, @field:SerializedName("from")
            @field:Expose
            var from: String, @field:SerializedName("to")
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
        parcel.writeString(industry)
        parcel.writeString(organisation)
        parcel.writeString(role)
        parcel.writeString(from)
        parcel.writeString(to)
        parcel.writeParcelable(_id, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Experience> {
        override fun createFromParcel(parcel: Parcel): Experience {
            return Experience(parcel)
        }

        override fun newArray(size: Int): Array<Experience?> {
            return arrayOfNulls(size)
        }
    }
}

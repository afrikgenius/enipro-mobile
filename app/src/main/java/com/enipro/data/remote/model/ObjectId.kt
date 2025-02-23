package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ObjectId
constructor(@field:SerializedName("\$oid")
            @field:Expose
            var oid: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(oid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObjectId> {
        override fun createFromParcel(parcel: Parcel): ObjectId {
            return ObjectId(parcel)
        }

        override fun newArray(size: Int): Array<ObjectId?> {
            return arrayOfNulls(size)
        }
    }
}
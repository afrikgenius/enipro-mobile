package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionTiming(
        @field:SerializedName("days")
        @field:Expose var days: List<String>,
        @field:SerializedName("from_time")
        @field:Expose var from: String,
        @field:SerializedName("to_time")
        @field:Expose var to: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createStringArrayList(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(days)
        parcel.writeString(from)
        parcel.writeString(to)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SessionTiming> {
        override fun createFromParcel(parcel: Parcel): SessionTiming {
            return SessionTiming(parcel)
        }

        override fun newArray(size: Int): Array<SessionTiming?> {
            return arrayOfNulls(size)
        }
    }
}

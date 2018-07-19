package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SessionSchedule
constructor(@field:SerializedName("start_period")
            @field:Expose
            var start_period: String,
            @field:SerializedName("end_period")
            @field:Expose
            var end_period: String,
            @field:SerializedName("timing")
            @field:Expose
            var sessionTiming: SessionTiming) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(SessionTiming::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(start_period)
        parcel.writeString(end_period)
        parcel.writeParcelable(sessionTiming, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SessionSchedule> {
        override fun createFromParcel(parcel: Parcel): SessionSchedule {
            return SessionSchedule(parcel)
        }

        override fun newArray(size: Int): Array<SessionSchedule?> {
            return arrayOfNulls(size)
        }
    }
}

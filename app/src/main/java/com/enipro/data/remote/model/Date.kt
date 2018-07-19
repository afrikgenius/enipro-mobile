package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.joda.time.format.ISODateTimeFormat

data class Date constructor(@field:SerializedName("\$date")
                            @field:Expose
                            var date: String) : Parcelable {
    val utilDate: org.joda.time.LocalDateTime
        get() {
            val parser = ISODateTimeFormat.dateTimeParser()
            return parser.parseDateTime(date).toLocalDateTime()
        }

    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Date> {
        override fun createFromParcel(parcel: Parcel): Date {
            return Date(parcel)
        }

        override fun newArray(size: Int): Array<Date?> {
            return arrayOfNulls(size)
        }
    }
}

package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.joda.time.format.ISODateTimeFormat
import org.parceler.ParcelConstructor

import java.text.ParseException

@org.parceler.Parcel
data class Date @ParcelConstructor constructor(@field:SerializedName("\$date")
                                               @field:Expose
                                               var `_$date`: String) {
    val utilDate: org.joda.time.LocalDateTime
        get() {
            val parser = ISODateTimeFormat.dateTimeParser()
            return parser.parseDateTime(`_$date`).toLocalDateTime()
        }
}

package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@org.parceler.Parcel
class SessionSchedule @ParcelConstructor
constructor(@field:SerializedName("start_period")
            @field:Expose
            var start_period: String, @field:SerializedName("end_period")
            @field:Expose
            var end_period: String, @param:ParcelProperty("timing") @field:SerializedName("timing")
            @field:Expose
            @field:ParcelProperty("timing")
            var sessionTiming: SessionTiming)

package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
data class SessionTiming(
        @field:SerializedName("days")
        @field:Expose var days: List<String>,
        @field:SerializedName("from_time")
        @field:Expose var from: String,
        @field:SerializedName("to_time")
        @field:Expose var to: String)

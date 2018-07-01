package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
data class Document(
        @field:SerializedName("name")
        @field:Expose var name: String,
        @field:SerializedName("url")
        @field:Expose var url: String,
        @field:SerializedName("size")
        @field:Expose var size: Int = 0,
        @field:SerializedName("extension")
        @field:Expose var extension: String)

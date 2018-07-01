package com.enipro.data.remote.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.ParcelConstructor

@org.parceler.Parcel
data class Experience @ParcelConstructor
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
            var to: String) {

    @SerializedName("_id")
    @Expose
    var _id: ObjectId? = null
}

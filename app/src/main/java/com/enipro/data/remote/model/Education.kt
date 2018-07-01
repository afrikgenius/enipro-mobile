package com.enipro.data.remote.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.ParcelConstructor

@org.parceler.Parcel
data class Education @ParcelConstructor
constructor(@field:SerializedName("school")
            @field:Expose
            var school: String, @field:SerializedName("course")
            @field:Expose
            var course: String, @field:SerializedName("degree")
            @field:Expose
            var degree: String, @field:SerializedName("from")
            @field:Expose
            var from: String, @field:SerializedName("to")
            @field:Expose
            var to: String) {

    @SerializedName("_id")
    @Expose
    var _id: ObjectId? = null
}

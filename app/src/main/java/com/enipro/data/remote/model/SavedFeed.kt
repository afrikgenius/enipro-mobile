package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
data class SavedFeed(@field:SerializedName("id")
                     @field:Expose var feedId: String) {

    @SerializedName("created_at")
    @Expose
    var createdAt: Date? = null

}

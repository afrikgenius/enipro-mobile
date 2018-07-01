package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
data class UserConnection(
        @field:SerializedName("id")
        @field:Expose var userId: String) {

    @SerializedName("created_at")
    @Expose
    var createdAt: Date? = null
}

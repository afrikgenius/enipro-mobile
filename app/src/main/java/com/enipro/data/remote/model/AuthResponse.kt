package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AuthResponse {
    @SerializedName("token")
    @Expose
    var token: String? = null
    @SerializedName("user_id")
    @Expose
    var userId: String? = null
}
package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaginationLinks {

    @SerializedName("first")
    @Expose
    var first: String? = null

    @SerializedName("last")
    @Expose
    var last: String? = null

    @SerializedName("next")
    @Expose
    var next: String? = null

    @SerializedName("prev")
    @Expose
    var prev: String? = null
}
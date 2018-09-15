package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Pagination {

    @SerializedName("page")
    @Expose
    var page: Int? = null

    @SerializedName("per_page")
    @Expose
    var per_page: Int? = null

    @SerializedName("_links")
    @Expose
    var links: PaginationLinks? = null
}
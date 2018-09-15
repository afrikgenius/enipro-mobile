package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * A paginated response captures a response of an api endpoint in enipro rest service that returns
 * paginated data in form of T.
 */
class PaginatedResponse<T> {

    @SerializedName("result")
    @Expose
    var result: MutableList<T>? = null

    @SerializedName("pagination")
    @Expose
    var pagination: Pagination? = null
}
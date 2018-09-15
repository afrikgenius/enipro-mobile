package com.enipro.repository

import android.content.Context
import com.enipro.R

class PageHandler(val context: Context) {

    val reference by lazy {
        context.getSharedPreferences(context.getString(R.string.last_requested_name_pref), Context.MODE_PRIVATE)
    }

    fun getLastPageNumber(): Long {
        return reference.getLong(context.getString(R.string.lastRequestedPage), 0)
    }


    fun setPage(pageNumber: Long) {
        reference.edit().putLong(context.getString(R.string.lastRequestedPage), pageNumber).apply()
    }
}
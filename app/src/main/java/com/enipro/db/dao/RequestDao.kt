package com.enipro.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import com.enipro.data.remote.model.Request

@Dao
interface RequestDao {


    @Query("SELECT * from requests where _id = :request_id")
    fun getRequest(request_id: String): Request

    @Insert
    fun insertRequest(request: Request)

    @Update
    fun updateRequest(request: Request)

    @Delete
    fun deleteRequest(request: Request)
}

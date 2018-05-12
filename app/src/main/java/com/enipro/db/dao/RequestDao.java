package com.enipro.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.enipro.data.remote.model.Request;

@Dao
public interface RequestDao {


    @Query("SELECT * from requests where _id = :request_id")
    Request getRequest(String request_id);

    @Insert
    void insertRequest(Request request);

    @Update
    void updateRequest(Request request);

    @Delete
    void deleteRequest(Request request);
}

package com.enipro.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import com.enipro.data.remote.model.Feed

/**
 * Data Access object used to retrieve information from the sqlite database.
 */

@Dao
interface FeedDao {

    @get:Query("SELECT * FROM feeds")
    val feeds: List<Feed>

    @Query("SELECT * FROM feeds where _id = :feed_id")
    fun getFeed(feed_id: String): Feed

    @Insert
    fun insertFeed(feed: Feed)

    @Query("DELETE FROM feeds")
    fun deleteAll()

    @Delete
    fun deleteFeed(feed: Feed)
}
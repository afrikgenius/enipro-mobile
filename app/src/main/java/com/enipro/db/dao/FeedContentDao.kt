package com.enipro.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import com.enipro.data.remote.model.FeedContent

@Dao
interface FeedContentDao {

    @Query("SELECT * FROM feed_content WHERE content_id = :content_id")
    fun getFeedContent(content_id: Int): FeedContent

    @Insert
    fun insertFeedContent(feedContent: FeedContent)

    @Delete
    fun deleteFeedContent(feedContent: FeedContent)

    @Query("DELETE FROM feed_content")
    fun deleteAll()
}
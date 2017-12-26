package com.enipro.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.enipro.data.remote.model.FeedContent;

@Dao
public interface FeedContentDao {

    @Query("SELECT * FROM feed_content WHERE content_id = :content_id")
    FeedContent getFeedContent(int content_id);

    @Insert
    void insertFeedContent(FeedContent feedContent);

    @Delete
    void deleteFeedContent(FeedContent feedContent);

    @Query("DELETE FROM feed_content")
    void deleteAll();
}
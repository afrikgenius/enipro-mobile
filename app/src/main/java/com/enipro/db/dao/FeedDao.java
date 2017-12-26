package com.enipro.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.enipro.data.remote.model.Feed;

import java.util.List;

/**
 * Data Access object used to retrieve information from the sqlite database.
 */

@Dao
public interface FeedDao {

    @Query("SELECT * FROM feeds")
    List<Feed> getFeeds();

    @Query("SELECT * FROM feeds where _id = :feed_id")
    Feed getFeed(String feed_id);

    @Insert
    void insertFeed(Feed feed);

    @Query("DELETE FROM feeds")
    void deleteAll();

    @Delete
    void deleteFeed(Feed feed);

//    @Query("SELECT * FROM feed_comments where _id = :feed_id")
//    List<FeedComment> getFeedComments(String feed_id);
}
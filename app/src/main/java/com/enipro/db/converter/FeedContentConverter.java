package com.enipro.db.converter;


import android.arch.persistence.room.TypeConverter;

import com.enipro.Application;
import com.enipro.data.remote.model.FeedContent;

public class FeedContentConverter {

    @TypeConverter
    public static int toContentId(FeedContent feedContent){
        // Persist feed content and return content id
        Application.getDbInstance().feedContent().insertFeedContent(feedContent); // Insert feed into database.
        return feedContent.getContent_id();
    }

    @TypeConverter
    public static FeedContent toFeedContent(int content_id){
        // Use the content id to get feed content from the database.
        return Application.getDbInstance().feedContent().getFeedContent(content_id);
    }
}

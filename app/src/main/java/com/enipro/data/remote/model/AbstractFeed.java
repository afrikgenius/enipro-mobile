package com.enipro.data.remote.model;

import java.util.List;

public interface AbstractFeed {

    ObjectId get_id();
    String getUser();
    int getLikes();
    boolean getModerated();
    FeedContent getContent();
    List<FeedComment> getComments();
    List<String> getTags();
    Date getCreated_at();
    Date getUpdated_at();
}
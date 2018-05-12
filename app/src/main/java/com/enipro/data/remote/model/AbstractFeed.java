package com.enipro.data.remote.model;

import java.util.List;

public interface AbstractFeed {

    ObjectId get_id();
    String getUser();
    List<UserConnection> getLikes();
    boolean getModerated();
    FeedContent getContent();
    List<FeedComment> getComments();
    List<String> getTags();
    PremiumDetails getPremiumDetails();
    Date getCreated_at();
    Date getUpdated_at();
}
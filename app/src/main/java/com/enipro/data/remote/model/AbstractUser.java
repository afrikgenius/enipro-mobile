package com.enipro.data.remote.model;


import java.util.List;

public interface AbstractUser {

    ObjectId get_id();
    String getFirstName();
    String getLastName();
    String getPassword();
    String getEmail();
    boolean getActive();
    String getUserType();
    String getHeadline();
    String getCountry();
    String getMobile();
    String getBio();
    String getAvatar();
    String getAvatar_cover();
    Date getCreated_at();
    Date getUpdated_at();
    String getAchievements();
    String getFirebaseToken();
    String getFirebaseUID();
    List<UserConnection> getCircle();
    List<UserConnection> getNetwork();
    List<String> getInterests();
    List<UserConnection> getChats();
    List<SavedFeed> getSavedFeeds();
    List<Education> getEducation();
    List<Experience> getExperiences();
}

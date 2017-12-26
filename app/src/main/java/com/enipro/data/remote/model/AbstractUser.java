package com.enipro.data.remote.model;


public interface AbstractUser {

    ObjectId get_id();
    String getFirstName();
    String getLastName();
    String getPassword();
    String getEmail();
    boolean getActive();
    String getUserType();
    String getAvatar();
    String getAvatar_cover();
    Date getCreated_at();
    Date getUpdated_at();
}

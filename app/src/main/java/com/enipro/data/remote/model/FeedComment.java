package com.enipro.data.remote.model;


import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
@Entity(tableName = "feed_comments")
public class FeedComment {

    @SerializedName("_id")
    @Expose
    public ObjectId _id;

    @SerializedName("comment_user_id")
    @Expose
    public String user;

    @SerializedName("comment")
    @Expose
    public String comment;

    @SerializedName("comment_image")
    @Expose
    public String comment_image;

    @SerializedName("created_at")
    @Expose
    public Date created_at;

    @SerializedName("updated_at")
    @Expose
    public Date updated_at;

    public FeedComment() {
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public ObjectId get_id() {
        return this._id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment_image(String comment_image) {
        this.comment_image = comment_image;
    }

    public String getComment_image() {
        return this.comment_image;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }


    /**
     * Returns a name used to save the comment image file.
     *
     * @return
     */
    public String getImageName() {
        return getUser() + new java.util.Random().nextLong() + new java.util.Date();
    }
}

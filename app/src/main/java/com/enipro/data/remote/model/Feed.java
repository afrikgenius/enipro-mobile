package com.enipro.data.remote.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a news feed item in the application.
 */

@org.parceler.Parcel
@Entity(tableName = "feeds")
public class Feed implements AbstractFeed {

    @SerializedName("_id")
    @Expose
    @PrimaryKey
    @NonNull
    public ObjectId _id;

    @SerializedName("user_id")
    @Expose
    public String user;

    @SerializedName("likes")
    @Expose
    public List<UserConnection> likes = new ArrayList<>();

    @SerializedName("moderated")
    @Expose
    public boolean moderated;

    @SerializedName("content")
    @Expose
    public FeedContent content;

    @SerializedName("comments")
    @Expose
    public List<FeedComment> comments = new ArrayList<>();

    @SerializedName("tags")
    @Expose
    public List<String> tags;

    @SerializedName("premium")
    @Expose
    public PremiumDetails premiumDetails;

    @SerializedName("created_at")
    @Expose
    public Date created_at;

    @SerializedName("updated_at")
    @Expose
    public Date updated_at;

    @Override
    public ObjectId get_id() {
        return this._id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public List<UserConnection> getLikes() {
        return this.likes;
    }

    public void setLikes(List<UserConnection> likes) {
        this.likes = likes;
    }

    @Override
    public boolean getModerated() {
        return this.moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    @Override
    public FeedContent getContent() {
        return this.content;
    }

    public void setContent(FeedContent content) {
        this.content = content;
    }

    @Override
    public List<FeedComment> getComments() {
        return this.comments;
    }

    public void setComments(List<FeedComment> comments) {
        this.comments = comments;
    }

    @Override
    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public PremiumDetails getPremiumDetails() {
        return this.premiumDetails;
    }

    public void setPremiumDetails(PremiumDetails premiumDetails) {
        this.premiumDetails = premiumDetails;
    }

    @Override
    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public void addComment(FeedComment comment) {
        this.comments.add(comment);
    }

    public Feed() {
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }

    public Feed(AbstractFeed feed) {
        _id = feed.get_id();
        user = feed.getUser();
        likes = feed.getLikes();
        moderated = feed.getModerated();
        content = feed.getContent();
        comments = feed.getComments();
        tags = feed.getTags();
        premiumDetails = feed.getPremiumDetails();
        created_at = feed.getCreated_at();
        updated_at = feed.getUpdated_at();
    }

//    public Feed(Parcel in) {
//        this._id = in.readParcelable(getClass().getClassLoader());
//        this.user = in.readString();
//        in.readList(this.likes, UserConnection.class.getClassLoader());
//        this.moderated = in.readByte() != 0;
//        this.content = in.readParcelable(getClass().getClassLoader());
//        this.comments = new ArrayList<>();
//        in.readList(this.comments, FeedComment.class.getClassLoader());
//        this.tags = new ArrayList<>();
//        in.readList(this.tags, List.class.getClassLoader());
//        this.updated_at = in.readParcelable(getClass().getClassLoader());
//        this.premiumDetails = in.readParcelable(getClass().getClassLoader());
//        this.created_at = in.readParcelable(getClass().getClassLoader());
//    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(_id, flags);
//        dest.writeString(user);
//        dest.writeList(likes);
//        dest.writeByte((byte) (moderated ? 1 : 0));
//        dest.writeParcelable(content, flags);
//        dest.writeList(comments);
//        dest.writeList(tags);
//        dest.writeParcelable(updated_at, flags);
//        dest.writeParcelable(premiumDetails, flags);
//        dest.writeParcelable(created_at, flags);
//    }
//
//    public static final Parcelable.Creator<Feed> CREATOR = new Parcelable.Creator<Feed>() {
//
//        @Override
//        public Feed createFromParcel(Parcel source) {
//            return new Feed(source);
//        }
//
//
//        @Override
//        public Feed[] newArray(int size) {
//            return new Feed[size];
//        }
//    };
}
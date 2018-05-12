package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
public class SavedFeed {

    @SerializedName("id")
    @Expose
    public String feedId;

    @SerializedName("created_at")
    @Expose
    public Date createdAt;

    public SavedFeed() {
    }

    public SavedFeed(String feedId) {
        this.feedId = feedId;
    }

//    public SavedFeed(Parcel in) {
//        feedId = in.readString();
//        createdAt = in.readParcelable(getClass().getClassLoader());
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(feedId);
//        dest.writeParcelable(createdAt, flags);
//    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

//    public static final Parcelable.Creator<SavedFeed> CREATOR = new Parcelable.Creator<SavedFeed>() {
//
//        @Override
//        public SavedFeed createFromParcel(Parcel source) {
//            return new SavedFeed(source);
//        }
//
//
//        @Override
//        public SavedFeed[] newArray(int size) {
//            return new SavedFeed[size];
//        }
//    };
}

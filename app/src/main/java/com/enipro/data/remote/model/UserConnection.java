package com.enipro.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
public class UserConnection {

    @SerializedName("id")
    @Expose
    public String userId;

    @SerializedName("created_at")
    @Expose
    public Date createdAt;

    public UserConnection() {
    }

    public UserConnection(String userId) {
        this.userId = userId;
    }

//    public UserConnection(Parcel in) {
//        userId = in.readString();
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
//        dest.writeString(userId);
//        dest.writeParcelable(createdAt, flags);
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

//    public static final Parcelable.Creator<UserConnection> CREATOR = new Parcelable.Creator<UserConnection>() {
//
//        @Override
//        public UserConnection createFromParcel(Parcel source) {
//            return new UserConnection(source);
//        }
//
//
//        @Override
//        public UserConnection[] newArray(int size) {
//            return new UserConnection[size];
//        }
//    };
}

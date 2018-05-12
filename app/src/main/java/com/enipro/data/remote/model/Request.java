package com.enipro.data.remote.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
@Entity(tableName = "requests")
@IgnoreExtraProperties
public class Request {

    @SerializedName("_id")
    @Expose
    @NonNull
    @PrimaryKey
    public ObjectId _id;

    @SerializedName("sender")
    @Expose
    public String sender;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("category")
    @Expose
    public String category;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("recipient")
    @Expose
    public String recipient;

    @SerializedName("created_at")
    @Expose
    @Exclude
    public Date created_at;

    @SerializedName("updated_at")
    @Expose
    @Exclude
    public Date updated_at;

    @SerializedName("schedule")
    @Expose
    public SessionSchedule schedule;

    public Request() {
    }

    public Request(String sender, String type, String category) {
        this.sender = sender;
        this.type = type;
        this.category = category;
    }

//    public Request(Parcel in) {
//        this._id = in.readParcelable(getClass().getClassLoader());
//        this.sender = in.readString();
//        this.type = in.readString();
//        this.category = in.readString();
//        this.status = in.readString();
//        this.recipient = in.readString();
//        this.created_at = in.readParcelable(getClass().getClassLoader());
//        this.updated_at = in.readParcelable(getClass().getClassLoader());
//        this.schedule = in.readParcelable(getClass().getClassLoader());
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeParcelable(_id, flags);
//        dest.writeString(sender);
//        dest.writeString(type);
//        dest.writeString(category);
//        dest.writeString(status);
//        dest.writeString(recipient);
//        dest.writeParcelable(created_at, flags);
//        dest.writeParcelable(updated_at, flags);
//        dest.writeParcelable(schedule, flags);
//    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public SessionSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(SessionSchedule schedule) {
        this.schedule = schedule;
    }

//    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
//
//        @Override
//        public Request createFromParcel(Parcel source) {
//            return new Request(source);
//        }
//
//        @Override
//        public Request[] newArray(int size) {
//            return new Request[size];
//        }
//    };
}

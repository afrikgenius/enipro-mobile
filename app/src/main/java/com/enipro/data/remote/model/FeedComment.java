package com.enipro.data.remote.model;


import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "feed_comments")
public class FeedComment implements Parcelable {

    @SerializedName("_id")
    @Expose
    private ObjectId _id;

    @SerializedName("user")
    @Expose
    private String user;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    public FeedComment(Parcel in){
        this._id = in.readParcelable(getClass().getClassLoader());
        this.user = in.readString();
        this.comment = in.readString();
        this.created_at = in.readParcelable(getClass().getClassLoader());
        this.updated_at = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(_id, flags);
        dest.writeString(user);
        dest.writeString(comment);
        dest.writeParcelable(created_at, flags);
        dest.writeParcelable(updated_at, flags);
    }

    public void set_id(ObjectId _id){ this._id = _id; }
    public ObjectId get_id(){ return this._id; }

    public void setUser(String user){ this.user = user; }
    public String getUser(){ return this.user; }

    public void setComment(String comment){ this.comment = comment; }
    public String getComment(){ return this.comment; }

    public void setCreated_at(Date created_at){ this.created_at = created_at; }
    public Date getCreated_at(){ return this.created_at; }

    public void setUpdated_at(Date updated_at){ this.updated_at = updated_at; }
    public Date getUpdated_at(){ return this.updated_at; }

    public static final Parcelable.Creator<FeedComment> CREATOR = new Parcelable.Creator<FeedComment>() {

        @Override
        public FeedComment createFromParcel(Parcel source) {
            return new FeedComment(source);
        }


        @Override
        public FeedComment[] newArray(int size) {
            return new FeedComment[size];
        }
    };
}

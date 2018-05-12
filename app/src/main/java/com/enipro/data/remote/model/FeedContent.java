package com.enipro.data.remote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.enipro.Application;
import com.enipro.db.EniproDatabase;
import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@org.parceler.Parcel
@Entity(tableName = "feed_content")
public class FeedContent {

    @PrimaryKey
    public int content_id = new Random().nextInt();

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("image")
    @Expose
    public String image;

    @SerializedName("video")
    @Expose
    public String video;

    @SerializedName("doc")
    @Expose
    public Document doc;

    public boolean docExists = false;

    /**
     * Type of media item for this feed content.
     */
    public int mediaType;

    public FeedContent() {
    }

//    public FeedContent(Parcel in) {
//        this.content_id = in.readInt();
//        this.text = in.readString();
//        this.image = in.readString();
//        this.video = in.readString();
//        this.doc = in.readParcelable(getClass().getClassLoader());
//        this.mediaType = in.readInt();
//        this.docExists = in.readByte() != 0;
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(content_id);
//        dest.writeString(text);
//        dest.writeString(image);
//        dest.writeString(video);
//        dest.writeParcelable(doc, flags);
//        dest.writeInt(mediaType);
//        dest.writeByte((byte) (docExists ? 1 : 0));
//    }

    public int getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public void setContent_id(int content_id) {
        this.content_id = content_id;
    }

    public int getContent_id() {
        return this.content_id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo() {
        return this.video;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    /**
     * Returns a name used to save the comment image file.
     *
     * @return
     */
    public String getMediaName() {
        return Application.getActiveUser().get_id().get_$oid() + new java.util.Random().nextLong() + new java.util.Date();
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public boolean isDocExists() {
        return docExists;
    }

    public void setDocExists(boolean docExists) {
        this.docExists = docExists;
    }

//    public static final Parcelable.Creator<FeedContent> CREATOR = new Parcelable.Creator<FeedContent>() {
//
//        @Override
//        public FeedContent createFromParcel(Parcel source) {
//            return new FeedContent(source);
//        }
//
//
//        @Override
//        public FeedContent[] newArray(int size) {
//            return new FeedContent[size];
//        }
//    };

    public class MediaType {
        public static final int IMAGE = 0x005;
        public static final int VIDEO = 0x002;
        public static final int DOC = 0x003;
    }
}

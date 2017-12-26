package com.enipro.data.remote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.enipro.db.EniproDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity(tableName = "feed_content")
public class FeedContent implements Parcelable {

    @PrimaryKey
    private int content_id = new Random().nextInt();

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("image")
    @Expose
    private List<String> imageURLS;

    @SerializedName("video")
    @Expose
    private String video;

    public FeedContent(){}

    public FeedContent(Parcel in){
        this.content_id = in.readInt();
        this.text = in.readString();
        this.imageURLS = new ArrayList<>();
//       TODO  in.readList(imageURLS, List.class.getClassLoader());
        this.video = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(content_id);
        dest.writeString(text);
//      TODO   dest.writeList(imageURLS);
        dest.writeString(video);
    }

    public void setContent_id(int content_id){ this.content_id = content_id; }
    public int getContent_id(){ return this.content_id; }

    public void setText(String text){ this.text = text; }
    public String getText(){return this.text; }

    public void setVideo(String video){this.video = video;}
    public String getVideo(){ return this.video; }

    public void setImageURLS(List<String> imageURLS){ this.imageURLS = imageURLS; }
    public List<String> getImageURLS(){ return this.imageURLS; }

    public static final Parcelable.Creator<FeedContent> CREATOR = new Parcelable.Creator<FeedContent>() {

        @Override
        public FeedContent createFromParcel(Parcel source) {
            return new FeedContent(source);
        }


        @Override
        public FeedContent[] newArray(int size) {
            return new FeedContent[size];
        }
    };
}

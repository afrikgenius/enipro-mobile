package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@org.parceler.Parcel
public class Document {


    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("size")
    @Expose
    public int size;

    @SerializedName("extension")
    @Expose
    public String extension;

    public Document(String name, String url, int size, String extension) {
        this.name = name;
        this.url = url;
        this.size = size;
        this.extension = extension;
    }

    public Document(){}

//    public Document(Parcel in) {
//        this.name = in.readString();
//        this.url = in.readString();
//        this.size = in.readInt();
//        this.extension = in.readString();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int i) {
//        dest.writeString(name);
//        dest.writeString(url);
//        dest.writeLong(size);
//        dest.writeString(extension);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

//    public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {
//
//        @Override
//        public Document createFromParcel(Parcel source) {
//            return new Document(source);
//        }
//
//        @Override
//        public Document[] newArray(int size) {
//            return new Document[size];
//        }
//    };

}

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

    public Document() {
    }

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

}

package com.enipro.data.remote.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;

@org.parceler.Parcel
public class Education {

    @SerializedName("_id")
    @Expose
    public ObjectId _id;

    @SerializedName("school")
    @Expose
    public String school;

    @SerializedName("course")
    @Expose
    public String course;

    @SerializedName("degree")
    @Expose
    public String degree;

    @SerializedName("from")
    @Expose
    public String from;

    @SerializedName("to")
    @Expose
    public String to;


    @ParcelConstructor
    public Education(String school, String course, String degree, String from, String to) {
        this.school = school;
        this.course = course;
        this.degree = degree;
        this.from = from;
        this.to = to;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

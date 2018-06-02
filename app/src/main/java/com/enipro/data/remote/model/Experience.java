package com.enipro.data.remote.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;

@org.parceler.Parcel
public class Experience {

    @SerializedName("_id")
    @Expose
    public ObjectId _id;

    @SerializedName("industry")
    @Expose
    public String industry;

    @SerializedName("organisation")
    @Expose
    public String organisation;

    @SerializedName("role")
    @Expose
    public String role;

    @SerializedName("from")
    @Expose
    public String from;

    @SerializedName("to")
    @Expose
    public String to;

    @ParcelConstructor
    public Experience(String industry, String organisation, String role, String from, String to) {
        this.industry = industry;
        this.organisation = organisation;
        this.role = role;
        this.from = from;
        this.to = to;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

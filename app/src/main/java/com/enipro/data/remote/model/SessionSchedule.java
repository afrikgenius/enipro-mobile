package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;

@org.parceler.Parcel
public class SessionSchedule {


    @SerializedName("start_period")
    @Expose
    public String start_period;

    @SerializedName("end_period")
    @Expose
    public String end_period;

    @SerializedName("timing")
    @Expose
    @ParcelProperty("timing")
    public SessionTiming sessionTiming;

    @ParcelConstructor
    public SessionSchedule(String start_period, String end_period, @ParcelProperty("timing") SessionTiming timing) {
        this.start_period = start_period;
        this.end_period = end_period;
        this.sessionTiming = timing;
    }

    public String getStart_period() {
        return start_period;
    }

    public void setStart_period(String start_period) {
        this.start_period = start_period;
    }

    public String getEnd_period() {
        return end_period;
    }

    public void setEnd_period(String end_period) {
        this.end_period = end_period;
    }

    public SessionTiming getSessionTiming() {
        return sessionTiming;
    }

    public void setSessionTiming(SessionTiming sessionTiming) {
        this.sessionTiming = sessionTiming;
    }
}

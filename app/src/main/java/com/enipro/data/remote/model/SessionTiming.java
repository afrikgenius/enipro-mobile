package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A session timing is basically at what time during a day a session will hold.
 * e.g 10am to 12pm on Mondays
 * The time in from and to are in 24 hour mode.
 */
@org.parceler.Parcel
public class SessionTiming {

    @SerializedName("from_time")
    @Expose
    public String time_from;

    @SerializedName("to_time")
    @Expose
    public String time_to;

    @SerializedName("days")
    @Expose
    public List<String> days;

    public SessionTiming() {

    }


    public SessionTiming(List<String> days, String from, String to) {
        this.days = days;
        this.time_from = from;
        this.time_to = to;
    }

    public String getFrom() {
        return time_from;
    }

    public List<String> getDays() {
        return days;
    }

    public String getTo() {
        return time_to;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public void setFrom(String from) {
        this.time_from = from;
    }

    public void setTo(String to) {
        this.time_to = to;
    }
    
}

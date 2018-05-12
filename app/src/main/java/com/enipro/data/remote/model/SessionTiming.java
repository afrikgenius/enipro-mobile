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


//    public SessionTiming(Parcel in) {
//        this.time_from = in.readString();
//        this.time_to = in.readString();
//        in.readList(this.days, List.class.getClassLoader());
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(time_from);
//        dest.writeString(time_to);
//        dest.writeList(days);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }

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

//    public static final Parcelable.Creator<SessionTiming> CREATOR = new Parcelable.Creator<SessionTiming>() {
//
//        @Override
//        public SessionTiming createFromParcel(Parcel source) {
//            return new SessionTiming(source);
//        }
//
//        @Override
//        public SessionTiming[] newArray(int size) {
//            return new SessionTiming[size];
//        }
//    };
}

package com.enipro.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.format.ISODateTimeFormat;

import java.text.ParseException;

public class Date implements Parcelable {

    @SerializedName("$date")
    @Expose
    private String $date;

    public Date(String $date){ this.$date = $date; }

    public Date(Parcel in){
        this.$date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString($date);
    }

    public String get_$date() {
        return $date;
    }
    public void set_$date(String $date) {
        this.$date = $date;
    }

    /**
     * Parsed date through a joda date formatter and parsed to give a java.util.Date.
     * @return the java.util.Date object of the date.
     * @throws ParseException
     */
    public org.joda.time.LocalDateTime getUtilDate() {
        org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        return parser.parseDateTime($date).toLocalDateTime();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Date> CREATOR = new Parcelable.Creator<Date>() {
        @Override
        public Date createFromParcel(Parcel source) {
            return new Date(source);
        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
}

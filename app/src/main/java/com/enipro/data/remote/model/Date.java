package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.format.ISODateTimeFormat;
import org.parceler.ParcelConstructor;

import java.text.ParseException;

@org.parceler.Parcel
public class Date {

    @SerializedName("$date")
    @Expose
    public String $date;

    @ParcelConstructor
    public Date(String $date) {
        this.$date = $date;
    }

    public String get_$date() {
        return $date;
    }

    public void set_$date(String $date) {
        this.$date = $date;
    }

    /**
     * Parsed date through a joda date formatter and parsed to give a java.util.Date.
     *
     * @return the java.util.Date object of the date.
     * @throws ParseException
     */
    public org.joda.time.LocalDateTime getUtilDate() {
        org.joda.time.format.DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
        return parser.parseDateTime($date).toLocalDateTime();
    }
}

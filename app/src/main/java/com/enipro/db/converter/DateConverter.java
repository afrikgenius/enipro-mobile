package com.enipro.db.converter;


import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.Date;

public class DateConverter {

    @TypeConverter
    public static String toDate(Date date) {
        return date.getDate();
    }

    @TypeConverter
    public static Date toString(String date) {
        return new Date(date);
    }
}
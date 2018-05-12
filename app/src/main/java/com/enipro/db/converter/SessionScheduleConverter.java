package com.enipro.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.SessionSchedule;

public class SessionScheduleConverter {

    @TypeConverter
    public static SessionSchedule toSessionSchedule(String sessionScheduleString) {
        return null;
    }

    @TypeConverter
    public static String toString(SessionSchedule schedule) {
        return null;
    }

}

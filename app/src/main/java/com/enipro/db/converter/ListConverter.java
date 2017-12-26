package com.enipro.db.converter;


import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.FeedComment;

import java.util.List;

public class ListConverter {

    @TypeConverter
    public static List<String> toListString(String value){
        return null;
    }

    @TypeConverter
    public static List<FeedComment> toListFComment(String value){
        return null;
    }

    @TypeConverter
    public static String fromListFComment(List<FeedComment> comments){
        return null;
    }

    @TypeConverter
    public static String fromListString(List<String> string){
        return null;
    }
}

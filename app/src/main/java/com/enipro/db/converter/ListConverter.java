package com.enipro.db.converter;


import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.UserConnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter {

    @TypeConverter
    public static List<String> toListString(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
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
    public static String fromListString(List<String> list){
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.toJson(list, type);
    }

    @TypeConverter
    public static List<UserConnection> toListUserConnection(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserConnection>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromListUserConnection(List<UserConnection> connections){
        Gson gson = new Gson();
        Type type = new TypeToken<List<UserConnection>>() {}.getType();
        return gson.toJson(connections, type);
    }

    @TypeConverter
    public static List<SavedFeed> toListSavedFeed(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<SavedFeed>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromListSavedFeed(List<SavedFeed> savedFeeds){
        Gson gson = new Gson();
        Type type = new TypeToken<List<SavedFeed>>() {}.getType();
        return gson.toJson(savedFeeds, type);
    }

    @TypeConverter
    public static List<Education> toListEducation(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Education>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromListEducation(List<Education> education){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Education>>() {}.getType();
        return gson.toJson(education, type);
    }

    @TypeConverter
    public static List<Experience> toListExperiences(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Experience>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromListExperiences(List<Experience> experiences){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Experience>>() {}.getType();
        return gson.toJson(experiences, type);
    }
}

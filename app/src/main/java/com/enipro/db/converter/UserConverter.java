package com.enipro.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class UserConverter {

    @TypeConverter
    public static User toUser(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromUser(User user) {
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        return gson.toJson(user, type);
    }
}

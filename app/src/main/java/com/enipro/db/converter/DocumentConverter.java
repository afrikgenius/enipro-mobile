package com.enipro.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.PremiumDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DocumentConverter {


    @TypeConverter
    public static Document toDocument(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<Document>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromDocument(Document document) {
        Gson gson = new Gson();
        Type type = new TypeToken<Document>() {
        }.getType();
        return gson.toJson(document, type);
    }
}

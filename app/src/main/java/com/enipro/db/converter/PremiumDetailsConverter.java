package com.enipro.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.PremiumDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PremiumDetailsConverter {

    @TypeConverter
    public static PremiumDetails toPremiumDetails(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<PremiumDetails>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromPremiumDetails(PremiumDetails premiumDetails) {
        Gson gson = new Gson();
        Type type = new TypeToken<PremiumDetails>() {
        }.getType();
        return gson.toJson(premiumDetails, type);
    }
}

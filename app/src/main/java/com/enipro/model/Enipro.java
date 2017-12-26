package com.enipro.model;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

public class Enipro extends Application {

    public static final String APPLICATION = "Enipro";
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}

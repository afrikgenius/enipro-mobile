package com.enipro;

import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;

import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;
import java.util.Locale;

import co.paystack.android.PaystackSdk;


public class Application extends MultiDexApplication {

    static EniproDatabase database;
    private static User activeUser;

    /**
     * Temporary bitmap image store for use in application.
     */
    private static Bitmap tempBitmap;

    /**
     * Identifier
     */
    private static String feedMediaIdentifier = "";

    private static File documentPostFile;

    private static String videoPath;

    public static boolean profileEdited = false;

    /**
     * Tag used to log messages in the application
     */
    public static final String TAG = "Enipro";

    // Intent extra sent to notify activity that intent is coming from launcher activity.
    public static int LAUNCHER_ENTRY_VALUE = 0x00123;
    public static String LAUNCHER_ENTRY_NAME = "com.enipro.presentation.login.LAUNCHER_ENTRY_NAME";
    public static final String BITMAP_IDENTIFIER_FEEDS = "com.enipro.presentation.Application.BITMAP_IDENTIFIER_FEEDS";
    public static final String VIDEO_IDENTIFIER_FEEDS = "com.enipro.presentation.Application.VIDEO_IDENTIFIER_FEEDS";

    private static boolean sIsMessageActivityOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        // Initialise payments api
        PaystackSdk.initialize(getApplicationContext());

        database = EniproDatabase.Companion.getInstance(this);
        new AppExecutors().diskIO().execute(() -> activeUser = database.userDao().getActiveUser(true));
    }

    public static Locale getLocale() {

        // TODO Implement correctly to return locale of user.
        return Locale.ENGLISH;
    }

    public static boolean isMessageActivityOpen() {
        return sIsMessageActivityOpen;
    }

    public static void setMessageActivityOpen(boolean IsMessageActivityOpen) {
        Application.sIsMessageActivityOpen = IsMessageActivityOpen;
    }

    /**
     * Returns the active user in the application.
     *
     * @return
     */
    public static User getActiveUser() {
        return activeUser;
    }

    /**
     * Sets the active of the application to the user specified.
     *
     * @param user the user to set active.
     */
    public static void setActiveUser(User user) {
        activeUser = user;
    }

    /**
     * Saves a bitmap image used in the application as temp and it is expected to be flushed when
     * used.
     *
     * @param imageBitmap the image bitmap to save
     */
    public static void saveTempBitmap(Bitmap imageBitmap) {
        tempBitmap = imageBitmap;
    }

    /**
     * Clears the bitmap image.
     */
    public static void flushTempBitmap() {
        tempBitmap = null;
    }

    /**
     * Returns the temporary bitmap stored for use.
     *
     * @return
     */
    public static Bitmap getTempBitmap() {
        return tempBitmap;
    }

    public static void saveTempVideoPath(String vvideoPath) {
        videoPath = vvideoPath;
    }

    public static String getTempVideoPath() {
        return videoPath;
    }

    public static void flushTempVideoPath() {
        videoPath = null;
    }

    public static String getFeedMediaIdentifier() {
        return feedMediaIdentifier;
    }

    public static void setFeedMediaIdentifier(String identifier) {
        feedMediaIdentifier = identifier;
    }

    public static File getDocumentPostFile() {
        return documentPostFile;
    }

    public static void setDocumentPostFile(File documentPostFile) {
        Application.documentPostFile = documentPostFile;
    }

    /**
     * Returns an instance of enipro database to access data in local storage in the application.
     *
     * @return
     */
    public static EniproDatabase getDbInstance() {
        return database;
    }

    public static class FirebaseTopics {
        public static final String TOPICS_PREFIX = "/topics/";
        public static final String FEED_TOPIC = "feeds_topic";
        public static final String REQUEST_TOPIC = "request_topic";
    }

    public static class PermissionRequests {
        public static final int MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0x001; // Request Code for Reading External Storage Permission.
        public static final int MY_PERMISSION_REQUEST_CAMERA = 0X002;
        public static final int MY_PERMISSION_REQUEST_GALLERY = 0X003;
        public static final int MY_PERMISSION_VIDE0 = 0x004;
    }
}

package com.enipro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.multidex.MultiDexApplication;
import android.widget.ImageView;

import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import java.io.File;

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
        Fresco.initialize(this);

        // Initialise payments api
        PaystackSdk.initialize(getApplicationContext());

        // Subscribe the application to a series of topics in order to receive notifications.
        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseTopics.FEED_TOPIC);

        // Register notification channels in the application for devices running Android 8.0


        database = EniproDatabase.getInstance(this);
        new AppExecutors().diskIO().execute(() -> activeUser = database.userDao().getActiveUser(true));

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
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
     *
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

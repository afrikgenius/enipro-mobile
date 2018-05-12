package com.enipro.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.PremiumDetails;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;
import com.enipro.db.converter.DateConverter;
import com.enipro.db.converter.DocumentConverter;
import com.enipro.db.converter.FeedContentConverter;
import com.enipro.db.converter.ListConverter;
import com.enipro.db.converter.ObjectIdConverter;
import com.enipro.db.converter.PremiumDetailsConverter;
import com.enipro.db.converter.SessionScheduleConverter;
import com.enipro.db.dao.FeedContentDao;
import com.enipro.db.dao.FeedDao;
import com.enipro.db.dao.MessageDao;
import com.enipro.db.dao.RequestDao;
import com.enipro.db.dao.UserDao;

/**
 * SQLite database stored on device to save data used in the application.
 */


@Database(entities = {Feed.class, User.class, FeedContent.class, Request.class}, version = 1, exportSchema = false)
// TODO Remove exportSchema and add room.schemaLocation in build.gradle under room annotation processor.
@TypeConverters({ObjectIdConverter.class, DateConverter.class, FeedContentConverter.class, ListConverter.class, SessionScheduleConverter.class, PremiumDetailsConverter.class, DocumentConverter.class})
public abstract class EniproDatabase extends RoomDatabase {

    @VisibleForTesting
    public static final String DATABASE_NAME = "enipro";

    public abstract FeedDao feedDao();

    public abstract FeedContentDao feedContentDao();

    public abstract UserDao userDao();

    public abstract RequestDao requestDao();

    public abstract MessageDao messageDao();

    private static EniproDatabase sInstance;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();


    public static EniproDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (EniproDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), EniproDatabase.class, DATABASE_NAME).build();
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * Checks whether database already exists.
     *
     * @param context
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists())
            setDatabaseCreated();
    }


    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}
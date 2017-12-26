package com.enipro.presentation.feeds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedPresenter extends BasePresenter<FeedContract.View> implements FeedContract.Presenter {

    private EniproDatabase dbInstance;

    FeedPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context){
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }


    @Override
    public void sendPostToAPI(Feed feedData) {
        checkViewAttached();
        getView().showPostNotification();

        // Pull out user information from local storage.
        User applicationUser = Application.getActiveUser();

        restService.createFeedItem(feedData, applicationUser.get_id().get_$oid()).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull Call<Feed> call,@NonNull Response<Feed> response) {
                // Check response code and pass feed data to feed fragment of home activity.
                if(response.isSuccessful()){
                    // Show notification, Persist feed data information in mobile data store and update UI
                    getView().showCompleteNotification();

                    // Persist in data store.
                    new AppExecutors().diskIO().execute(() -> dbInstance.feedDao().insertFeed(response.body()));
                    getView().updateUI(response.body(), applicationUser);
                } else {
                    getView().showErrorNotification();
                    JSONObject jsonObject;
                    try{
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d("Application", jsonObject.getString("errors"));
                    } catch (IOException | JSONException io_json){
//                  TODO Fix casting to activity for FeedFragment      Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feed> call,@NonNull Throwable throwable) {
                getView().showErrorNotification(); // Show Error occured.
                Log.d("Application", call.request().body().toString());
                Log.d("Application", call.request().headers().toString());
                Log.d("Application", throwable.getMessage());
            }
        });
    }

    /**
     * Uses the user id to return the information of a user.
     * @param _id the id of the user.
     * @return
     */
    @Override
    public void getUser(String _id, LocalCallback localCallback){

        // Check if the id parameter is that of the active user and return active user object
        new AppExecutors().diskIO().execute(() -> {
            User appUser = dbInstance.userDao().getUser(_id);
            if(appUser != null)
                localCallback.respond(appUser);
            else {
                restService.getUser(_id).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful()) {
                            localCallback.respond(response.body()); // Passing the result in a local callback.
                        } else {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
                                Log.d("Application", jsonObject.getString("errors"));
                            } catch (IOException | JSONException io_json) {
                                Log.e(Enipro.APPLICATION + ":" + getClass().getCanonicalName(), io_json.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                        Log.d("Application", call.request().body().toString());
                        Log.d("Application", call.request().headers().toString());
                        Log.d("Application", throwable.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void loadFeeds(LocalCallback<List<Feed>> localCallback) {
        User applicationUser = Application.getActiveUser();

        // Load feeds in local storage before hitting the API with a load feed request.
        // In a case where data is empty, display text views representing no feed data.
        Log.d(Enipro.APPLICATION, "Loading feeds from local storage in FeedPresenter");
        new AppExecutors().diskIO().execute(() -> {
            List<Feed> feeds = dbInstance.feedDao().getFeeds();
            if(feeds == null) {
                Log.d(Enipro.APPLICATION, "Feeds is a null object");

                // TODO Grab all feeds for the user from API
            }
            if(feeds.size() == 0)
                localCallback.respond(null); // Null callback result means no feed data.
            else {
                // Reverse the order of the feeds
                Collections.reverse(feeds);
                localCallback.respond(feeds);
            }

            // TODO Get new feeds from the API from the date of last feed in local storage

        });
    }



    @Override
    public String analyseFeedText(String feed_text) {
        return null;
    }

    @Override
    public List<Feed> update_feeds() {
        // TODO Implement fetching of feeds not present in the feed fragment and also in local storage.
        return null;
    }
}

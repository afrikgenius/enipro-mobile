package com.enipro.presentation.feeds;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedCommentPresenter extends BasePresenter<FeedContract.CommentView> implements FeedContract.CommentPresenter {

    private EniproDatabase dbInstance;

    FeedCommentPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }


    @Override
    public void persistComment(FeedComment comment, String feed_owner_id, String feed_id) {
        checkViewAttached();

        // Pull out user information from local storage.
        User applicationUser = Application.getActiveUser();

        restService.createFeedComment(comment, feed_owner_id, feed_id).enqueue(new Callback<FeedComment>() {
            @Override
            public void onResponse(@NonNull Call<FeedComment> call, @NonNull Response<FeedComment> response) {
                // Check response code and pass feed data to feed fragment of home activity.
                if (response.isSuccessful()) {
                    // Persist in data store.
//                    new AppExecutors().diskIO().execute(() -> dbInstance.feedCommentDao().insertComment(response.body()));
                    getView().updateUI(response.body(), applicationUser);
                } else {
                    getView().showErrorNotification();
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d("Application", jsonObject.getString("errors"));
                    } catch (IOException | JSONException io_json) {
//                      TODO Fix casting to activity for FeedFragment      Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeedComment> call, @NonNull Throwable throwable) {
                getView().showErrorNotification(); // Show Error occured.
            }
        });
    }

    @Override
    public void uploadCommentImageFirebase(StorageReference storageReference, Bitmap bitmap, LocalCallback<String> localCallback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Storage metadata for the avatar file
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UploadTask uploadTask = storageReference.putBytes(data, storageMetadata);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads

        }).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL and pass into callback function.
            localCallback.respond(taskSnapshot.getDownloadUrl().toString());
        });
    }

    /**
     * Uses the user id to return the information of a user.
     *
     * @param _id the id of the user.
     * @return
     */
    @Override
    public void getUser(String _id, LocalCallback<User> localCallback) {
        checkViewAttached();
        // Check if the id parameter is that of the active user and return active user object
        new AppExecutors().diskIO().execute(() -> {
            User appUser = dbInstance.userDao().getUser(_id);
            if (appUser != null)
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
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {}
                });
            }
        });
    }


    @Override
    public void loadComments(String user_id, String feedId) {
        checkViewAttached();
        getView().showLoading();
        addDisposable(Observable.defer(() -> restService.getFeedComments(user_id, feedId))
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(comments -> {
                    getView().hideLoading();
                    getView().updateFeedData(comments);
                }, throwable -> getView().showErrorMessage(), () -> {}));
    }
}
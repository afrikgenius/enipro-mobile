package com.enipro.presentation.feeds;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class FeedPresenter extends BasePresenter<FeedContract.View> implements FeedContract.Presenter {

    private EniproDatabase dbInstance;


    public FeedPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }

    @Override
    public void removeFeed(Feed feed) {
        checkViewAttached();
        addDisposable(restService.deleteFeedItem(Application.getActiveUser().getId(), feed.get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe());
    }

    @Override
    public void processFeed(Feed feedData) {
        checkViewAttached();
        getView().showPostNotification();

        // Check if a media item exists(image or video) and persist in firebase.
        if (feedData.getContent().getMediaType() == FeedContent.MediaType.INSTANCE.getIMAGE()) {
            // Persist image in firebase.
            StorageReference storageReference = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_FEED_IMAGES_LOCATION + feedData.getContent().getMediaName() + ".jpg");
            Bitmap imageBitmap = null;
            if (Application.getFeedMediaIdentifier().equals(Application.BITMAP_IDENTIFIER_FEEDS))
                imageBitmap = Application.getTempBitmap();
            Utility.uploadImageFirebase(storageReference, imageBitmap, (downloadURL) -> {
                feedData.getContent().setImage(downloadURL);
                // Clear application temp bitmap
                Application.flushTempBitmap();

                // Checks if a document exists and upload the doc to firebase and send post.
                if (feedData.getContent().isDocExists())
                    uploadDocFirebase(feedData);
                else
                    sendPostToAPI(feedData);
            });
        } else if (feedData.getContent().getMediaType() == FeedContent.MediaType.INSTANCE.getVIDEO()) {
            // Persist video in firebase
            StorageReference storageReference = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_FEED_VIDEO_LOCATION + feedData.getContent().getMediaName() + ".mp4");
            Uri imageURI = null;
            if (Application.getFeedMediaIdentifier().equals(Application.VIDEO_IDENTIFIER_FEEDS))
                imageURI = Uri.fromFile(new File(Application.getTempVideoPath()));
//            Log.d(Application.TAG, imageURI.toString());
            Utility.uploadVideoFirebase(storageReference, imageURI, (downloadURL) -> {
                feedData.getContent().setVideo(downloadURL);
//                sendPostToAPI(feedData);
                // Clear application video URI
                Application.flushTempVideoPath();

                // Checks if a document exists and upload the doc to firebase and send post.
                if (feedData.getContent().isDocExists())
                    uploadDocFirebase(feedData);
                else
                    sendPostToAPI(feedData);

            });
        } else {
//            Log.d(Application.TAG, "No media file, checking for doc");
            // Checks if a document exists and upload the doc to firebase and send post.
            if (feedData.getContent().isDocExists()) {
                uploadDocFirebase(feedData);
            } else
                sendPostToAPI(feedData);
        }
    }


    /**
     * Creates a storage ref and uploads a doc to firebase storage and saves the download url of
     * the doc file in the feed content data of the feed and sends the information to the web api.
     *
     * @param feedData the feed data to modify with the download url.
     */
    private void uploadDocFirebase(Feed feedData) {
        File file = Application.getDocumentPostFile();
        Uri fileUri = Uri.fromFile(file);
//        Log.d(Application.TAG, file.getPath());
        // Send doc to firebase and persist the download URL
        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_FEED_DOCUMENT_LOCATION + fileUri.getLastPathSegment());
        try {
            Utility.uploadFileFirebase(storageReference, file, downloadURL -> {
                String filename = FilenameUtils.removeExtension(fileUri.getLastPathSegment());
                Document document = new Document(filename, downloadURL, (int) file.length(), FilenameUtils.getExtension(fileUri.getLastPathSegment()));
                feedData.getContent().setDoc(document);
                sendPostToAPI(feedData);
            });
        } catch (FileNotFoundException fnfe) {
            FirebaseCrash.log(fnfe.getMessage());
        }
    }

    @Override
    public void sendPostToAPI(Feed feedData) {
        checkViewAttached();

        // Pull out user information from local storage.
        User applicationUser = Application.getActiveUser();
        restService.createFeedItem(feedData, applicationUser.get_id().get_$oid()).enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(@NonNull Call<Feed> call, @NonNull Response<Feed> response) {
                // Check response code and pass feed data to feed fragment of home activity.
                if (response.isSuccessful()) {
                    // Show notification, Persist feed data information in mobile data store and update UI
                    getView().showCompleteNotification();

                    // Persist in data store.
                    new AppExecutors().diskIO().execute(() -> dbInstance.feedDao().insertFeed(response.body()));
                    getView().updateUI(response.body(), applicationUser);
                } else {
                    getView().showErrorNotification();
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
//                        Log.d("Application", jsonObject.getString("errors"));
                    } catch (IOException | JSONException io_json) {
                        // TODO Fix casting to activity for FeedFragment      Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Feed> call, @NonNull Throwable throwable) {
                getView().showErrorNotification(); // Show Error occured.
            }
        });
    }

    /**
     * Uses the user id to return the information of a user.
     *
     * @param _id the id of the user.
     * @return
     */
    @Override
    public void getUser(String _id, LocalCallback localCallback) {
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
//                                Log.d("Application", jsonObject.getString("errors"));
                            } catch (IOException | JSONException io_json) {
//                                Log.e(Enipro.APPLICATION + ":" + getClass().getCanonicalName(), io_json.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    }
                });
            }
        });
    }

    @Override
    public void loadFeeds(LocalCallback<List<Feed>> localCallback) {
        checkViewAttached();
        User applicationUser = Application.getActiveUser();

        // TODO Grab all feeds for the user from API using pagination and store
        addDisposable(Observable.defer(() -> restService.getFeeds(applicationUser.get_id().get_$oid()))
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(feedList -> {
                    getView().showLoading();
                    localCallback.respond(feedList);

                    if (feedList.size() == 0)
                        localCallback.respond(null); // Null callback result means no feed data.
                    else {
                        // Reverse the order of the feeds
                        Collections.reverse(feedList);
                        localCallback.respond(feedList);
                    }
                }, throwable -> getView().showErrorMessage(), () -> {
                    getView().hideLoading();
                }));
    }

    @Override
    public void deleteFeed(Feed feed) {
        addDisposable(restService.deleteFeedItem(Application.getActiveUser().get_id().get_$oid(), feed.get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(aVoid -> {
                }, throwable -> {
                }, () -> {
                }));
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


    @Override
    public void loadSavedFeeds() {
        // Collect all saved feed info from application user profile and for each, request feed with feed id.
        List<SavedFeed> feeds = Application.getActiveUser().getSavedFeeds();
        addDisposable(restService.getSavedFeeds(Application.getActiveUser().get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(feedList -> {
                    getView().onSavedFeedsRetrieved(feedList);
                }, throwable -> {
                }, () -> {
                }));
    }

    @Override
    public void removeSaved(Feed feed) {
        addDisposable(restService.deleteSavedFeed(Application.getActiveUser().get_id().get_$oid(), feed.get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
                    Application.setActiveUser(user);
                }, throwable -> {
                    String errorBody = ((HttpException) throwable).response().errorBody().string();
//                    Log.d(Application.TAG, errorBody);
//                    Log.d(Application.TAG, throwable.getMessage());
                }, () -> {
                }));
    }

    @Override
    public void addSaved(Feed feed) {
        addDisposable(restService.saveFeed(new SavedFeed(feed.get_id().get_$oid()), Application.getActiveUser().get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
                    Application.setActiveUser(user);
                }, throwable -> {
                    String errorBody = ((HttpException) throwable).response().errorBody().string();
//                    Log.d(Application.TAG, errorBody);
//                    Log.d(Application.TAG, throwable.getMessage());
                }, () -> {
                }));
    }
}

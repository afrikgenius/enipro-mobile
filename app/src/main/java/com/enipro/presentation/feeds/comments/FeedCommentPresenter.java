package com.enipro.presentation.feeds.comments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.base.BasePresenter;
import com.enipro.presentation.feeds.FeedContract;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class FeedCommentPresenter extends BasePresenter<FeedContract.CommentView> implements FeedContract.CommentPresenter {


    private Context context;

    public FeedCommentPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        this.context = context;
    }

    @Override
    public void persistComment(FeedComment comment, String feed_owner_id, String feed_id) {
        checkViewAttached();

        // Pull out user information from local storage.
        User applicationUser = Application.getActiveUser();

        addDisposable(restService.createFeedComment(comment, feed_id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(response -> {
                    // Persist in data store.
//                    new AppExecutors().diskIO().execute(() -> dbInstance.feedCommentDao().insertComment(response.body()));
                    getView().updateUI(response, applicationUser);
                }, throwable -> getView().showErrorNotification()));
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

    public void onLikeClicked(View view, Feed feed) {
//        if (!likeState) {
//            // First off increase the number of comments and make the animation happen then
//            // revert if error occurred
//            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite, null));
//            Utility.applyBounceAnimation((ImageButton) view, context);
////            String likesStr =
//            int currentNoLikes = Integer.valueOf("5");
//
////            likeFeed(feed.getId()).subscribe(feedObject -> {
////                int likes = feed.getLikesCount();
////            }, throwable -> {
////
////            });
//            likeFeed(feed.getId());
//        } else {
//            unlikeFeed(feed.getId());
//            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_border, null));
//            Utility.applyBounceAnimation((ImageButton) view, context);
//        }
//        likeState = !likeState; // Invert like state.
    }

    /**
     * Activates the comment edit text when the comment button is clicked.
     */
    public void onCommentClicked() {
//        binding.postAComment.commentEditText.requestFocus();
//        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * When the saved button is clicked, depending on the state of the feed whether saved or not of
     * the authenticating user, the current state is negated.
     *
     * @param feed the feed to save or unsave depending on the saved state.
     */
    public void onSavedClicked(Feed feed) {
        // Check saved state before saving again.

    }

    public void onShareClicked(Feed feed) {
    }

    Observable<Feed> likeFeed(String feed_id) {
        return restService.likeFeed(feed_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler);
    }

    Observable<Feed> unlikeFeed(String feed_id) {
        return restService.unlikeFeed(feed_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler);
    }

    /***
     * Starts an activity that displays a list of users that liked a feed item.
     * @param feed the feed to get information from.
     */
    public void getFeedLikeUsers(Feed feed) {

    }

}
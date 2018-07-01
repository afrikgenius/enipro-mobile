package com.enipro.presentation.feeds;

import android.graphics.Bitmap;

import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FeedContract {

    public interface View extends MvpView {
        void showPostNotification();

        void showCompleteNotification();

        void showErrorNotification();

        // Updates the user interface with the newly added feed item.
        void updateUI(Feed feedItem, User user);

        void showErrorMessage();

        void showLoading();

        void hideLoading();

        /**
         * Runs when saved feeds have been retrieved for user.
         */
        void onSavedFeedsRetrieved(List<Feed> feeds);
    }

    interface CommentView extends MvpView {

        void showErrorNotification();

        void showLoading();

        void hideLoading();

        void showErrorMessage();

        /**
         * Updates the UI with the feed comment.
         *
         * @param commentItem the comment item to send
         * @param user        the user object.
         */
        void updateUI(FeedComment commentItem, User user);

        void updateFeedData(List<FeedComment> comments);
    }

    public interface CommentPresenter extends MvpPresenter<FeedContract.CommentView> {

        /**
         * Returns the information of a user with the user id in a callback passed into the function.
         *
         * @param _id      the id of the user.
         * @param callback the callback function to be called after a response is received.
         */
        void getUser(String _id, LocalCallback<User> callback);


        /**
         * Sends a feed comment information to the web API and also persists in datastore.
         *
         * @param comment       the comment to add
         * @param feed_owner_id the id of the owner of the feed item.
         * @param feed_id       the id of the feed the comment is to be added.
         */
        void persistComment(FeedComment comment, String feed_owner_id, String feed_id);

        /**
         * Uploads the comment image to firebase and returns the download URL of the uploaded image
         *
         * @param storageReference firebase storage reference.
         * @param bitmap           the bitmap to be sent to Firebase.
         * @param localCallback    callback function called after operation is done
         */
        void uploadCommentImageFirebase(StorageReference storageReference, Bitmap bitmap, LocalCallback<String> localCallback);


        /**
         * Loads comments for a feed
         *
         * @param user_id
         * @param feed_id
         */
        void loadComments(String user_id, String feed_id);

    }

    public interface Presenter extends MvpPresenter<FeedContract.View> {

        int POST_FEED_REQUEST = 0x01;

        String ACTIVITY_RETURN_KEY = "Return Data";

        String FEED = "com.enipro.presentation.feeds.FeedContract.Presenter.FEED";

        String OPEN_KEYBOARD_COMMENT = "com.enipro.presentation.feeds.FeedContract.Presenter.OPEN_KEYBOARD_COMMENT";
        int ADD_COMMENT = 0x00231312;

        void sendPostToAPI(Feed feed);

        void deleteFeed(Feed feed);

        /**
         * Fetches new feeds on manual request by the user.
         *
         * @return the feeds fetched by the update operation.
         */
        List<Feed> update_feeds();

        /**
         * Analyses the feed text and recognises hashtags, urls, names of people etc.
         *
         * @param feed_text
         * @return
         */
        String analyseFeedText(String feed_text);

        /**
         * Returns the information of a user with the user id in a callback passed into the function.
         *
         * @param _id      the id of the user.
         * @param callback the callback function to be called after a response is received.
         */
        void getUser(String _id, LocalCallback callback);

        /**
         * Loads feeds for the current active user in the application from both local storage and online storage.
         * Implementation loads only feeds from online storage that are not present in local storage to reduce the
         * amount of data being returned by the API.
         *
         * @param localCallback A callback given to pass the result back to the calling function since it will be run in an executor.
         */
        void loadFeeds(LocalCallback<List<Feed>> localCallback);

        /**
         * Performs some operations on the feed data before sending down to API.
         *
         * @param feed the feed data to process.
         */
        void processFeed(Feed feed);

        void removeFeed(Feed feed);

        /**
         * Loads saved feeds that the application user has in profile.
         */
        void loadSavedFeeds();


        /**
         * Add the feed item to saved feeds in the users profile.
         * @param feed the feed item to add.
         */
        void addSaved(Feed feed);

        /**
         * Removes a feed item from the saved feeds in the users profile
         * @param feed the feed item to remove.
         */
        void removeSaved(Feed feed);
    }
}

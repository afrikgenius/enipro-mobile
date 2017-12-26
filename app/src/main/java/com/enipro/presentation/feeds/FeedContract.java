package com.enipro.presentation.feeds;

import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;

public class FeedContract {

    interface View extends MvpView {
        void showPostNotification();

        void showCompleteNotification();

        void showErrorNotification();

        // Updates the user interface with the newly added feed item.
        void updateUI(Feed feedItem, User user);
    }

    public interface Presenter extends MvpPresenter<FeedContract.View> {

        int POST_FEED_REQUEST = 0x01;

        String ACTIVITY_RETURN_KEY = "Return Data";

        String FEED = "com.enipro.presentation.feeds.FeedContract.Presenter.FEED";

        String OPEN_KEYBOARD_COMMENT = "com.enipro.presentation.feeds.FeedContract.Presenter.OPEN_KEYBOARD_COMMENT";
        int ADD_COMMENT = 0x00231312;

        void sendPostToAPI(Feed feed);

        /**
         * Fetches new feeds on manual request by the user.
         * @return the feeds fetched by the update operation.
         */
        List<Feed> update_feeds();

        /**
         * Analyses the feed text and recognises hashtags, urls, names of people etc.
         * @param feed_text
         * @return
         */
        String analyseFeedText(String feed_text);

        /**
         * Returns the information of a user with the user id in a callback passed into the function.
         * @param _id the id of the user.
         * @param callback the callback function to be called after a response is received.
         */
        void getUser(String _id, LocalCallback callback);

        /**
         * Loads feeds for the current active user in the application from both local storage and online storage.
         * Implementation loads only feeds from online storage that are not present in local storage to reduce the
         * amount of data being returned by the API.
         * @param localCallback A callback given to pass the result back to the calling function since it will be run in an executor.
         */
        void loadFeeds(LocalCallback<List<Feed>> localCallback);

//        boolean persistLikes();
    }
}

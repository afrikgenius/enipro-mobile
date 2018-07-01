package com.enipro.presentation.post;

import com.enipro.data.remote.model.Feed;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class PostContract {

    interface View extends MvpView {

        /**
         * Sends the feed data to the feeds fragment to be added to the UI of the post activity.
         * @param feed the feed item to be added.
         */
        void sendFeedItem(Feed feed);

        void showPostError(String errorMessage);

        void saveFeedAsDraft(Feed feed);

        void finish();

    }

    interface Presenter extends MvpPresenter<PostContract.View> {

        String ACTIVITY_RETURN_KEY = "Return Data";

    }
}
package com.enipro.presentation.requests;


import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;

public class RequestsContract {

    interface View extends MvpView {
        void onRequestsCollected(List<Request> requests);

        void onRequestsError();

        void onRequestAccepted();

        void onRequestDeclined();
    }


    interface Presenter extends MvpPresenter<RequestsContract.View> {

        /**
         * Returns all requests pending that has the user with the user id
         * as sender or recipient.
         *
         * @param user_id the user or user id
         */
        void getRequests(String user_id);

        void getUser(String _id, LocalCallback<User> localCallback);

        void acceptRequest(Request request);

        void declineRequest(Request request);
    }

}

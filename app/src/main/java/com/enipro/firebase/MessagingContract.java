package com.enipro.firebase;

import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class MessagingContract {


    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<View> {
        void getUser(String _id, LocalCallback<User> localCallback);
    }
}
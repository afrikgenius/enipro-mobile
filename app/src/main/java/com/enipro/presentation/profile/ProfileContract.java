package com.enipro.presentation.profile;


import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class ProfileContract {

    interface View extends MvpView {

        void showProgress();

        void dismissProgress();
    }

    interface Presenter extends MvpPresenter<ProfileContract.View> {

        /**
         * Logs the current active user out of the application.
         */
        void logout();
    }
}

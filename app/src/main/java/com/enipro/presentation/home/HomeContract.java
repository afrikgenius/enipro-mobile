package com.enipro.presentation.home;


import com.enipro.data.remote.model.User;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class HomeContract {

    interface View extends MvpView {

        Presenter getPresenter();
    }

    interface Presenter extends MvpPresenter<View> {
        User getActiveUser();
    }
}

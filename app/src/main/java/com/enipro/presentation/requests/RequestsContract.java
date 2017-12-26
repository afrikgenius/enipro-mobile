package com.enipro.presentation.requests;


import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class RequestsContract {

    interface View extends MvpView {

    }


    interface Presenter extends MvpPresenter<RequestsContract.View> {

    }
}

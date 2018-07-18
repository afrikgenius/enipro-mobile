package com.enipro.presentation.home;


import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;

public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {


    public HomePresenter(EniproRestService restService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler){
        super(restService, ioScheduler, mainScheduler);
    }


    @Override
    public User getActiveUser() {
        return Application.getActiveUser();
    }
}
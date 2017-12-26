package com.enipro.presentation.requests;


import android.content.Context;

import com.enipro.data.remote.EniproRestService;
import com.enipro.db.EniproDatabase;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;

public class RequestsPresenter extends BasePresenter<RequestsContract.View> implements RequestsContract.Presenter {

    private EniproDatabase db;

    RequestsPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context){
        super(restService, ioScheduler, mainScheduler);
    }
}

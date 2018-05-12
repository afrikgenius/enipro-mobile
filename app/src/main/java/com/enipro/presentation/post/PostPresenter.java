package com.enipro.presentation.post;

import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Feed;
import com.enipro.model.ValidationService;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;


public class PostPresenter extends BasePresenter<PostContract.View> implements PostContract.Presenter {

    public PostPresenter(EniproRestService eniproRestService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler, ValidationService validationService){
        super(eniproRestService, ioScheduler, mainScheduler);
    }
}

package com.enipro.services.firebase;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;

public class MessagingPresenter extends BasePresenter<MessagingContract.View> implements MessagingContract.Presenter {


    MessagingPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler) {
        super(restService, ioScheduler, mainScheduler);
    }

    /**
     * Uses the user id to return the information of a user.
     *
     * @param _id the id of the user.
     * @return
     */
    @Override
    public void getUser(String _id, LocalCallback<User> localCallback) {
        checkViewAttached();
        // Get token from shared pref
        addDisposable(restService.getUser(_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> localCallback.respond(user)));
    }

}
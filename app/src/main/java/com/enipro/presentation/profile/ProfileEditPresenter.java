package com.enipro.presentation.profile;


import android.content.Context;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;

public class ProfileEditPresenter extends BasePresenter<ProfileContract.EditView> implements ProfileContract.EditPresenter {

    // Private Instance variables
    public Context context;
    private EniproDatabase dbInstance;

    ProfileEditPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }

    @Override
    public void updateUser(User user, String user_id) {
        checkViewAttached();
        addDisposable(restService.updateUser(user, user_id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user1 -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user1));
                    Application.setActiveUser(user1);
                    getView().onProfileUpdated(user1);
                }, throwable -> {
                }, () -> {
                }));
    }
}

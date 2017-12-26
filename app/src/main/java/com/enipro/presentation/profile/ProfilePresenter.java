package com.enipro.presentation.profile;


import android.content.Context;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.presentation.base.BasePresenter;

import io.reactivex.Scheduler;

public class ProfilePresenter extends BasePresenter<ProfileContract.View> implements ProfileContract.Presenter {


    private EniproDatabase db;

    ProfilePresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context){
        super(restService, ioScheduler, mainScheduler);
        db = EniproDatabase.getInstance(context);
    }

    @Override
    public void logout() {
        checkViewAttached();
        new AppExecutors().diskIO().execute(() -> {
            // Delete user and all other data stored in the local storage.
            db.userDao().deleteUser(Application.getActiveUser());
            db.feedDao().deleteAll();
            db.feedContentDao().deleteAll();
        });
    }
}

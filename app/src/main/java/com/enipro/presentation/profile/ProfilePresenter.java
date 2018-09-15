package com.enipro.presentation.profile;


import android.content.Context;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;

import java.util.List;

import io.reactivex.Scheduler;

public class ProfilePresenter extends BasePresenter<ProfileContract.View> implements ProfileContract.Presenter {

    private EniproDatabase dbInstance;

    ProfilePresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.Companion.getInstance(context);
    }

    @Override
    public void logout() {
        checkViewAttached();
        new AppExecutors().diskIO().execute(() -> {
            // Delete user and all other data stored in the local storage.
            dbInstance.user().deleteUser(Application.getActiveUser());
            dbInstance.feed().deleteAll();
            dbInstance.feedContent().deleteAll();
        });
    }

    @Override
    public void getRequest(String request_id, LocalCallback<Request> requestLocalCallback) {
//        addDisposable(restService.getRequest(request_id, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe());
    }

    @Override
    public void getRequest(String sender, String recipient, LocalCallback<List<Request>> requestLocalCallback) {
//        addDisposable(restService.getRequest(sender, recipient, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe(requestLocalCallback::respond, throwable -> {
//                }, () -> {
//                }));
    }

    @Override
    public void requestMentoring(String sender, String recipient) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_MENTORING, Constants.CATEGORY_SEND);
        request.setRecipient(recipient);
//        addDisposable(restService.createRequest(request, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe(request1 -> getView().onMentoringRequestSent(), throwable -> getView().onError(throwable), () -> {
//                }));
    }

    @Override
    public void availableForMentoring(String sender) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_MENTORING, Constants.CATEGORY_AVAILABLE);
//        addDisposable(restService.createRequest(request, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe(req -> getView().onAvailableRequestSent(), throwable -> {
//                }, () -> {
//                }));
    }

    @Override
    public void deleteRequest() {
    }

    @Override
    public void addCircle(UserConnection userConnection) {
        checkViewAttached();
        addDisposable(restService.addUserToCircle(userConnection, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onCircleAdded();
                }, throwable -> getView().onError(throwable), () -> {
                }));
    }

    @Override
    public void requestAddCircle(String sender, String recipient) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_CIRCLE, Constants.CATEGORY_CIRCLE);
        request.setRecipient(recipient);
//        addDisposable(restService.createRequest(request, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe(request1 -> getView().onAddCircleRequestValidated(), throwable -> getView().onError(throwable), () -> {
//                }));


    }

    @Override
    public void requestAddNetwork(String sender, String recipient) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_NETWORK, Constants.CATEGORY_NETWORK);
        request.setRecipient(recipient);
//        addDisposable(restService.createRequest(request, Application.getAuthToken())
//                .subscribeOn(ioScheduler)
//                .observeOn(mainScheduler)
//                .subscribe(request1 -> getView().onAddNetworkRequestValidated(), throwable -> getView().onError(throwable), () -> {
//                }));

    }

    @Override
    public void addNetwork(UserConnection userConnection) {
        checkViewAttached();
        addDisposable(restService.addUsersToNetwork(userConnection, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onNetworkAdded();
                }, throwable -> getView().onError(throwable), () -> {
                }));
    }

    @Override
    public void removeNetwork(String user_id) {
        checkViewAttached();
        addDisposable(restService.deleteUserFromNetwork(user_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onNetworkRemoved();
                }, (throwable) -> getView().onError(throwable), () -> {
                }));

    }

    @Override
    public void removeCircle(String user_id) {
        checkViewAttached();
        addDisposable(restService.deleteUserFromCircle(user_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onCircleRemoved();
                }, throwable -> getView().onError(throwable), () -> {
                }));
    }

    @Override
    public void getUser(String _id, LocalCallback<User> localCallback) {
        checkViewAttached();
        addDisposable(restService.getUser(_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> localCallback.respond(user)));
    }
}

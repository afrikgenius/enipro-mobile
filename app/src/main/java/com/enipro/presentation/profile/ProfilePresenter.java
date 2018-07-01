package com.enipro.presentation.profile;


import android.content.Context;
import android.support.annotation.NonNull;

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
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePresenter extends BasePresenter<ProfileContract.View> implements ProfileContract.Presenter {


    private EniproDatabase dbInstance;

    ProfilePresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }

    @Override
    public void logout() {
        checkViewAttached();
        new AppExecutors().diskIO().execute(() -> {
            // Delete user and all other data stored in the local storage.
            dbInstance.userDao().deleteUser(Application.getActiveUser());
            dbInstance.feedDao().deleteAll();
            dbInstance.feedContentDao().deleteAll();
        });
    }

    @Override
    public void getRequest(String request_id, LocalCallback<Request> requestLocalCallback) {
        addDisposable(restService.getRequest(request_id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe());
    }

    @Override
    public void getRequest(String sender, String recipient, LocalCallback<List<Request>> requestLocalCallback) {
        addDisposable(restService.getRequest(sender, recipient)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(requestLocalCallback::respond, throwable -> {
                }, () -> {
                }));
    }

    @Override
    public void requestMentoring(String sender, String recipient) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_MENTORING, Constants.CATEGORY_SEND);
        request.setRecipient(recipient);
        addDisposable(restService.createRequest(request)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(request1 -> getView().onMentoringRequestSent(), throwable -> getView().onError(throwable), () -> {
                }));
    }

    @Override
    public void availableForMentoring(String sender) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_MENTORING, Constants.CATEGORY_AVAILABLE);
        addDisposable(restService.createRequest(request)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(req -> getView().onAvailableRequestSent(), throwable -> {
                }, () -> {
                }));
    }

    @Override
    public void deleteRequest() {
    }

    @Override
    public void addCircle(UserConnection userConnection) {
        checkViewAttached();
        addDisposable(restService.addUserToCircle(Application.getActiveUser().get_id().get_$oid(), userConnection)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
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
        addDisposable(restService.createRequest(request)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(request1 -> getView().onAddCircleRequestValidated(), throwable -> getView().onError(throwable), () -> {}));


    }

    @Override
    public void requestAddNetwork(String sender, String recipient) {
        checkViewAttached();
        Request request = new Request(sender, Constants.TYPE_NETWORK, Constants.CATEGORY_NETWORK);
        request.setRecipient(recipient);
        addDisposable(restService.createRequest(request)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(request1 -> getView().onAddNetworkRequestValidated(), throwable -> getView().onError(throwable), () -> {}));

    }

    @Override
    public void addNetwork(UserConnection userConnection) {
        checkViewAttached();
        addDisposable(restService.addUsersToNetwork(Application.getActiveUser().get_id().get_$oid(), userConnection)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onNetworkAdded();
                }, throwable -> getView().onError(throwable), () -> {
                }));
    }

    @Override
    public void removeNetwork(String user_id) {
        checkViewAttached();
        addDisposable(restService.deleteUserFromNetwork(Application.getActiveUser().get_id().get_$oid(), user_id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onNetworkRemoved();
                }, (throwable) -> getView().onError(throwable), () -> {
                }));

    }

    @Override
    public void removeCircle(String user_id) {
        checkViewAttached();
        addDisposable(restService.deleteUserFromCircle(Application.getActiveUser().get_id().get_$oid(), user_id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.userDao().updateUser(user));
                    Application.setActiveUser(user);
                    getView().onCircleRemoved();
                }, throwable -> getView().onError(throwable), () -> {
                }));
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
        restService.getUser(_id).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    localCallback.respond(response.body()); // Passing the result in a local callback.
                } else {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        FirebaseCrash.log(jsonObject.getString("errors"));
                    } catch (IOException | JSONException io_json) {
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
            }
        });
    }
}

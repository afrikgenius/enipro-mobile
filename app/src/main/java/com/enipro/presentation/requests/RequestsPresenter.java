package com.enipro.presentation.requests;


import android.content.Context;
import android.support.annotation.NonNull;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsPresenter extends BasePresenter<RequestsContract.View> implements RequestsContract.Presenter {

    private EniproDatabase db;

    RequestsPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        db = EniproDatabase.getInstance(context);
    }


    @Override
    public void acceptRequest(Request request) {
        checkViewAttached();
        request.setStatus(Constants.ACCEPT_REQUEST);
        addDisposable(restService.updateRequest(request, request.get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(request1 -> {
                    // Save request in local storage please do that
                    new AppExecutors().diskIO().execute(() -> db.requestDao().insertRequest(request1));
                    getView().onRequestAccepted();
                }, throwable -> {
                }));
    }


    @Override
    public void declineRequest(Request request) {
        checkViewAttached();
        Request req = new Request();
        req.setStatus(Constants.DECLINE_REQUEST);
        addDisposable(restService.updateRequest(req, request.get_id().get_$oid())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(request1 -> getView().onRequestDeclined()));
    }

    @Override
    public void getRequests(String user_id) {
        checkViewAttached();
        String user_type = Application.getActiveUser().getUserType().toUpperCase();

        // Get all requests that have the user as recipient
        if (user_type.equals(Constants.STUDENT)) {
            addDisposable(restService.getRequestSender(user_id)
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(requests -> {
                        // TODO Before passing result along to activity
                        // TODO Be a sweetheart and save it in Local storage.
                        getView().onRequestsCollected(requests);
                    }, throwable -> getView().onRequestsError(), () -> {
                    }));
        } else if (user_type.equals(Constants.PROFESSIONAL)) {
            addDisposable(restService.getRequestRecipient(user_id)
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(requests -> {
                        // TODO Before passing result along to activity
                        // TODO Be a sweetheart and save it in Local storage.
                        getView().onRequestsCollected(requests);
                    }, throwable -> getView().onRequestsError(), () -> {
                    }));
        }
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
        // Check if the id parameter is that of the active user and return active user object
        new AppExecutors().diskIO().execute(() -> {
            User appUser = db.userDao().getUser(_id);
            if (appUser != null)
                localCallback.respond(appUser);
            else {
                restService.getUser(_id).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                        if (response.isSuccessful()) {
                            localCallback.respond(response.body()); // Passing the result in a local callback.
                        } else {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response.errorBody().string());
//                                Log.d("Application", jsonObject.getString("errors"));
                            } catch (IOException | JSONException io_json) {
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                    }
                });
            }
        });
    }
}

package com.enipro.firebase;

import android.support.annotation.NonNull;

import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
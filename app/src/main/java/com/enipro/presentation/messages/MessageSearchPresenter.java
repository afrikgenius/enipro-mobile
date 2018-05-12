package com.enipro.presentation.messages;


import android.support.annotation.NonNull;
import android.util.Log;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserType;
import com.enipro.model.Enipro;
import com.enipro.presentation.base.BasePresenter;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageSearchPresenter extends BasePresenter<MessagesContract.SearchView> implements MessagesContract.SearchPresenter {

    MessageSearchPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler) {
        super(restService, ioScheduler, mainScheduler);
    }

    @Override
    public void loadConnectedUsers() {
        checkViewAttached();
        User user = Application.getActiveUser();
        if(user.getUserType().equalsIgnoreCase(UserType.PROFESSIONAL)){
            Log.d(Enipro.APPLICATION, "Professional");
            restService.getNetworkUsers(user.get_id().get_$oid()).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(@NotNull Call<List<User>> call, @NotNull Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        getView().onConnectedUsersLoaded(response.body()); // Passing the result in a local callback.} else {
                    } else {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            Log.d("Application", jsonObject.getString("errors"));
                        } catch (IOException | JSONException io_json) {
                            Log.e(Enipro.APPLICATION + ":" + getClass().getCanonicalName(), io_json.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<User>> call,@NonNull Throwable throwable) {
                    Log.d("Application", call.request().body().toString());
                    Log.d("Application", call.request().headers().toString());
                    Log.d("Application", throwable.getMessage());
                }
            });
        } else if(user.getUserType().equalsIgnoreCase(UserType.STUDENT)){ //
            restService.getCircleUsers(user.get_id().get_$oid()).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(@NotNull Call<List<User>> call,@NotNull Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        getView().onConnectedUsersLoaded(response.body()); // Passing the result in a local callback.
                    } else {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            Log.d("Application", jsonObject.getString("errors"));
                        } catch (IOException | JSONException io_json) {
                            Log.e(Enipro.APPLICATION + ":" + getClass().getCanonicalName(), io_json.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<User>> call,@NonNull Throwable throwable) {
                    Log.d("Application", call.request().body().toString());
                    Log.d("Application", call.request().headers().toString());
                    Log.d("Application", throwable.getMessage());
                }
            });
        }
    }

    @Override
    public void search(String term) {
        checkViewAttached();
    }
}

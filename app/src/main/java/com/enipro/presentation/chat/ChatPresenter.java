package com.enipro.presentation.chat;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserType;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
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

public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private EniproDatabase dbInstance;

    ChatPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context){
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.getInstance(context);
    }


    @Override
    public void loadChatUsers(User user, LocalCallback<List<User>> localCallback) {
        checkViewAttached();
        // Get user type and make appropriate request to grab circle or network users.
        Log.d(Enipro.APPLICATION, "User type is:" + user.getUserType());
        if(user.getUserType().equalsIgnoreCase(UserType.PROFESSIONAL)){
            Log.d(Enipro.APPLICATION, "Professional");
            restService.getNetworkUsers(user.get_id().get_$oid()).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(@NotNull  Call<List<User>> call,@NotNull Response<List<User>> response) {
                    if (response.isSuccessful()) {
                        localCallback.respond(response.body()); // Passing the result in a local callback.} else {
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
                        localCallback.respond(response.body()); // Passing the result in a local callback.
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


    /**
     * Uses the user id to return the information of a user.
     * @param _id the id of the user.
     * @return
     */
    public void getUser(String _id, LocalCallback localCallback){

        // Check if the id parameter is that of the active user and return active user object
        new AppExecutors().diskIO().execute(() -> {
            User appUser = dbInstance.userDao().getUser(_id);
            if(appUser != null)
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
                                Log.d("Application", jsonObject.getString("errors"));
                            } catch (IOException | JSONException io_json) {
                                Log.e(Enipro.APPLICATION + ":" + getClass().getCanonicalName(), io_json.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                        Log.d("Application", call.request().body().toString());
                        Log.d("Application", call.request().headers().toString());
                        Log.d("Application", throwable.getMessage());
                    }
                });
            }
        });
    }
}

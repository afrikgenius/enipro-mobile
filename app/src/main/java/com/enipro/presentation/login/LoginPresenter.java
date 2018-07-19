package com.enipro.presentation.login;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Login;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.DataValidator;
import com.enipro.model.Utility;
import com.enipro.model.ValidationService;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter, DataValidator {

    /* Private Instance Variables*/
    private Map<String, View> views = new HashMap<>();
    private ValidationService validationService;
    EniproDatabase db;
    private Context context;


    public LoginPresenter(EniproRestService restService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler, ValidationService validationService, Context context) {
        super(restService, ioScheduler, mainScheduler);
        this.validationService = validationService;
        this.db = EniproDatabase.Companion.getInstance(context);
        this.context = context;
    }

    /**
     * Attaches a view item to be used by the presenter.
     *
     * @param itemName
     * @param view
     */
    public void attachViewItems(String itemName, android.view.View view) {
        views.put(itemName, view);
    }

    @Override
    public void validate(String[] data) {
        checkViewAttached(); // Before doing anything, the view must be attached.

        // Collapse the android keyboard
        Utility.collapseKeyboard((Activity) getView());

        // Performs validation and verification of email address and password with online data.

        String emailAddress = data[0];
        String password = data[1];

        // Email Validation
        boolean emailValid = validationService.validateEmail(emailAddress);

        // Before validating an verifying, presence of internet connection has to be gotten.
        if (!Utility.isInternetConnected((Activity) getView()))
            getView().showMessage(R.string.no_internet);
        else if (!emailValid || password.equalsIgnoreCase(""))
            getView().setViewError(views.get(LoginContract.View.EMAIL), "Please enter a valid email");
        else {
            // Use rest service to verify user credentials
            Login login = new Login();
            login.setEmail(emailAddress);
            login.setPassword(password);
            getView().showProgress();
            restService.login(login).enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    // Dismiss progress.
                    getView().dismissProgress();
                    if (response.isSuccessful()) {
                        User applicationUser = response.body();
                        applicationUser.setFirebaseToken(Utility.getTokenFromSharedPref(context));
                        addDisposable(restService.updateUser(applicationUser, applicationUser.get_id().getOid())
                                .subscribeOn(ioScheduler)
                                .observeOn(mainScheduler)
                                .subscribe(user -> {
                                    // Persist user information in mobile datastore.
                                    new AppExecutors().diskIO().execute(() -> {
                                        applicationUser.setActive(true); // TODO Check what is going on here.
                                        Application.setActiveUser(applicationUser); // Set the active user of the application.
                                        db.userDao().insertUser(applicationUser); // Execute the operation on the diskIO thread.
                                    });

                                    // Login user information into Firebase Auth using Email/Password Authentication.
                                    loginFirebaseAuth(emailAddress, password);

                                    // Open application
                                    getView().openApplication(applicationUser);
                                }));
                    } else {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            getView().showMessage(LoginContract.View.MESSAGE_DIALOG, jsonObject.getString("description"));
                        } catch (IOException | JSONException io_json) {
//                            Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    getView().dismissProgress();
                    getView().showMessage(LoginContract.View.MESSAGE_DIALOG, "An application error occurred. Please try again.");
                }
            });
        }
    }

    @Override
    public void sendFPRequest(String email) {
        // Validate email address
        boolean emailValid = validationService.validateEmail(email);

        if (!Utility.isInternetConnected((Activity) getView()))
            getView().showMessage(R.string.no_internet);
        else if (!emailValid || email.equalsIgnoreCase("")) {
            getView().showMessage(LoginContract.View.MESSAGE_DIALOG, "Invalid Email Provided");
        } else {
            restService.forgotPassword(email).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {

                }
            });
        }
    }

    @Override
    public void loginFirebaseAuth(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Get the firebase UID and persist it
                Application.getActiveUser().setFirebaseUID(task.getResult().getUser().getUid());
                new AppExecutors().diskIO().execute(() -> db.userDao().updateUser(Application.getActiveUser()));
            } else {
                // TODO Put on a future thread to execute again later in the application.
            }
        });
    }
}

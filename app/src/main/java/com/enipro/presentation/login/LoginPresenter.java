package com.enipro.presentation.login;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.AuthResponse;
import com.enipro.data.remote.model.UserCred;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.DataValidator;
import com.enipro.model.Enipro;
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
            UserCred cred = new UserCred();
            cred.setEmail(emailAddress);
            cred.setPassword(password);
            getView().showProgress();
            restService.auth_token(cred).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                    // Dismiss progress.
                    getView().dismissProgress();
                    if (response.isSuccessful()) {
                        AuthResponse authResponse = response.body();
                        Application.setAuthToken(authResponse.getToken());

                        // Save the token in shared pref.
                        // TODO A getUser and updateUser is sent in here. Not cool.
                        addDisposable(restService.getUser(authResponse.getUserId(), Application.getAuthToken())
                                .subscribeOn(ioScheduler)
                                .observeOn(mainScheduler)
                                .subscribe(appUser -> {
                                    appUser.setFirebaseToken(Utility.getTokenFromSharedPref(context));
                                    addDisposable(restService.updateUser(appUser, appUser.get_id().getOid(), Application.getAuthToken())
                                            .subscribeOn(ioScheduler)
                                            .observeOn(mainScheduler)
                                            .subscribe(user -> {
                                                // Persist user information in mobile datastore.
                                                new AppExecutors().diskIO().execute(() -> {
                                                    appUser.setActive(true); // TODO Check what is going on here.
                                                    Application.setActiveUser(appUser); // Set the active user of the application.
                                                    db.user().insertUser(appUser); // Execute the operation on the diskIO thread.
                                                });

                                                // Login user information into Firebase Auth using Email/Password Authentication.
                                                loginFirebaseAuth(emailAddress, password);

                                                // Open application
                                                getView().openApplication(appUser);
                                            }));
                                }));
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable throwable) {
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
                new AppExecutors().diskIO().execute(() -> db.user().updateUser(Application.getActiveUser()));
            } else {
                // TODO Put on a future thread to execute again later in the application.
            }
        });
    }
}

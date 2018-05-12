package com.enipro.presentation.signup;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.ChatUser;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.DataValidator;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.model.ValidationService;
import com.enipro.presentation.base.BasePresenter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupPresenter extends BasePresenter<SignupContract.View> implements SignupContract.Presenter, DataValidator {

    private Map<String, View> views = new HashMap<>();
    private ValidationService validationService;
    private EniproDatabase db;
    private FirebaseAuth mAuth;
    private Context context;

    public SignupPresenter(EniproRestService restService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler, ValidationService validationService, EniproDatabase local_db, Context context) {
        super(restService, ioScheduler, mainScheduler);
        this.validationService = validationService;
        this.db = local_db;
        mAuth = FirebaseAuth.getInstance(); // Initialise firebase authentication.
        this.context = context;
    }

    @Override
    public void attachViewItems(String itemName, View view) {
        views.put(itemName, view);
    }

    @Override
    public void validate(String[] data) {
        checkViewAttached(); // View must be attached to presenter before any action can be performed.

        // Before validating an verifying, presence of internet connection has to be gotten.
        if (!Utility.isInternetConnected(((Activity) getView())))
            getView().showMessage(R.string.no_internet);
        else {

            // User data gotten from text fields.
            String first_name = data[0], last_name = data[1], email = data[2], password = data[3];

            // TODO Check if conversion to lowercase broke anything
            String userType = getView().getSpinnerData(SignupContract.View.SPINNER_USER_TYPE).toLowerCase();

            // Validate all data fields.
            // Email validation
            boolean emailValid = validationService.validateEmail(email);
            if (!emailValid || email.equalsIgnoreCase("")) {
                getView().setViewError(views.get(SignupContract.View.EMAIL), "Please enter a valid email.");
                return;
            }

            // Password validation
            boolean passwordValid = validationService.validatePassword(password);
            if (!passwordValid || password.equalsIgnoreCase("")) {
                getView().showMessage(SignupContract.View.MESSAGE_DIALOG, ((Activity) getView()).getResources().getString(R.string.invalid_pass));
                return;
            }

            // Create a user object and persist data in the database of the API
            User user = new User(first_name, last_name, email, true, password, userType);
            getView().advanceProcess(user); // Advance the sign up process to next item.
        }
    }


    /**
     * The current sign up process is first of all creating a user in firebase AUTH, then adding the user information in
     * Firebase Realtime Database for chat purposes then on Complete of that, sending user information to
     * Enipro API to persist it.
     *
     * @param user the user information to persist.
     */
    @Override
    public void persistUser(User user) {
        signUpFirebase(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        persistFirebaseDatabase(user, task)
                                .addOnCompleteListener(task1 -> {
                                    // Send the user information to Enipro when task is passed.
                                    if (task1.isSuccessful()) {
                                        Log.d(Application.TAG, "Added user to firebase database");
                                        persistEniproAPI(user);
                                    }
                                })
                                .addOnFailureListener(e -> Log.d(Application.TAG, "Error Occurred:" + e.getMessage()));
                    } else {
                        // TODO Task is not successful. Do Something
                    }
                })
                .addOnFailureListener(e -> {
                });
    }


    @Override
    public void persistAvatarFirebase(User user, ImageView imageView, StorageReference storageReference, LocalCallback<User> userLocalCallback) {
        getView().showProgress(); // Show progress

        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Storage metadata for the avatar file
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UploadTask uploadTask = storageReference.putBytes(data, storageMetadata);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads

        }).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL and update user avatar.
            String avatar_url = taskSnapshot.getDownloadUrl().toString();
            user.setAvatar(avatar_url); // Update avatar url of the user object.
            userLocalCallback.respond(user);
        });
    }

    /**
     * @param email
     * @param password
     * @return
     */
    public Task<AuthResult> signUpFirebase(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    /**
     * Persist the user chat information which is (email, firebaseToken and firebaseUID) in Firebase
     * Realtime Database
     *
     * @param user the user information
     * @param task the task that contains the auth data (FirebaseUser).
     * @return a firebase task after creating the user in the database.
     */
    private Task<Void> persistFirebaseDatabase(User user, Task<AuthResult> task) {
        // Add user to firebase database
        FirebaseUser firebaseUser = task.getResult().getUser();
        // Update the user object to persist the firebase UID and firebase token
        user.setFirebaseUID(firebaseUser.getUid());
        user.setFirebaseToken(Utility.getTokenFromSharedPref(context));

        ChatUser chatUser = new ChatUser(firebaseUser.getUid(), user.getEmail(), user.getFirebaseToken());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        return database.child(Constants.ARG_USERS).child(firebaseUser.getUid()).setValue(chatUser);
    }


    /**
     * Sends the user information to Enipro Web API.
     *
     * @param user the user to persist in Enipro.
     */
    private void persistEniproAPI(final User user) {
        restService.signup(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                getView().dismissProgress(); // Dismiss progress dialog.
                if (response.isSuccessful()) {
                    // Persist user information in mobile data store
                    new AppExecutors().diskIO().execute(() -> {
                        User activeUser = response.body();
                        activeUser.setActive(true); // TODO Check what is going on here.
                        Application.setActiveUser(activeUser); // Set the active user of the application.
                        db.userDao().insertUser(activeUser); // Execute the operation on the diskIO thread.
                    });
                    // Open application
                    getView().openApplication(response.body());
                } else {
                    Log.d("Application", "404 Error Occurred.");
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        Log.d(Application.TAG, jsonObject.getString("errors"));
                        getView().showMessage(SignupContract.View.MESSAGE_DIALOG, jsonObject.getString("errors"));
                    } catch (IOException | JSONException io_json) {
                        Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable throwable) {
                getView().dismissProgress(); // Dismiss progress dialog.
                Log.d("Application", call.request().body().toString());
                Log.d("Application", call.request().headers().toString());
                Log.d("Application", throwable.getMessage());
                getView().showMessage(SignupContract.View.MESSAGE_DIALOG, "An application error occurred. Please try again.");
            }
        });
    }
}
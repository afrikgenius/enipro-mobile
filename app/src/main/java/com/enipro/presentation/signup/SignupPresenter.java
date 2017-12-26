package com.enipro.presentation.signup;


import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.DataValidator;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.model.ValidationService;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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


    public SignupPresenter(EniproRestService restService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler, ValidationService validationService, EniproDatabase local_db) {
        super(restService, ioScheduler, mainScheduler);
        this.validationService = validationService;
        this.db = local_db;
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
            String first_name = data[0], last_name = data[1], mobile_number = data[2], email = data[3], password = data[4];
            String userType = getView().getSpinnerData(SignupContract.View.SPINNER_USER_TYPE);

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

            // User type validation
            boolean found = false;
            String[] usertypes = ((Activity) getView()).getResources().getStringArray(R.array.user_type);
            for(int i = 1;i < usertypes.length;i++){
                if(userType.equalsIgnoreCase(usertypes[i])){
                    found = true;
                    break;
                }
            }
            if(!found){
                getView().showMessage(SignupContract.View.MESSAGE_DIALOG, "Please select a valid user type from the list.");
                return;
            }

//            getView().showProgress();  // Open progress dialog // TODO No point of doing this but might be useful later.

            // Create a user object and persist data in the database of the API
            User user = new User(first_name, last_name, email, true, password, userType);
            getView().advanceProcess(user); // Advance the sign up process to next item.
        }
    }


    @Override
    public void persistUser(User user){
        restService.signup(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,@NonNull Response<User> response) {
                getView().dismissProgress(); // Dismiss progress dialog.
                if(response.isSuccessful()) {
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
                    try{
                        jsonObject = new JSONObject(response.errorBody().string());
                        getView().showMessage(SignupContract.View.MESSAGE_DIALOG, jsonObject.getString("description"));
                    } catch (IOException | JSONException io_json){
                        Log.e(Enipro.APPLICATION + ":" + ((Activity) getView()).getLocalClassName(), io_json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                getView().dismissProgress(); // Dismiss progress dialog.
                Log.d("Application", call.request().body().toString());
                Log.d("Application", call.request().headers().toString());
                Log.d("Application", throwable.getMessage());
                getView().showMessage(SignupContract.View.MESSAGE_DIALOG, "An application error occurred. Please try again.");
            }
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
            user.setAvatar(avatar_url);
            userLocalCallback.respond(user);
        });
    }
}
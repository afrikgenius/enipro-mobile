package com.enipro.presentation.signup;


import android.widget.ImageView;

import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;
import com.google.firebase.storage.StorageReference;

public class SignupContract {


    interface View extends MvpView {

        String SPINNER_USER_TYPE = "USER_TYPE";
        String SPINNER_COUNTRY_CODE = "COUNTRY_CODE";
        String EMAIL = "email";
        String PASSWORD = "password";
        String MESSAGE_SNACKBAR = "Snack";
        String MESSAGE_DIALOG = "Dialog";

        /**
         * Sends error messages as snack bar messages.
         *
         * @param message the message to send.
         */
        void showMessage(int message);

        void setViewError(android.view.View view, String errorMessage);

        void showMessage(String type, String message);

        void showMessageDialog(int title, int message);

        void showMessageDialog(int title, String message);

        void showMessageDialog(String title, int message);

        void showMessageDialog(String title, String message);

        void showProgress();

        void dismissProgress();

        String getSpinnerData(String spinner_name);

        void openApplication(User user);

        /**
         * Advance the sign up process passing a user object with fields added.
         *
         * @param user the user object to pass
         */
        void advanceProcess(User user);
    }

    interface Presenter extends MvpPresenter<View> {

        void attachViewItems(String itemName, android.view.View view);

        void persistUser(User user);

        /**
         * Persists the user profile avatar in firebase storage and saves it in the user avatar property and
         * returns the new user object
         *
         * @param user             the user profile avatar to persist
         * @param imageView        the imageview to grab avatar from
         * @param storageReference storage reference to use in firebase
         */
        void persistAvatarFirebase(User user, ImageView imageView, StorageReference storageReference, LocalCallback<User> userLocalCallback);
    }
}

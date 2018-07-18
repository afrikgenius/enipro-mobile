package com.enipro.presentation.login;


import com.enipro.data.remote.model.User;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

public class LoginContract {

    interface View extends MvpView{

        String EMAIL = "email";
        String PASSWORD = "password";
        String MESSAGE_SNACKBAR = "Snack";
        String MESSAGE_DIALOG = "Dialog";

        /**
         * Sends error messages as snack bar messages.
         * @param message the message to send.
         */
        void showMessage(int message);

        void setViewError(android.view.View view, String errorMessage);

        void showMessage(String type, String message);

        void showProgress();

        void dismissProgress();

        void openApplication(User user);
    }

    interface Presenter extends MvpPresenter<View>{

        void attachViewItems(String itemName, android.view.View view);

        /**
         * Send forgot password request to API
         * @param email the email address
         */
        void sendFPRequest(String email);

        /**
         * Logs in using firebase auth using Email/Password Authentication.
         * @param email the email address to use
         * @param password the password.
         */
        void loginFirebaseAuth(String email, String password);
    }
}

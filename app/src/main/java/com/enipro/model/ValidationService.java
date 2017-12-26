package com.enipro.model;

import android.view.View;

import com.enipro.presentation.base.MvpPresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Agbanagba Oghenetega
 */
public class ValidationService extends ApplicationService {

    /* Private Instance Variables */

    /** View on click listener for registering view on the validation service. */
    public static final String LISTENER_VIEW_ON_CLICK = "View.OnClickListener";


    // Minimum length of password that a user can have
    private static final int MIN_LENGTH_PASSWORD = 8;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-zA-Z])(?=.*[!/\\-$%^&*()_+|~=`{}\\[\\]:\";\\'<>?,.@#\\\\/]).{8,20})";

    // pattern object for compiling regex for email validation.
    private static Pattern email_pattern;

    // pattern object for compiling regex for password validation
    private static Pattern pass_pattern;

    public ValidationService(){
        email_pattern = Pattern.compile(EMAIL_PATTERN);
        pass_pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    /**
     * Validates the email address of a user using email regular expression.
     *
     * @param email the email address
     * @return true if the email address is valid.
     */
    public boolean validateEmail(String email) {

        Matcher matcher = email_pattern.matcher(email);
        return matcher.matches();
    }


    /**
     * Validates the password of a user which must conform to a password policy.
     * The password policy is that the password must be 8 to 20 characters and the entropy must reach
     * a particula value.
     *
     * @param password the password of the user.
     * @return true if the password is valid and strong.
     */
    public boolean validatePassword(String password) {
        Matcher matcher = pass_pattern.matcher(password);
        return matcher.matches();
    }


    @Override
    public void register(MvpPresenter presenter, View view, String listener, final EditTextDataExtractor _extractor) throws Exception {
        switch (listener) {
            case LISTENER_VIEW_ON_CLICK:
                // Activity must implement DataValidator class.
                final DataValidator validator = presenter instanceof DataValidator ? ((DataValidator) presenter) : null;
                if (validator == null)
                    throw new Exception("Activity must implement data validator interface.");
                view.setOnClickListener(v -> {
                        validator.validate(_extractor.extract());
                });
                break;
        }

    }


}

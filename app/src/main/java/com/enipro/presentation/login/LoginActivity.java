package com.enipro.presentation.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.ApplicationService;
import com.enipro.model.Enipro;
import com.enipro.model.ServiceType;
import com.enipro.model.ValidationService;
import com.enipro.presentation.home.HomeActivity;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.signup.SignUpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import com.enipro.model.EditTextDataExtractor;

public class LoginActivity extends FragmentActivity implements LoginContract.View {


    private LoginContract.Presenter loginPresenter;

    private CoordinatorLayout coordinatorLayout;

    private MaterialDialog progressDialog;

    /* Edit text used to collect email address from the user. */
    @BindView(R.id.editTxtEmailAddressLogin) TextInputLayout emailTextInputLayout;

    /* Edit text used to collect password from user*/
    @BindView(R.id.editTxtPasswordLogin) TextInputLayout passTextInputLayout;

    // Edit text for password on login screen
    private EditText passEditText;
    private EditText emailEditText;

    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        emailEditText = emailTextInputLayout.getEditText();
        passEditText = passTextInputLayout.getEditText();

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);

        //Layout for snack bar errors and messages.
        coordinatorLayout = findViewById(R.id.snackbar_container);

        // Extract data from edit text.
        int[] editTextIds = {R.id.emailAddressLogin, R.id.passwordLogin};
        EditTextDataExtractor _extractor = new EditTextDataExtractor(this, editTextIds);

        // Register a validation service for data validation.
        ValidationService validationService = (ValidationService) ApplicationService.getInstance(ServiceType.ValidationService);

        // Initialise presenter and attach view.
        loginPresenter = new LoginPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), validationService,
                EniproDatabase.getInstance(this));
        loginPresenter.attachView(this);

        // Attach view items to presenter
        loginPresenter.attachViewItems(EMAIL, emailEditText);
        loginPresenter.attachViewItems(PASSWORD, passEditText);

        // Register the validation service.
        try {
            validationService.register(getPresenter(), findViewById(R.id.btnLoginInLogin),
                    validationService.LISTENER_VIEW_ON_CLICK, _extractor);
        } catch (Exception e) {
            Log.e("ERROR", "Activity did not implement DataValidator");
        }

        TextView txtSignUp = (TextView) findViewById(R.id.clickable_sign_up_text);
        txtSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Launches the forgot password dialog when the text view is clicked for password retrieval.
        findViewById(R.id.forgot_password).setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .title(R.string.forgot_pass)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .input(R.string.input_content, R.string.empty, (materialDialog, charSequence) ->  {
                        // Call presenter to send message to API
                        loginPresenter.sendFPRequest(charSequence.toString());
                    })
                    .show();

        });


        // When the user puts in the email address and the email is wrong on change focus to
        // another view, an invalid error message is thrown.
        emailEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String emailAddress = emailEditText.getText().toString();
                boolean emailValid = validationService.validateEmail(emailAddress);
                if (!emailValid || emailAddress.equalsIgnoreCase(""))
                    emailEditText.setError("Please enter a valid email");
            }
        });
    }

    @Override
    public void showMessage(int message) {
        Snackbar snack = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        ((TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(getResources().getColor(R.color.white));
        snack.show();
    }

    @Override
    public void showMessage(String type, String message) {
        switch (type){
            case MESSAGE_SNACKBAR:
                Snackbar snack = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
                ((TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text))
                        .setTextColor(getResources().getColor(R.color.white));
                snack.show();
                break;
            case MESSAGE_DIALOG:
                // OPEN A DIALOG WITH THE MESSAGE
                new MaterialDialog.Builder(this)
                        .title(R.string.incorrect)
                        .content(message)
                        .positiveText(R.string.ok)
                        .show();
                break;
        }
    }

    @Override
    public void openApplication(User user) {
        Intent intent = HomeActivity.newIntent(this);
        intent.putExtra(HomeActivity.EXTRA_DATA, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showProgress() {
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void setViewError(View view, String errorMessage) {
        if(view == emailEditText || view == passEditText)
            ((EditText) view).setError(errorMessage);
    }

    @Override
    public LoginContract.Presenter getPresenter() {
        return loginPresenter;
    }

    @Override
    protected void onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.detachView();
    }
}
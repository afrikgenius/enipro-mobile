package com.enipro.presentation.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.ApplicationService;
import com.enipro.model.EditTextDataExtractor;
import com.enipro.model.ServiceType;
import com.enipro.model.ValidationService;
import com.enipro.presentation.home.HomeActivity;
import com.enipro.presentation.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class SignUpActivity extends FragmentActivity implements SignupContract.View {

    private EditText emailEditText, passwordEditText, firstNameEditText, lastNameEditText, mobileEditText;

    private CoordinatorLayout coordinatorLayout;

    private SignupContract.Presenter presenter;

    private MaterialDialog progressDialog;

    // Spinners that hold country code and user type.
    private Spinner country_code;
    private Spinner user_type;

    @BindView(R.id.first_name_layout) TextInputLayout firstNameTextInputLayout;
    @BindView(R.id.last_name_layout) TextInputLayout lastNameTextInputLayout;
    @BindView(R.id.signUpEmail_Layout) TextInputLayout emailTextInputLayout;
    @BindView(R.id.mobile_number_layout) TextInputLayout mobileTextInputLayout;
    @BindView(R.id.signUpPassword_Layout) TextInputLayout passTextInputLayout;

    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SignUpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);
        coordinatorLayout = findViewById(R.id.snackbar_container);

        emailEditText = emailTextInputLayout.getEditText();
        firstNameEditText = firstNameTextInputLayout.getEditText();
        lastNameEditText = lastNameTextInputLayout.getEditText();
        mobileEditText = mobileTextInputLayout.getEditText();
        passwordEditText = passTextInputLayout.getEditText();

        // Get login data input from the user. Data is retrieved from presenter the way it is inserted into extractor.
        int[] editTextIds = {R.id.first_name, R.id.last_name, R.id.mobile_number, R.id.signUpEmail, R.id.signUpPassword};
        EditTextDataExtractor _extractor = new EditTextDataExtractor(this, editTextIds);

        ValidationService validationService = (ValidationService) ApplicationService.getInstance(ServiceType.ValidationService);
        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), validationService,
                EniproDatabase.getInstance(this));
        presenter.attachView(this);

        // Attach view items to presenter
        presenter.attachViewItems(EMAIL, emailEditText);
        presenter.attachViewItems(PASSWORD, passwordEditText);

        // Register a validation service for data validation.
        try {
            validationService.register(getPresenter(), findViewById(R.id.btnSignUpInSignUp),
                    ValidationService.LISTENER_VIEW_ON_CLICK, _extractor);
        } catch (Exception e) {
            Log.e("ERROR", "Activity did not implement DataValidator");
        }

        // Clickable button for signing in redirects the user to the login activity.
        TextView txtSignIn = findViewById(R.id.clickable_sign_in_text);
        txtSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        // Country codes for mobile number
        country_code = findViewById(R.id.country_code_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_codes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_code.setAdapter(adapter);

        // Spinner adapter for user types
        user_type = findViewById(R.id.user_type);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.user_type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_type.setAdapter(typeAdapter);
    }

    @Override
    public SignupContract.Presenter getPresenter() {
        return presenter;
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
                        .content(message)
                        .positiveText(R.string.ok)
                        .show();
                break;
            default: // Do nothing.
        }
    }

    @Override
    public void showMessageDialog(int title, int message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void showMessageDialog(int title, String message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void showMessageDialog(String title, int message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void showMessageDialog(String title, String message) {
        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(R.string.ok)
                .show();
    }

    @Override
    public void setViewError(View view, String errorMessage) {
        if(view == emailEditText || view == passwordEditText)
            ((EditText) view).setError(errorMessage);
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
    public String getSpinnerData(String spinner_name) {
        String spinner_data = null;
        switch(spinner_name){
            case SPINNER_USER_TYPE:
                spinner_data = user_type.getSelectedItem().toString();
                break;
            case SPINNER_COUNTRY_CODE:
                spinner_data = country_code.getSelectedItem().toString();
                break;
        }
        return spinner_data;
    }

    @Override
    public void openApplication(User user) {
        Intent intent = HomeActivity.newIntent(this);
        intent.putExtra(HomeActivity.EXTRA_DATA, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void advanceProcess(User user) {
        // Open add photo activity passing the user object as a bundle
        Intent addPhotoIntent = AddPhotoActivity.newIntent(this);
        addPhotoIntent.putExtra(AddPhotoActivity.TAG, user);
        startActivity(addPhotoIntent);
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
        presenter.detachView();
    }
}
package com.enipro.presentation.signup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.enipro.R;
import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddExperienceActivity extends AppCompatActivity implements SignupContract.View {

    private SignupContract.Presenter presenter;

    public static final String TAG = ".AddExpeienceActivity";

    @BindView(R.id.industry)
    EditText industry;
    @BindView(R.id.org)
    EditText org;
    @BindView(R.id.role)
    EditText role;
    @BindView(R.id.start_year)
    EditText start_year;
    @BindView(R.id.end_year)
    EditText end_year;
    @BindView(R.id.continue_exp)
    Button continue_exp;
    @BindView(R.id.checkBox)
    CheckBox checkBox;


    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, AddExperienceActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_experience);
        ButterKnife.bind(this);

        // Get user object passed from previous activity
        User user = getIntent().getParcelableExtra(TAG);

        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.getInstance(this), this);
        presenter.attachView(this);

        continue_exp.setOnClickListener(view -> {

            // Grab all fields and attach then advance process

            String indust = industry.getText().toString();
            String organsation = org.getText().toString();
            String job_title = role.getText().toString();
            String start_yr = start_year.getText().toString();
            String end_yr;
            if (end_year.getVisibility() == View.GONE)
                end_yr = "Present";
            else
                end_yr = end_year.getText().toString();
            String headline = job_title + ", " + organsation;

            Experience experience = new Experience(indust, organsation, job_title, start_yr, end_yr);
            List<Experience> experiences = new ArrayList<>();
            experiences.add(experience);
            user.setExperiences(experiences);
            user.setHeadline(headline);
            advanceProcess(user);
        });

        start_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, start_year));
        end_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, end_year));
        checkBox.setOnClickListener(view -> {
            if (((CheckBox) view).isChecked()) {
                end_year.setVisibility(View.GONE);
            } else
                end_year.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void openApplication(User user) {

    }

    @Override
    public SignupContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showMessage(int message) {
    }

    @Override
    public void showMessage(String type, String message) {
    }

    @Override
    public void showMessageDialog(int title, int message) {
    }

    @Override
    public void showMessageDialog(int title, String message) {
    }

    @Override
    public void showMessageDialog(String title, int message) {
    }

    @Override
    public void showMessageDialog(String title, String message) {
    }

    @Override
    public void setViewError(View view, String errorMessage) {
    }

    @Override
    public String getSpinnerData(String spinner_name) {
        return null;
    }

    @Override
    public void advanceProcess(User user) {
        // Open add photo activity passing the user object as a bundle
        Intent intent = AddInterestsActivity.newIntent(this);
        intent.putExtra(AddInterestsActivity.TAG, user);
        startActivity(intent);
    }
}

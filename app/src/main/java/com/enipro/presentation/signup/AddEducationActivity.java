package com.enipro.presentation.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.enipro.R;
import com.enipro.data.remote.model.Education;
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

public class AddEducationActivity extends AppCompatActivity implements SignupContract.View {

    private SignupContract.Presenter presenter;

    public static final String TAG = ".AddEducationActivity";

    @BindView(R.id.continue_edu)
    Button continue_edu;
    @BindView(R.id.start_year)
    EditText start_year;
    @BindView(R.id.end_year)
    EditText end_year;
    @BindView(R.id.course)
    EditText course;
    @BindView(R.id.university)
    EditText university;


    public static Intent newIntent(Context context) {
        return new Intent(context, AddEducationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_education);
        ButterKnife.bind(this);

        // Get user object passed from previous activity
        User user = getIntent().getParcelableExtra(TAG);

        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.Companion.getInstance(this), this);
        presenter.attachView(this);

        continue_edu.setOnClickListener(view -> {
            // Grab all fields
            String uni = university.getText().toString();
            String major = course.getText().toString();
            String start_yr = start_year.getText().toString();
            String end_yr = end_year.getText().toString();
            String degree = "Bachelors of Science";

            String headline = "Student, " + uni;
            Education education = new Education(uni, major, degree, start_yr, end_yr);
            List<Education> educationList = new ArrayList<>();
            educationList.add(education);
            user.setEducation(educationList);
            user.setHeadline(headline);
            advanceProcess(user);
        });

        start_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, start_year));
        end_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, end_year));
    }


    @Override
    public SignupContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void showMessage(int message) {
    }

    @Override
    public void openApplication(User user) {

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
        Intent intent = AddInterestsActivity.newIntent(this);
        intent.putExtra(AddInterestsActivity.TAG, user);
        startActivity(intent);
    }
}

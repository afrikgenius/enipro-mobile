package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.data.remote.model.Education;
import com.enipro.model.Constants;
import com.enipro.model.Utility;

public class NewEducationActivity extends AppCompatActivity {


    private EditText school, degree, course;

    EditText start_year;
    EditText end_year;
    TextInputLayout school_layout;
    TextInputLayout degree_layout;
    TextInputLayout course_layout;
    Button save;


    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, NewEducationActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_education);

        start_year = findViewById(R.id.start_year);
        end_year = findViewById(R.id.end_year);
        school_layout = findViewById(R.id.school);
        degree_layout = findViewById(R.id.degree);
        course_layout = findViewById(R.id.course);
        save = findViewById(R.id.save);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // TODO Change the title of this
        getSupportActionBar().setTitle(R.string.edit_profile);

        school = school_layout.getEditText();
        degree = degree_layout.getEditText();
        course = course_layout.getEditText();

        start_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, start_year));
        end_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, end_year));
        save.setOnClickListener(view -> {
            // Check if profile has changed else return.
            if (!profileChanged())
                return;

            Education education = validateData();
            if (education != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EDUCATION_EXTRA, education);
                setResult(Constants.ADD_EDUCATION, resultIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Before back button is pressed, check if there is a change in profile information.
                if (profileChanged()) {
                    new MaterialDialog.Builder(this)
                            .content(R.string.unsaved_changes)
                            .positiveText(R.string.cancel)
                            .negativeText(R.string.discard)
                            .onNegative((dialog, which) -> finish())
                            .show();
                } else finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the profile information has changed from the information of the profile user
     * by checking all fields with data to see if their content was altered.
     *
     * @return true if profile info has been changed and false otherwise.
     */
    private boolean profileChanged() {
        return (!school.getText().toString().equals("") ||
                !degree.getText().toString().equals("") ||
                !course.getText().toString().equals("") ||
                !start_year.getText().toString().equals("") ||
                !end_year.getText().toString().equals(""));
    }

    private Education validateData() {
        // This is done to make sure fields are not empty
        String school_str, degree_str, course_str, start_yr, end_yr;
        school_str = school.getText().toString();
        degree_str = degree.getText().toString();
        course_str = course.getText().toString();
        start_yr = start_year.getText().toString();
        end_yr = end_year.getText().toString();
        if (school_str.equals("")) {
            school.setError("This field is required");
            return null;
        }
        if (degree_str.equals("")) {
            degree.setError("This field is required");
            return null;
        }
        if (course_str.equals("")) {
            course.setError("This field is required");
            return null;
        }
        if (start_yr.equals("")) {
            start_year.setError("This field is required");
            return null;
        }
        if (end_yr.equals("")) {
            end_year.setError("This field is required");
            return null;
        }
        return new Education(school_str, course_str, degree_str, start_yr, end_yr);
    }
}

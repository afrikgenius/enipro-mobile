package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.model.Constants;
import com.enipro.model.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewExperienceActivity extends AppCompatActivity {


    private EditText industry, organisation, role;

    @BindView(R.id.start_year)
    EditText start_year;
    @BindView(R.id.end_year)
    EditText end_year;
    @BindView(R.id.industry)
    TextInputLayout industry_layout;
    @BindView(R.id.org)
    TextInputLayout org_layout;
    @BindView(R.id.role)
    TextInputLayout role_layout;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.checkBox)
    CheckBox checkBox;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, NewExperienceActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experience);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // TODO Change the title of this
        getSupportActionBar().setTitle(R.string.edit_profile);

        industry = industry_layout.getEditText();
        organisation = org_layout.getEditText();
        role = role_layout.getEditText();

        start_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, start_year));
        end_year.setOnClickListener(view -> Utility.showDatePickerDialog(this, end_year));
        save.setOnClickListener(view -> {
            // Check if profile has changed else return.
            if (!profileChanged())
                return;

            Experience experience = validateData();
            if (experience != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.EXPERIENCE_EXTRA, experience);
                setResult(Constants.ADD_EXPERIENCE, resultIntent);
                finish();
            }
        });

        checkBox.setOnClickListener(view -> {
            if (((CheckBox) view).isChecked()) {
                end_year.setVisibility(View.GONE);
            } else
                end_year.setVisibility(View.VISIBLE);
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
        return (!industry.getText().toString().equals("") ||
                !organisation.getText().toString().equals("") ||
                !role.getText().toString().equals("") ||
                !start_year.getText().toString().equals("") ||
                !end_year.getText().toString().equals(""));
    }

    private Experience validateData() {
        // This is done to make sure fields are not empty
        String industry_str, org_str, role_str, start_yr, end_yr;
        industry_str = industry.getText().toString();
        org_str = organisation.getText().toString();
        role_str = role.getText().toString();
        start_yr = start_year.getText().toString();
        if (end_year.getVisibility() == View.GONE)
            end_yr = "Present";
        else
            end_yr = end_year.getText().toString();
        if (industry_str.equals("")) {
            industry.setError("This field is required");
            return null;
        }
        if (org_str.equals("")) {
            organisation.setError("This field is required");
            return null;
        }
        if (role_str.equals("")) {
            role.setError("This field is required");
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
        return new Experience(industry_str, org_str, role_str, start_yr, end_yr);
    }
}
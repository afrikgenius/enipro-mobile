package com.enipro.presentation.requests.session_schedule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.enipro.R;
import com.enipro.data.remote.model.SessionSchedule;
import com.enipro.data.remote.model.SessionTiming;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity displays view to select a schedule for a mentoring session between a professional and a student.
 * The schedule is then attached to the request information and it is used to trigger alarm manager of the device when
 * the scheduled time is approaching.
 */
public class SessionScheduleActivity extends AppCompatActivity {


    private static final String PICKER_DIALOG_TAG = "com.enipro.presentation.requests.session_schedule.SessionScheduleActivity";
    @BindView(R.id.from_time)
    EditText from_time;

    @BindView(R.id.to_time)
    EditText to_time;

    @BindView(R.id.monday_chkbox)
    CheckBox monday_checkbox;
    @BindView(R.id.tuesday_chkbox)
    CheckBox tuesday_checkbox;
    @BindView(R.id.wed_chkbox)
    CheckBox wednesday_checkbox;
    @BindView(R.id.thurs_chkbox)
    CheckBox thursday_checkbox;
    @BindView(R.id.friday_chkbox)
    CheckBox friday_checkbox;
    @BindView(R.id.sat_chkbox)
    CheckBox saturday_checkbox;
    @BindView(R.id.sun_chkbox)
    CheckBox sunday_checkbox;

    @BindView(R.id.send_request)
    Button send;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout layout;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SessionScheduleActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_schedule);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        init();

        // Handle what happens when the send button is clicked.
        send.setOnClickListener((View view) -> {

            // If no checkbox item has been checked, flag an error
            List<String> days = getDaysPicked();
            String from = from_time.getText().toString(); // The time from and to are in 24 hour mode
            String to = to_time.getText().toString();

            // Check number of days selected.
            if (days.size() == 0) {
                // No day selected for schedule, Display
                Utility.showSnackBar(layout, getString(R.string.choose_a_date), true);
                return;
            }
            // Check time selected if not empty
            if (from.isEmpty() || to.isEmpty()) {
                Utility.showSnackBar(layout, getString(R.string.select_time), true);
                return;
            }

            // Also check time frame length, start time must be less than end time
            String[] from_vals = from.split(":"); // Contains two elements. Element[0] == Hour Value, Element[1] = Minute value
            String[] to_vals = to.split(":");
            if (Integer.valueOf(from_vals[0]).intValue() == Integer.valueOf(to_vals[0]).intValue()) {
                // Check the minute parameter
                if (Integer.valueOf(from_vals[1]) >= Integer.valueOf(to_vals[1])) {
                    Utility.showSnackBar(layout, getString(R.string.start_time_less), true);
                    return;
                }
            } else if (Integer.valueOf(from_vals[0]) > Integer.valueOf(to_vals[0])) {
                Utility.showSnackBar(layout, getString(R.string.start_time_less), true);
                return;
            }


            // Create a session schedule and timing object with the data
            SessionTiming timing = new SessionTiming(days, from, to);
            SessionSchedule schedule = new SessionSchedule("2018-03-30T20:03:50.864Z", "2018-03-30T20:03:50.864Z", timing);
            Intent scheduleReturnIntent = new Intent();
            scheduleReturnIntent.putExtra(Constants.SESSION_SCHEDULE_DATA, Parcels.wrap(schedule));
            setResult(RESULT_OK, scheduleReturnIntent);
            finish();
        });
    }

    /**
     * Initializes action of several view items in the activity.
     */
    private void init() {
        from_time.setOnClickListener(view -> timePicker(from_time));
        to_time.setOnClickListener(view -> timePicker(to_time));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }


    /**
     * Get the days picked by the user.
     *
     * @return the days picked as a list of string.
     */
    private List<String> getDaysPicked() {
        List<String> days = new ArrayList<>();
        if (monday_checkbox.isChecked())
            days.add(Constants.MONDAY);
        if (tuesday_checkbox.isChecked())
            days.add(Constants.TUESDAY);
        if (wednesday_checkbox.isChecked())
            days.add(Constants.WEDNESDAY);
        if (thursday_checkbox.isChecked())
            days.add(Constants.THURSDAY);
        if (friday_checkbox.isChecked())
            days.add(Constants.FRIDAY);
        if (saturday_checkbox.isChecked())
            days.add(Constants.SATURDAY);
        if (sunday_checkbox.isChecked())
            days.add(Constants.SUNDAY);
        return days;
    }

    /**
     * Opens the time picker dialog to select a time
     */
    private void timePicker(EditText editText) {
        TimePickerDialog tpd = TimePickerDialog.newInstance((view, hourOfDay, minute, second) -> {
            String hour = String.valueOf(hourOfDay);
            String min = String.valueOf(minute);
            // If the hour and minute value are less than 10, Add a zero digit behind
            if (hourOfDay < 10)
                hour = "0" + hour;
            if (minute < 10)
                min = "0" + min;
            // Sets the field passed into time picker with the data from the on time set listener here.
            String time = hour + ":" + min;
            editText.setText(time);
        }, false);
        tpd.show(getFragmentManager(), PICKER_DIALOG_TAG);
    }
}

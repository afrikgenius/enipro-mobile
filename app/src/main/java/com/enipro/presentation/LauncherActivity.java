package com.enipro.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.AppLaunchChecker;
import android.support.v7.app.AppCompatActivity;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.presentation.home.HomeActivity;
import com.enipro.presentation.login.LoginActivity;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.signup.SignUpActivity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LauncherActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 2000;
    private ScheduledExecutorService diskIOExecutor;
    private Runnable mRunnable;


    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, LauncherActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // Make activity have a fade out transition to another activity.
        overridePendingTransition(0, R.anim.fade_out);

        // Check Local Storage for any user. If user found, open home else open sign up.
        EniproDatabase db = EniproDatabase.getInstance(this);

        // Run operation in a different thread than the UI thread.
        mRunnable = () -> {
            Intent intent;
            List<User> users = db.userDao().getUsers();

            if(users.size() == 1) {
                intent = HomeActivity.newIntent(this); // Launch Home activity
                intent.putExtra(Constants.APPLICATION_USER, users.get(0)); // Pass the application user down to home activity.
            } else
                intent = LoginActivity.newIntent(this);   // No user exists and launch sign up activity

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent); // Start activity in the intent
        };

        diskIOExecutor = AppExecutors.scheduledExecutorService();
        diskIOExecutor.schedule(mRunnable, SPLASH_TIME_MS, TimeUnit.MILLISECONDS);
    }


    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, R.anim.fade_out);
    }


    @Override
    protected void onStop() {
        super.onStop();
        diskIOExecutor.shutdownNow();
    }
}
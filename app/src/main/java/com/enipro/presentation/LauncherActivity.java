package com.enipro.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.presentation.home.HomeActivity;
import com.enipro.presentation.login.LoginActivity;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LauncherActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 2000;
    private ScheduledExecutorService diskIOExecutor;
    private Runnable mRunnable;


    public static Intent newIntent(Context context) {
        return new Intent(context, LauncherActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        overridePendingTransition(0, R.anim.fade_out);
        EniproDatabase db = EniproDatabase.Companion.getInstance(this);

        mRunnable = () -> {
            Intent intent;
            List<User> users = db.user().getUsers();

            if (users.size() == 1) {
                intent = HomeActivity.newIntent(this, users.get(0)); // Launch Home activity
//                intent.putExtra(Constants.APPLICATION_USER, Parcels.wrap(users.get(0))); // Pass the application user down to home activity.
            } else
                intent = LoginActivity.Companion.newIntent(this);   // No user exists and launch sign up activity

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
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
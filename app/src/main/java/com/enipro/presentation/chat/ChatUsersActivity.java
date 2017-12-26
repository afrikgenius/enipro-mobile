package com.enipro.presentation.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserType;
import com.enipro.injection.Injection;
import com.enipro.model.Enipro;
import com.enipro.model.Utility;
import com.enipro.presentation.feeds.FeedContract;
import com.enipro.presentation.feeds.FeedPresenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static java.security.AccessController.getContext;

public class ChatUsersActivity extends AppCompatActivity implements ChatContract.View {


    RecyclerView mRecyclerView;

    private ChatContract.Presenter presenter;
    private ChatUsersRecyclerAdapter adapter;


    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ChatUsersActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        presenter = new ChatPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);

        // Load all users in the users circle or network depending on the type of user
        presenter.loadChatUsers(Application.getActiveUser(), users -> {

            // Check number of users and unhide (No User information)
            Log.d(Application.TAG, "Number of users: " + users.size());
            if (users.size() == 0) {
                findViewById(R.id.no_chatuser_layout).setVisibility(View.VISIBLE);
                if(Application.getActiveUser().getUserType().equalsIgnoreCase(UserType.STUDENT)){
                    ((RobotoTextView)findViewById(R.id.no_chatuser_instructions)).setText(R.string.no_chat_inst_stu1);
                    ((RobotoTextView)findViewById(R.id.no_chatuser_instructions2)).setText(R.string.no_chat_inst_stu2);
                } else if(Application.getActiveUser().getUserType().equalsIgnoreCase(UserType.PROFESSIONAL)){
                    ((RobotoTextView)findViewById(R.id.no_chatuser_instructions)).setText(R.string.no_chat_inst_prof1);
                    ((RobotoTextView)findViewById(R.id.no_chatuser_instructions2)).setText(R.string.no_chat_inst_prof2);
                }

            } else {
                Log.d(Enipro.APPLICATION, "Result is not null");

                // Initialize adapter and load data on UI Thread in order to access it on the UI Thread
                // and not the thread that loaded the data from the API.
                runOnUiThread(() -> {
                    // Recycler view and adapter for news feed items
                    mRecyclerView = findViewById(R.id.chat_users_recycler);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(this));

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(linearLayoutManager);

                    adapter = new ChatUsersRecyclerAdapter(this, users);
                    mRecyclerView.setAdapter(adapter);
                });
            }
        });
    }
}

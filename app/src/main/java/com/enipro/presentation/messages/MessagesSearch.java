package com.enipro.presentation.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserType;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessagesSearch extends AppCompatActivity implements MessagesContract.SearchView {

    @BindView(R.id.messages_search_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.alternate_toolbar)
    Toolbar alternateToolbar;
    @BindView(R.id.message_search_toolbar)
    Toolbar messageSearchToolbar;

    private MessagesContract.SearchPresenter messagePresenter;
    private MessageSearchAdapter usersAdapter;
    private EditText search;
    private ImageButton search_clear;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, MessagesSearch.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_search);
        ButterKnife.bind(this);

        // On arrow back press, finish the activity.
        findViewById(R.id.arrow_back).setOnClickListener(v -> finish());

        // Message Presenter
        messagePresenter = new MessageSearchPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        messagePresenter.attachView(this);

        setSupportActionBar(messageSearchToolbar);

        search = findViewById(R.id.search);
        onSearchContentChanged(); // Set text changed listener to search edit text.

        // Empty the content of the search edit text when the clear button is clicked.
        search_clear = findViewById(R.id.search_clear);
        search_clear.setOnClickListener(v -> search.setText(""));

        // Recycler view and adapter for news feed items
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        messagePresenter.loadConnectedUsers();

        usersAdapter = new MessageSearchAdapter(null, this, (user) -> {
            // Return to the calling activity with the user in a result Intent
            // Send user information back to user search.
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.MESSAGE_SEARCH_RETURN_KEY, user);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        mRecyclerView.setAdapter(usersAdapter);
    }

    /**
     * Content change listener method that performs request to API when search content
     * is changed.
     */
    void onSearchContentChanged() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 0) {
                    search_clear.setVisibility(View.VISIBLE);
                    // Send to presenter to search for user with new text
                    messagePresenter.search(charSequence.toString());
                } else {
                    search_clear.setVisibility(View.GONE);
                    // TODO Reset user to all users in cirle or network
                    messagePresenter.loadConnectedUsers();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }
        });
    }

//    void initializeAlternateToolbar() {
//        setSupportActionBar(alternateToolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//    }

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

    @Override
    public void onConnectedUsersLoaded(List<User> users) {
        if (users.size() == 0) {
            findViewById(R.id.no_chatuser_layout).setVisibility(View.VISIBLE);
            if (Application.getActiveUser().getUserType().equalsIgnoreCase(UserType.INSTANCE.getSTUDENT())) {
                ((TextView) findViewById(R.id.no_chatuser_instructions)).setText(R.string.no_chat_inst_stu1);
                ((TextView) findViewById(R.id.no_chatuser_instructions2)).setText(R.string.no_chat_inst_stu2);
            } else if (Application.getActiveUser().getUserType().equalsIgnoreCase(UserType.INSTANCE.getPROFESSIONAL())) {
                ((TextView) findViewById(R.id.no_chatuser_instructions)).setText(R.string.no_chat_inst_prof1);
                ((TextView) findViewById(R.id.no_chatuser_instructions2)).setText(R.string.no_chat_inst_prof2);
            }
        } else {
            usersAdapter.setItems(users);
        }
    }


    @Override
    public void showSearchResults(List<User> userList) {
        usersAdapter.setItems(userList);
    }

    @Override
    public void showError(String message) {
        mRecyclerView.setVisibility(View.GONE);
    }

}

package com.enipro.presentation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.presentation.profile.ProfileActivity;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserSearchActivity extends AppCompatActivity implements SearchContract.View {

    @BindView(R.id.search_recycler_view)
    RecyclerView search_recycler_view;
    @BindView(R.id.search_progress_bar)
    CircularProgressView search_progress_bar;
    @BindView(R.id.text_view_error_msg)
    TextView textViewErrorMessage;
    @BindView(R.id.search)
    EditText search;
    @BindView(R.id.search_clear)
    ImageButton search_clear;

    private SearchContract.Presenter messagePresenter;
    private SearchAdapter usersAdapter;


    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, UserSearchActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.message_search_toolbar);
        setSupportActionBar(toolbar);

        // On arrow back press, finish the activity.
        findViewById(R.id.arrow_back).setOnClickListener(v -> finish());

        // Message Presenter
        messagePresenter = new SearchPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        messagePresenter.attachView(this);

        onSearchContentChanged(); // Set text changed listener to search edit text.
        // Empty the content of the search edit text when the clear button is clicked.
        search_clear.setOnClickListener(v -> search.setText(""));

        // Recycler view and adapter for news feed items
        search_recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        search_recycler_view.setLayoutManager(linearLayoutManager);
        usersAdapter = new SearchAdapter(null, this, this::onSearchItemClicked);
        search_recycler_view.setAdapter(usersAdapter);
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
                    // Clear data in adapter
                    usersAdapter.clear();
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


    @Override
    public void showSearchResults(List<User> userList) {
        search_recycler_view.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
        usersAdapter.setItems(userList);
    }

    @Override
    public void showError(String message) {
        textViewErrorMessage.setVisibility(View.VISIBLE);
        search_recycler_view.setVisibility(View.GONE);
        textViewErrorMessage.setText(message);
    }

    @Override
    public void showLoading() {
        search_progress_bar.setVisibility(View.VISIBLE);
        search_progress_bar.startAnimation();
        search_recycler_view.setVisibility(View.GONE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        search_progress_bar.setVisibility(View.GONE);
        search_progress_bar.stopAnimation();
        search_recycler_view.setVisibility(View.VISIBLE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    private void onSearchItemClicked(User user) {
        // Open the profile activity and remove current activity from back stack.
        Intent profileIntent = ProfileActivity.newIntent(this);
        profileIntent.putExtra(Constants.APPLICATION_USER, user);
        startActivity(profileIntent);
    }

}

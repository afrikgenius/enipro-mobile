package com.enipro.presentation.profile.student_search;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;
import com.enipro.presentation.profile.ProfileContract;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StudentSearch extends AppCompatActivity implements ProfileContract.StudentSearchView {

    @BindView(R.id.student_search_bar)
    EditText student_search_bar;
    @BindView(R.id.student_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.close)
    ImageButton close;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.progress_bar)
    CircularProgressView progress_bar;
    @BindView(R.id.clear)
    ImageButton search_clear;

    private StudentSearchAdapter adapter;
    private ProfileContract.StudentSearchPresenter presenter;


    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, StudentSearch.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_search);
        ButterKnife.bind(this);

        // Recycler view and adapter for student item
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        presenter = new StudentSearchPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        presenter.attachView(this);

        adapter = new StudentSearchAdapter(this, null);
        mRecyclerView.setAdapter(adapter);
        presenter.search("");
        init();

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);
    }

    @Override
    protected void onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom);
        super.onPause();
    }

    /**
     * Initializes action buttons with click listeners and all other initialization is done here.
     */
    private void init() {
        onSearchContentChanged();
        // Empty the content of the search edit text when the clear button is clicked.
        search_clear.setOnClickListener(v -> student_search_bar.setText(""));
        close.setOnClickListener(view -> finish());
        next.setOnClickListener(view -> {
            // TODO Check if at least a user has been selected to be mentored before advancing to defining a schedule for the mentoring sessions

        });
    }

    /**
     * Content change listener method that performs request to API when search content
     * is changed.
     */
    void onSearchContentChanged() {
        student_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 0) {
                    search_clear.setVisibility(View.VISIBLE);
                    // Send to presenter to search for user with new text
                    presenter.search(charSequence.toString());
                } else {
                    search_clear.setVisibility(View.GONE);
                    // Instead of clearing the data in the adapter, send a request to search for all users.
                    presenter.search("");
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
    public void showLoading() {
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.startAnimation();
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progress_bar.setVisibility(View.GONE);
        progress_bar.stopAnimation();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSearchResults(List<User> users) {
        mRecyclerView.setVisibility(View.VISIBLE);
        adapter.setItems(users);
    }

    @Override
    public void showError(String errorMessage) {

    }
}

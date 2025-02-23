package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;
import com.enipro.presentation.feeds.FeedContract;
import com.enipro.presentation.feeds.FeedPresenter;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ViewPostsActivity extends AppCompatActivity implements FeedContract.View {
    FeedContract.Presenter presenter;


    RecyclerView mRecyclerView;
    View no_saved_item;
    CircularProgressView posts_progress_bar;


    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ViewPostsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posts);

        mRecyclerView = findViewById(R.id.posts_recyclerview);
        no_saved_item = findViewById(R.id.no_saved_item);
        posts_progress_bar = findViewById(R.id.posts_progress_bar);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getString(R.string.saved));

        // Recycler view and adapter for news feed items
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        presenter = new FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);
        presenter.loadSavedFeeds();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void updateUI(Feed feedItem) {

    }

    @Override
    public void showPostNotification() {

    }

    @Override
    public void showCompleteNotification() {

    }

    @Override
    public void showErrorMessage() {

    }

    @Override
    public void showErrorNotification() {

    }
}

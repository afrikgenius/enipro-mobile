package com.enipro.presentation.feeds;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;
import com.enipro.presentation.generic.FeedRecyclerAdapter;
import com.enipro.presentation.post.PostActivity;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class FeedFragment extends Fragment implements FeedContract.View {

    RecyclerView mRecyclerView;
    FeedRecyclerAdapter feedRecyclerAdapter;
    FloatingActionButton fab;
    CircularProgressView progressView;


    NotificationCompat.Builder mBuilder; // Notification used to display progress.
    NotificationManager notificationManager;

    private FeedContract.Presenter presenter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private EniproDatabase db;

    // List of feeds present in the UI.
    private List<Feed> feeds = new ArrayList<>();

    private static final String FEED_ADAPTER_STATE = "com.enipro.presentation.feeds.FEED_ADAPTER_STATE";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if there is data in the bundle with an instance saved,
        if (savedInstanceState != null && savedInstanceState.containsKey(FEED_ADAPTER_STATE)) {
//            mAdapterSavedState = savedInstanceState.getParcelableArrayList(FEED_ADAPTER_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        progressView = view.findViewById(R.id.progress_view);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivityForResult(PostActivity.newIntent(getActivity()), FeedContract.Presenter.POST_FEED_REQUEST));

        // Swipe to refresh action to update feeds in app.
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_feeds);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
//            presenter.update_feeds(); // TODO Update UI with feeds.
            presenter.loadFeeds(result -> {
                // Check if result is null
                if (result == null) {
                    // Unhide no feed text views.
                    view.findViewById(R.id.no_feed_layout).setVisibility(View.VISIBLE);
                } else {
                    // Set feed data to result
                    feeds = new ArrayList<>(result);
                    // Clear feed recycler adapter
                    feedRecyclerAdapter.clear();
                    feedRecyclerAdapter.setItems(feeds);
                }
                mSwipeRefreshLayout.setRefreshing(false); // Remove the refresh button from screen and updates UI with update.
            });
        });

        // Recycler view and adapter for news feed items
        mRecyclerView = view.findViewById(R.id.feeds_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(getContext()));
        setRVScrollEvent();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        presenter = new FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), getContext());
        presenter.attachView(this);

        feedRecyclerAdapter = new FeedRecyclerAdapter(getContext(), null, presenter, false);
        mRecyclerView.setAdapter(feedRecyclerAdapter);

        // Load feeds from local storage and also from API
        presenter.loadFeeds(result -> {
            // Check if result is null
            if (result == null) {
                // Unhide no feed text views.
                view.findViewById(R.id.no_feed_layout).setVisibility(View.VISIBLE);
            } else {
                Log.d(Application.TAG, "Result is not null");
                // Set feed data to result
                feeds = new ArrayList<>(result);
                feedRecyclerAdapter.setItems(feeds);
            }
        });

        // TODO Some non-view operations should be in onCreate() in the fragment to allow for performance boost.
        return view;
    }

    public void setRVScrollEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == FeedContract.Presenter.POST_FEED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get feed information from intent
                Feed feed = data.getParcelableExtra(FeedContract.Presenter.ACTIVITY_RETURN_KEY);
                presenter.processFeed(feed); // Send feed to presenter to create feed.
            }
        }
    }

    @Override
    public void showPostNotification() {
        mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.sending_post))
                .setProgress(0, 0, true);

        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void showCompleteNotification() {
        mBuilder.setContentTitle(getResources().getString(R.string.sent))
                .setProgress(0, 0, false);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void showErrorNotification() {
        mBuilder.setContentTitle(getResources().getString(R.string.noti_error))
                .setProgress(0, 0, false);
        notificationManager.notify(0, mBuilder.build());
    }

    @Override
    public void showErrorMessage() {

    }

    @Override
    public void onSavedFeedsRetrieved(List<Feed> feeds) {

    }

    @Override
    public void updateUI(Feed feedItem, User user) {
        // Get rid of the no feed layout.
        View no_feed_layout = getView().findViewById(R.id.no_feed_layout);
        if (no_feed_layout.getVisibility() == View.VISIBLE)
            no_feed_layout.setVisibility(View.GONE);

        feedRecyclerAdapter.addItem(feedItem);
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void showLoading() {
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        progressView.setVisibility(View.GONE);
        progressView.stopAnimation();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false); // Remove the refresh button from screen and updates UI with update.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false); // Remove the refresh button from screen and updates UI with update.
        if (presenter != null)
            presenter.detachView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
package com.enipro.presentation.requests;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.SessionSchedule;
import com.enipro.data.remote.model.User;
import com.enipro.events.NotificationEvent;
import com.enipro.firebase.FirebaseNotificationBuilder;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.enipro.presentation.feeds.FeedContract;
import com.enipro.presentation.feeds.FeedPresenter;
import com.enipro.presentation.messages.MessageActivity;
import com.enipro.presentation.requests.session_schedule.SessionScheduleActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.enipro.model.Constants.SCHEDULE_DATA_REQUESTCODE;


public class RequestsFragment extends Fragment implements RequestsContract.View, RequestInteractor {


    private RequestsContract.Presenter presenter;
    private RequestsRecyclerAdapter adapter;

    RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CoordinatorLayout layout;
    private RequestPackage requestPackage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Application.TAG, "Request Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // Swipe to refresh action to update feeds in app.
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh_req);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            presenter.getRequests(Application.getActiveUser().get_id().get_$oid());
        });

        // Recycler view and adapter for news feed items
        mRecyclerView = view.findViewById(R.id.req_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(getContext()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        presenter = new RequestsPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), getContext());
        presenter.attachView(this);

        layout = view.findViewById(R.id.coordinatorLayout);
        adapter = new RequestsRecyclerAdapter(getActivity(), null, presenter, layout, this);
        mRecyclerView.setAdapter(adapter);

        // TODO Multiple request will be retrieved and sorted based on types and displayed in recycler views for each type of request.
        presenter.getRequests(Application.getActiveUser().get_id().get_$oid());
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Executes when result is returned.
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.SCHEDULE_DATA_REQUESTCODE:

                    // Get session schedule object from intent
                    SessionSchedule schedule = data.getParcelableExtra(Constants.SESSION_SCHEDULE_DATA);
                    Request request = requestPackage.request;
                    request.setSchedule(schedule);
                    // Perform acceptance of request
                    presenter.acceptRequest(request);
                    break;
                default:// nothing
            }
        }
    }

    @Override
    public void processAcceptance(Request request, User user, int position) {
        requestPackage = new RequestPackage(request, user, position);

        //  Open activity to show session schedule and session timings.
        Intent scheduleIntent = SessionScheduleActivity.newIntent(getActivity());
        startActivityForResult(scheduleIntent, Constants.SCHEDULE_DATA_REQUESTCODE);
    }

    @Override
    public void processDecline(Request request, int position) {
        presenter.declineRequest(request);
        adapter.removeItem(position);
        Utility.showSnackBar(layout, "Request Rejected", true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // Register to listen to incoming message events
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this); // Unregister listening to incoming message events.
    }

    /**
     * Acts as a subscriber that is called when a notification event from the publisher i.e
     * When a request notification is received.
     *
     * @param notificationEvent
     */
    @Subscribe
    public void onNotificationEvent(NotificationEvent notificationEvent) {
        Log.d(Application.TAG, "Notification Event Called.");
        // Should only be called in a case where there is no data in the adapter
//        if (messagesAdapter == null || messagesAdapter.getItemCount() == 0)
//            presenter.getMessageFromFirebaseUser(applicationUser.getFirebaseUID(), notificationEvent.getUid());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestsCollected(List<Request> requests) {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        adapter.setItems(requests);
    }

    @Override
    public void onRequestsError() {

    }

    @Override
    public void onRequestAccepted() {
        adapter.removeItem(requestPackage.position);
        Snackbar snackbar = Snackbar.make(layout, "Request Accepted", Snackbar.LENGTH_LONG)
                .setAction(Constants.MESSAGE, view -> {
                    // Create an intent to accepted user information
                    Intent intent = MessageActivity.newIntent(getActivity());
                    intent.putExtra(Constants.MESSAGE_CHAT_RETURN_KEY, requestPackage.user);
                    startActivity(intent);
                });
        TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();

        // Send a notification to sender that the request has been accepted.
        FirebaseNotificationBuilder.initialize()
                .title(Application.getActiveUser().getName())
                .message("accepted your mentoring request.")
                .username(Application.getActiveUser().get_id().get_$oid())
                .uid(Application.getActiveUser().getFirebaseUID())
                .firebaseToken(Application.getActiveUser().getFirebaseToken())
                .receiverFirebaseToken(requestPackage.user.getFirebaseToken())
                .uniqueIdentifier(Constants.MENTORING_REQUEST_REC)
                .send();
    }

    @Override
    public void onRequestDeclined() {

    }

    /**
     * A request bundle/ package that stores a request with its position in the list
     * of requests and represents a request that is either being accepted or being declined
     * at a point in time.
     */
    class RequestPackage {
        Request request; // The request that is being operated on.
        User user; // the user that sent the request
        int position; // the position of the request in the list of requests.

        RequestPackage(Request request, User user, int position) {
            this.request = request;
            this.user = user;
            this.position = position;
        }
    }
}

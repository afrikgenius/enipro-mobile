package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.enipro.presentation.generic.EduRecyclerAdapter;
import com.enipro.presentation.generic.ExpRecyclerAdapter;
import com.enipro.presentation.messages.MessageActivity;
import com.enipro.presentation.profile.student_search.StudentSearch;

import org.parceler.Parcels;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {


    @BindView(R.id.cover_photo)
    ImageView cover_photo;
    @BindView(R.id.profile_imageview)
    ImageView profile_imageview;
    @BindView(R.id.profile_name)
    TextView profile_name;
    @BindView(R.id.profile_headline)
    TextView profile_headline;
    @BindView(R.id.user_type)
    TextView user_type;
    @BindView(R.id.country)
    TextView country;
    @BindView(R.id.bio_text)
    TextView bio_text;
    @BindView(R.id.saved_layout)
    View saved_layout;
    @BindView(R.id.interests_layout)
    View interests_layout;
    @BindView(R.id.add_action)
    CircularProgressButton add_action;
    @BindView(R.id.message_user)
    ImageButton message_user;
    @BindView(R.id.edit_profile)
    ImageButton edit_profile;
    @BindView(R.id.settings)
    ImageButton settings;
    @BindView(R.id.education_recyclerview)
    RecyclerView edu_recyclerview;
    @BindView(R.id.experience_recyclerview)
    RecyclerView exp_recyclerview;

    // Layouts
    @BindView(R.id.bio_layout)
    View bio_layout;
    @BindView(R.id.education_card)
    View edu_layout;
    @BindView(R.id.exp_card)
    View exp_layout;
    @BindView(R.id.action_layout)
    View action_layout;
    @BindView(R.id.interest_post_card)
    View interest_post_card;


    ProfileContract.Presenter profilePresenter;
    private User user;
    private ProfileInteractor interactor;
    private MaterialDialog progressDialog;
    private EduRecyclerAdapter edu_adapter;
    private ExpRecyclerAdapter exp_adapter;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get user information from intent
        user = Parcels.unwrap(getIntent().getParcelableExtra(Constants.APPLICATION_USER));
        profilePresenter = new ProfilePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        profilePresenter.attachView(this);
        init();
        loadViewData();
        evaluateUser();
        setClickListeners();
    }

    private void init() {
        // Recycler view and adapter for education
        edu_recyclerview.setHasFixedSize(true);
        edu_recyclerview.addItemDecoration(new Utility.DividerItemDecoration(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        edu_recyclerview.setLayoutManager(linearLayoutManager);

        // Recycler view and adapter for experience
        exp_recyclerview.setHasFixedSize(true);
        exp_recyclerview.addItemDecoration(new Utility.DividerItemDecoration(this));
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        exp_recyclerview.setLayoutManager(linearLayoutManager1);
    }

    private void loadViewData() {
        profile_name.setText(user.getName());
        profile_headline.setText(user.getHeadline());
        country.setText(user.getCountry());
        user_type.setText(user.getUserType());
        Glide.with(this).load(user.getAvatar())
                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                .into(profile_imageview);
        Glide.with(this).load(user.getAvatar_cover())
                .apply(new RequestOptions().placeholder(R.drawable.bg_image))
                .into(cover_photo);
        edu_adapter = new EduRecyclerAdapter(user.getEducation());
        edu_recyclerview.setAdapter(edu_adapter);
        exp_adapter = new ExpRecyclerAdapter(user.getExperiences());
        exp_recyclerview.setAdapter(exp_adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Application.profileEdited) {
            user = Application.getActiveUser();
            loadViewData();
            Application.profileEdited = false;
        }
    }

    /**
     * Evaluate user to see if it is the current active user or not and takes some operations away.
     */
    private void evaluateUser() {

        if (user.get_id().get_$oid().equals(Application.getActiveUser().get_id().get_$oid())) {
            // Current user activity open
            switch (user.getUserType().toUpperCase()) {
                case Constants.STUDENT:
                    action_layout.setVisibility(View.GONE); // Take away the action layout
                    break;
                case Constants.PROFESSIONAL:
                    add_action.setText(Constants.AVAILABLE_FOR_MENTORING);
                    break;
                // TODO Support should be added for both company and schools.
            }
        } else {
            // Hide some of the view items
            edit_profile.setVisibility(View.GONE);
            settings.setVisibility(View.GONE);
            interest_post_card.setVisibility(View.GONE);
            // Another user information.
            switch (user.getUserType().toUpperCase()) {
                case Constants.STUDENT:
                    break;
                case Constants.PROFESSIONAL:
                    break;
                // TODO Support should be added for both company and schools.
            }
            interactor = new ProfileInteractor(user, profilePresenter, this::onInteractorProcessCompleted);
            interactor.process(); // Process
        }
    }

    /**
     * Runs as a call back function after the interactor process has been completed.
     */
    private void onInteractorProcessCompleted(String connectionString) {
        add_action.setText(connectionString);
        switch (connectionString) {
            case Constants.CONNECTION_IN_CIRCLE:
            case Constants.CONNECTION_IN_NETWORK:
            case Constants.CONNECTION_MENTORING:
                //  Change background of action to something else
                message_user.setVisibility(View.VISIBLE);
                add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
                add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case Constants.CONNECTION_REQUEST_MENTORING:
                break;
            case Constants.NO_CONNECTION:
                add_action.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * Click listeners for profile view items.
     */
    private void setClickListeners() {
        bio_text.setOnClickListener(view -> startActivity(BioActivity.newIntent(this)));
        // Set up action listener.
        add_action.setOnClickListener(view -> {
            // Based on the value of the connection string, the action listener does a couple of things
            switch (add_action.getText().toString()) {
                case Constants.CONNECTION_ADD_CIRCLE:
                    add_action.startAnimation();
                    // Call presenter to add user to the application users circle, and send a notification to the user to accept request.
//                    profilePresenter.addCircle(new UserConnection(user.get_id().get_$oid()));
                    profilePresenter.requestAddCircle(Application.getActiveUser().get_id().get_$oid(), user.get_id().get_$oid());
                    break;
                case Constants.CONNECTION_IN_CIRCLE:
                    // Open a dialog showing if the user wants to remove the current user from circle
                    new MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Remove " + user.getName() + " from circle")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(((dialog, which) -> {
                                add_action.startAnimation();
                                profilePresenter.removeCircle(user.get_id().get_$oid());
                            }))
                            .show();
                    break;
                case Constants.CONNECTION_ADD_NETWORK:
                    add_action.startAnimation();
//                    profilePresenter.addNetwork(new UserConnection(user.get_id().get_$oid()));
                    profilePresenter.requestAddNetwork(Application.getActiveUser().get_id().get_$oid(), user.get_id().get_$oid());
                    break;
                case Constants.CONNECTION_IN_NETWORK:
                    new MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Remove " + user.getName() + " from network")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(((dialog, which) -> {
                                add_action.startAnimation();
                                profilePresenter.removeNetwork(user.get_id().get_$oid());
                            }))
                            .show();
                    break;
                case Constants.CONNECTION_REQUEST_MENTORING:
                    add_action.startAnimation();
                    profilePresenter.requestMentoring(Application.getActiveUser().get_id().get_$oid(), user.get_id().get_$oid());
                    break;
                case Constants.CONNECTION_PENDING:
                    // On click, option to withdraw request that has been sent.
                    new MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Withdraw Mentoring Request")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive(((dialog, which) -> {
                                add_action.startAnimation();
                                profilePresenter.deleteRequest(); // This removes the request from
                            })).show();
                    break;
                case Constants.CONNECTION_MENTORING:
                    break;
                case Constants.AVAILABLE_FOR_MENTORING:
//                    add_action.startAnimation();
                    startActivity(StudentSearch.newIntent(this));
//                    profilePresenter.availableForMentoring(Application.getActiveUser().get_id().get_$oid());
                    break;
            }
        });

        edit_profile.setOnClickListener(view -> startActivity(ProfileEditActivity.newIntent(this)));
        // Click listener for interests and saved posts
        interests_layout.setOnClickListener(view -> startActivity(InterestActivity.newIntent(this)));
        saved_layout.setOnClickListener(view -> startActivity(ViewPostsActivity.newIntent(this)));
        message_user.setOnClickListener(view -> {

            // Pass the user info to the message Activity
            startActivity(MessageActivity.Companion.newIntent(this, user));
        });

        // Based on user type, remove view not needed
        switch (user.getUserType().toUpperCase()) {
            case Constants.STUDENT:
                bio_layout.setVisibility(View.GONE);
                exp_layout.setVisibility(View.GONE);
                break;
            case Constants.COMPANY:
                edu_layout.setVisibility(View.GONE);
                exp_layout.setVisibility(View.GONE);
                break;
            case Constants.SCHOOL:
                edu_layout.setVisibility(View.GONE);
                bio_layout.setVisibility(View.GONE);
                exp_layout.setVisibility(View.GONE);
                break;
        }
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

    @Override
    public void showProgress() {
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.wait)
                .progress(true, 0)
                .show();
    }

    /**
     * Callback function that runs in the view after the user has been added to the application users
     * circle.
     */
    @Override
    public void onCircleAdded() {
        // Change text of add action button and show a snack bar showing the user has been added.
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_IN_CIRCLE);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
            message_user.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onCircleRemoved() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_ADD_CIRCLE);
            add_action.setTextColor(Color.WHITE);
            add_action.setBackground(getResources().getDrawable(R.drawable.button_bg));
            message_user.setVisibility(View.GONE);
        });
    }

    @Override
    public void onNetworkAdded() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_IN_NETWORK);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
            message_user.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onNetworkRemoved() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_ADD_NETWORK);
            add_action.setTextColor(Color.WHITE);
            add_action.setBackground(getResources().getDrawable(R.drawable.button_bg));
            message_user.setVisibility(View.GONE);
        });
    }

    @Override
    public void onError(Throwable throwable) {
    }

    @Override
    public void onRequestSent() {

    }

    @Override
    public void onAvailableRequestSent() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.MENTORING_REQUEST_SENT);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
            message_user.setVisibility(View.GONE);
        });
    }

    @Override
    public void onMentoringRequestSent() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_PENDING);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
        });
        // Send a notification to profile user that a request for mentoring has been sent.
        User appUser = Application.getActiveUser();
        Utility.sendPushNotificationToReceiver(appUser.getName(), appUser.get_id().get_$oid(), "wants to be mentored by you.",
                appUser.getFirebaseUID(), appUser.getFirebaseToken(), user.getFirebaseToken(), Constants.MENTORING_REQUEST_ID);
    }

    /**
     * This is triggered when the request to add a user to a circle has been validated
     * by the Web API and it is up to this function to send a notification to the user to
     * either accept or reject the request.
     */
    @Override
    public void onAddCircleRequestValidated() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_PENDING);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
        });

        // Send notification to the profile user
        User appUser = Application.getActiveUser();
        Utility.sendPushNotificationToReceiver(appUser.getName(), appUser.get_id().get_$oid(), "wants to be in your circle",
                appUser.getFirebaseUID(), appUser.getFirebaseToken(), user.getFirebaseToken(), Constants.CIRCLE_REQUEST);
    }

    /**
     * This is triggered when the request to add a user to a network has been validated by the web
     * API and a notification is to be sent
     */
    @Override
    public void onAddNetworkRequestValidated() {
        add_action.revertAnimation(() -> {
            add_action.setText(Constants.CONNECTION_PENDING);
            add_action.setTextColor(getResources().getColor(R.color.colorPrimary));
            add_action.setBackground(getResources().getDrawable(R.drawable.oval_button));
        });

        // Send notification to the profile user
        User appUser = Application.getActiveUser();
        Utility.sendPushNotificationToReceiver(appUser.getName(), appUser.get_id().get_$oid(), "wants to be in your network",
                appUser.getFirebaseUID(), appUser.getFirebaseToken(), user.getFirebaseToken(), Constants.NETWORK_REQUEST);
    }

    @Override
    public void onRequestDeleted() {

    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        add_action.dispose();
        // Dismiss progress dialog if it is currently showing
        if (progressDialog != null && progressDialog.isShowing())
            dismissProgress();
    }
}

package com.enipro.presentation.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.presentation.feeds.FeedFragment;
import com.enipro.presentation.messages.MessagesFragment;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.requests.RequestsFragment;
import com.enipro.presentation.search.UserSearchActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    public static String EXTRA_DATA = "EXTRA_DATA";
    private static final String TAG = "HomeActivity";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private HomeContract.Presenter homePresenter; // Presenter to control data loading from models for this activity.

    // Instances to fragments in this activity
    private FeedFragment mFeedFragment;
    private RequestsFragment mRequestFragment;
    private MessagesFragment mMessagesFragment;
    private User activeUser;

    @BindView(R.id.home_profile_image)
    ImageView home_profile_image;
    @BindView(R.id.search_edit_text)
    EditText search_edit_text;

    private static final String FEED_FRAGMENT = "com.enipro.presentation.home.HomeActivity.FEED_FRAGMENT";
    private static final String REQUEST_FRAGMENT = "com.enipro.presentation.home.HomeActivity.REQUEST_FRAGMENT";
    private static final String MESSAGES_FRAGMENT = "com.enipro.presentation.home.HomeActivity.MESSAGES_FRAGMENT";

    /**
     * 1
     * Drawables used for tab icons in the activity.
     */
    static int[] INACTIVE_TAB_ICONS = {R.drawable.ic_home_dark, R.drawable.ic_tab_notification_inactive, R.drawable.ic_tab_message_inactive};
    static int[] ACTIVE_TAB_ICONS = {R.drawable.ic_home, R.drawable.ic_tab_notification_active, R.drawable.ic_tab_message_active};

    /**
     * Position of the first and active tab when the application is opened.
     */
    static int FIRST_TAB_POSITION = 0;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    @NonNull
    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(Constants.APPLICATION_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Tab Layout for view pager
        mTabLayout = findViewById(R.id.tabs);

        // Search for an enipro user.
        search_edit_text.setOnClickListener(view -> startActivity(UserSearchActivity.newIntent(this)));

        // Retrieve fragment instances from bundle
//        if (savedInstanceState == null) {
        mFeedFragment = new FeedFragment();
        mRequestFragment = new RequestsFragment();
        mMessagesFragment = new MessagesFragment();
//        } else {
//            mFeedFragment = (FeedFragment) getSupportFragmentManager().getFragment(savedInstanceState, FEED_FRAGMENT);
//            mRequestFragment = (RequestsFragment) getSupportFragmentManager().getFragment(savedInstanceState, REQUEST_FRAGMENT);
//            mMessagesFragment = (MessagesFragment) getSupportFragmentManager().getFragment(savedInstanceState, MESSAGES_FRAGMENT);
//        }

        // View pager to control tabs for news feed, requests and messages.
        mViewPager = findViewById(R.id.home_pager);

        HomePagerAdapter mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        // Add fragments to view pager.
        mPagerAdapter.addFragment(mFeedFragment, "FEED");
        mPagerAdapter.addFragment(mRequestFragment, "REQUESTS");
        mPagerAdapter.addFragment(mMessagesFragment, "MESSAGES");

        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        // Set tab icons
        for (int i = 1; i < mTabLayout.getTabCount(); i++) {
            View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
            view.findViewById(R.id.icon).setBackgroundResource(INACTIVE_TAB_ICONS[i]);
            mTabLayout.getTabAt(i).setCustomView(view);
        }

        // First tab is always active and must be set to ac
        View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view.findViewById(R.id.icon).setBackgroundResource(ACTIVE_TAB_ICONS[FIRST_TAB_POSITION]);
        mTabLayout.getTabAt(FIRST_TAB_POSITION).setCustomView(view);

        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                View view = tab.getCustomView();
                view.findViewById(R.id.icon).setBackgroundResource(ACTIVE_TAB_ICONS[tab.getPosition()]);
                tab.setCustomView(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                View view = tab.getCustomView();
                view.findViewById(R.id.icon).setBackgroundResource(INACTIVE_TAB_ICONS[tab.getPosition()]);
                tab.setCustomView(view);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });


        homePresenter = new HomePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        homePresenter.attachView(this);

        // Get active user in the application
        activeUser = getIntent().getParcelableExtra(Constants.APPLICATION_USER);
//        activeUser = getIntent().getParcelableExtra(Constants.APPLICATION_USER);
        Application.setActiveUser(activeUser);
        // Load profile avatar using picasso into
        RequestOptions options = new RequestOptions().placeholder(R.drawable.profile_image);
        Glide.with(this).load(activeUser.getAvatar()).apply(options).into(home_profile_image);

        home_profile_image.setOnClickListener(v -> startActivity(ProfileActivity.Companion.newIntent(this, Application.getActiveUser())));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save fragment state.
//        getSupportFragmentManager().putFragment(outState, FEED_FRAGMENT, mFeedFragment);
//        getSupportFragmentManager().putFragment(outState, REQUEST_FRAGMENT, mRequestFragment);
//        getSupportFragmentManager().putFragment(outState, MESSAGES_FRAGMENT, mMessagesFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public HomeContract.Presenter getPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        homePresenter.detachView();
    }

}

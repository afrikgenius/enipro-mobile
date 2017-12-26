package com.enipro.presentation.home;

import android.animation.LayoutTransition;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.presentation.chat.ChatActivity;
import com.enipro.presentation.feeds.FeedFragment;
import com.enipro.presentation.forums.ForumActivity;
import com.enipro.presentation.messages.MessagesFragment;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.requests.RequestsFragment;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeContract.View {

    public static String EXTRA_DATA = "EXTRA_DATA";
    private static final String TAG = "HomeActivity";

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private HomeContract.Presenter homePresenter; // Presenter to control data loading from models for this activity.

    Drawer navigationDrawer;

    // Instances to fragments in this activity
    private FeedFragment mFeedFragment;
    private RequestsFragment mRequestFragment;
    private MessagesFragment mMessagesFragment;
    private User activeUser;

    private ImageView profileView;

    private static final String FEED_FRAGMENT = "com.enipro.presentation.home.HomeActivity.FEED_FRAGMENT";
    private static final String REQUEST_FRAGMENT = "com.enipro.presentation.home.HomeActivity.REQUEST_FRAGMENT";
    private static final String MESSAGES_FRAGMENT = "com.enipro.presentation.home.HomeActivity.MESSAGES_FRAGMENT";

    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profileView = new ImageView(this);
        profileView.setImageDrawable(getResources().getDrawable(R.drawable.image001));
        // View pager to control tabs for news feed, requests and messages.
        mViewPager = findViewById(R.id.home_pager);
        HomePagerAdapter mPagerAdapter = new HomePagerAdapter(getSupportFragmentManager());

        // Retrieve fragment instances from bundle
        if(savedInstanceState == null){
            mFeedFragment = new FeedFragment();
            mRequestFragment = new RequestsFragment();
            mMessagesFragment = new MessagesFragment();
        } else {
            mFeedFragment = (FeedFragment) getSupportFragmentManager().getFragment(savedInstanceState, FEED_FRAGMENT);
            mRequestFragment = (RequestsFragment) getSupportFragmentManager().getFragment(savedInstanceState, REQUEST_FRAGMENT);
//            mMessagesFragment = (MessagesFragment) getSupportFragmentManager().getFragment(savedInstanceState, MESSAGES_FRAGMENT);
        }

        // Add fragments to view pager.
        mPagerAdapter.addFragment(mFeedFragment, "FEED");
        mPagerAdapter.addFragment(mRequestFragment, "REQUESTS");
        mPagerAdapter.addFragment(mMessagesFragment, "MESSAGES");

        mViewPager.setAdapter(mPagerAdapter);

        // Tab Layout for view pager
        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        homePresenter = new HomePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        homePresenter.attachView(this);

        // Get active user in the application
        activeUser = homePresenter.getActiveUser();
        // Load profile avatar using picasso into
        Picasso.with(this).load(activeUser.getAvatar()).into(profileView);
        Log.d(TAG, "DEFAULT URL " + activeUser.getAvatar());
        Log.d(TAG, "DEFAULT URL COVER " + activeUser.getAvatar_cover());
        // Build Navigation
        navigationDrawer = buildNavigationDrawer(toolbar).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save fragment state.
        getSupportFragmentManager().putFragment(outState, FEED_FRAGMENT, mFeedFragment);
        getSupportFragmentManager().putFragment(outState, REQUEST_FRAGMENT, mRequestFragment);
//        getSupportFragmentManager().putFragment(outState, MESSAGES_FRAGMENT, mMessagesFragment);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        LinearLayout searchBar = searchView.findViewById(R.id.search_bar);
        searchBar.setLayoutTransition(new LayoutTransition());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Intent profileIntent = ProfileActivity.newIntent(this);
            startActivity(profileIntent);
        } else if (id == R.id.nav_forums) {
//            Intent forumIntent = ForumActivity.newIntent(this);
//            startActivity(forumIntent);
        } else if (id == R.id.nav_chat) {
//            Intent chatIntent = ChatActivity.newIntent(this);
//            startActivity(chatIntent);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {
//            Intent helpActivity = HelpActivity.newIntent(this);
//            startActivity(helpActivity);/**/
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private DrawerBuilder buildNavigationDrawer(Toolbar toolbar){
        // Navigation Drawer Items
        PrimaryDrawerItem profileItem = new PrimaryDrawerItem().withIdentifier(DrawerItemConstants.NAV_PROFILE)
                .withSelectable(false)
                .withName(R.string.nav_profile).withIcon(R.drawable.ic_profile_normal);
        PrimaryDrawerItem forumItem = new PrimaryDrawerItem().withIdentifier(DrawerItemConstants.NAV_FORUMS)
                .withSelectable(false)
                .withName(R.string.nav_forums).withIcon(GoogleMaterial.Icon.gmd_forum);
        PrimaryDrawerItem chatItem = new PrimaryDrawerItem().withIdentifier(DrawerItemConstants.NAV_CHATS)
                .withSelectable(false)
                .withName(R.string.nav_chats).withIcon(GoogleMaterial.Icon.gmd_chat);

        // Secondary Navigation
        SecondaryDrawerItem settingsItem = new SecondaryDrawerItem().withIdentifier(DrawerItemConstants.NAV_SETTINGS)
                .withName(R.string.nav_settings)
                .withSelectable(false);
        SecondaryDrawerItem helpItem = new SecondaryDrawerItem().withIdentifier(DrawerItemConstants.NAV_HELP)
                .withName(R.string.nav_help)
                .withSelectable(false);

        String userName = activeUser.getFirstName() + " " + activeUser.getLastName();
        // Account Header
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.nav_header_672)
                .addProfiles(
                        new ProfileDrawerItem().withName(userName).withEmail(activeUser.getEmail())
                                .withIcon(activeUser.getAvatar())
                )
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener(){
                    @Override
                    public boolean onProfileImageClick(View view, IProfile iProfile, boolean b) {
                        startActivity(ProfileActivity.newIntent(getApplicationContext()));
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                })
                .build();

        // Configure Navigation Drawer
        return new DrawerBuilder().withActivity(this).withToolbar(toolbar).withActionBarDrawerToggle(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        profileItem,
                        forumItem,
                        chatItem,
                        new DividerDrawerItem(),
                        settingsItem,
                        helpItem)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {

                    // Launch item activity.
                    Intent activityIntent = navItemClick(drawerItem.getIdentifier());
                    startActivity(activityIntent);

                    // Close navigation drawer.
                    signalCloseDrawer();
                    return true;
                });
    }

    /**
     * Item click for items in navigation drawer.
     * @param identifier identifier used to create the nav item.
     * @return intent to use in activity launch
     */
    private Intent navItemClick(long identifier) {
        Intent activityIntent = null;
        if (identifier == DrawerItemConstants.NAV_PROFILE) {
            activityIntent = ProfileActivity.newIntent(this);
        } else if (identifier == DrawerItemConstants.NAV_FORUMS) {
            activityIntent = ForumActivity.newIntent(this);
        } else if (identifier == DrawerItemConstants.NAV_CHATS) {
            activityIntent = ChatActivity.newIntent(this);
        } else if (identifier == DrawerItemConstants.NAV_SETTINGS) {

        } else if (identifier == DrawerItemConstants.NAV_HELP) {
//            activityIntent = HelpActivity.newIntent(this);
        }
        return activityIntent;
    }

    /**
     * Closes navigation drawer.
     */
    private void signalCloseDrawer(){
        navigationDrawer.closeDrawer();
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

    /**
     * Constants used to create nav items.
     */
    private class DrawerItemConstants {
        static final long NAV_PROFILE = 0x01;
        static final long NAV_FORUMS = 0x02;
        static final long NAV_CHATS = 0x03;
        static final long NAV_SETTINGS = 0x04;
        static final long NAV_HELP = 0x05;
    }
}

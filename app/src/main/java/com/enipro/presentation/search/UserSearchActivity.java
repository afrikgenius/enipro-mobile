package com.enipro.presentation.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.enipro.R;
import com.enipro.model.Utility;

public class UserSearchActivity extends AppCompatActivity {

//    SearchView searchView;

    /**
     * Returns a new intent to open an instance of this activity.
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

        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final SearchView searchView = new SearchView(this);
        searchView.setQueryHint(getResources().getString(R.string.search_icon_title));
        // Add a listener to listen for text change and text submit.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Utility.showToast(getApplicationContext(), newText, false);
                return false;
            }
        });

        // Close activity when search view is closed.
        searchView.setOnCloseListener(() -> {
//            onDestroy(); // Finish the current activity.
            Utility.showToast(getApplicationContext(), "Closed", true);
            return true;
        });

        MenuItem searchMenu = menu.add("Search");
        searchMenu.setIcon(getResources().getDrawable(R.drawable.ic_search))
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        searchMenu.expandActionView();
        return super.onCreateOptionsMenu(menu);
    }
}

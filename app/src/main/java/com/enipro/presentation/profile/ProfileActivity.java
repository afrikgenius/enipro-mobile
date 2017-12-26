package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.injection.Injection;
import com.enipro.presentation.LauncherActivity;
import com.enipro.presentation.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {


//    @BindView(R.id.more_options_profile) ImageButton moreButton;

    ProfileContract.Presenter profilePresenter;

    private MaterialDialog progressDialog;

    /**
     * Returns a new intent to open an instance of this activity.
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

        profilePresenter = new ProfilePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        profilePresenter.attachView(this);

//        moreButton.setOnClickListener(v -> {
//            // Open more options (edit and log out.)
//            PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, v);
//            popupMenu.setOnMenuItemClickListener((menuItem) -> {
//                switch (menuItem.getItemId()){
//                    case R.id.edit_profile_item:
//                        // Open profile edit activity
//                        startActivity(ProfileEditActivity.newIntent(this));
//                        return true;
//                    case R.id.logout_item:
//                        showProgress();
//                        // Log the user out of the application
//                        profilePresenter.logout();
//                        // Open the login activity and remove all other activities from back stack.
//                        Intent intent = LauncherActivity.newIntent(this);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        return true;
//                    default: return false;
//                }
//            });
//            popupMenu.inflate(R.menu.profile_popup_menu);
//            popupMenu.show();
//        });

    }


    @Override
    public void showProgress() {
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Dismiss progress dialog if it is currently showing
        if(progressDialog != null && progressDialog.isShowing())
            dismissProgress();
    }
}

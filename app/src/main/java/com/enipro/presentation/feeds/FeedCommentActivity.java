package com.enipro.presentation.feeds;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.User;
import com.enipro.model.DateTimeStringProcessor;
import com.enipro.model.Utility;
import com.enipro.presentation.home.HomeActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class FeedCommentActivity extends AppCompatActivity {

    private Feed feedData;
    private User user;

    @BindView(R.id.comment_editText) EditText commentEditText;
    @BindView(R.id.user_post_image) CircleImageView user_image;
    @BindView(R.id.user_post_name) RobotoTextView user_name;
    @BindView(R.id.user_post_headline) RobotoTextView user_headline;
    @BindView(R.id.user_post_date) RobotoTextView post_date;
    @BindView(R.id.content) RobotoTextView feed_content;

    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, FeedCommentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comment);
        ButterKnife.bind(this); // Bind butterknife to this activity.

        Toolbar toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Collect feed data from feed fragment.
        feedData = getIntent().getExtras().getParcelable(FeedContract.Presenter.FEED);
        user = Application.getActiveUser();

        // Get intent that started the activity and get extra for keyboard event.
        if(getIntent().getIntExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, 0x01) == FeedContract.Presenter.ADD_COMMENT){
            // Request focus for edit text and Open the keyboard
            commentEditText.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        user_name.setText(user.getName());
        post_date.setText(DateTimeStringProcessor.process(feedData.getUpdated_at().getUtilDate()));
        feed_content.setText(feedData.getContent().getText());
        user_headline.setText(user.getUserType().toUpperCase());
        Picasso.with(this).load(user.getAvatar()).into(user_image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle arrow click to return to previous activity
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.enipro.presentation.post;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.injection.Injection;
import com.enipro.model.ApplicationService;
import com.enipro.model.ServiceType;
import com.enipro.model.ValidationService;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity implements PostContract.View {

    private static final String EXTRA_DATA = "EXTRA_DATA";

    private PostContract.Presenter presenter;

    @BindView(R.id.post_content_edit) EditText postContentEditText;

    @BindView(R.id.post_feed) Button post_button;


    public static Intent newIntent(Context context, Object repository) {
        Intent intent = new Intent(context, PostActivity.class);
//        intent.putExtra(EXTRA_REPOSITORY, repository);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ValidationService validationService = (ValidationService) ApplicationService.getInstance(ServiceType.ValidationService);
        presenter = new PostPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), validationService);
        presenter.attachView(this);

        // Close button to close activity.
        findViewById(R.id.post_close).setOnClickListener(view -> {
            if(isFeedContentEmpty())
                this.finish();
            else {

                // Wrap feed content into a feed object
                Feed feed = new Feed();

                // Spin up a dialog and ask the user to save or discard.
                new MaterialDialog.Builder(this)
                        .content(R.string.save_as_draft)
                        .positiveText(R.string.save)
                        .onPositive((dialog, which) -> {
                            // TODO Save draft in the application for later edit.
                            saveFeedAsDraft(feed);
//                            this.finish();
                        })
                        .negativeText(R.string.discard)
                        .onNegative((dialog, which) -> {
                            // The activity should be finished.
                            this.finish();
                        })
                        .show();
            }
        });

        // Post button unfades and is clickable
        onFeedContentChange();

        // Post button that collects feed content information.
        findViewById(R.id.post_feed).setOnClickListener(v -> {
            FeedContent content = new FeedContent();
            content.setText(postContentEditText.getText().toString()); // TODO For now only text can be posted.
            Feed feed = new Feed();
            feed.setContent(content);
            presenter.postFeedItem(feed);
        });

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    /**
     * When feed content changes.
     */
    void onFeedContentChange(){
        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(count > 0){
                    post_button.setTextColor(getResources().getColor(R.color.colorPrimary));
                    post_button.setClickable(true);
                } else {
                    post_button.setTextColor(getResources().getColor(R.color.material_grey_400_));
                    post_button.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /**
     * Checks if there is a content placed in the feed space by the user
     * @return true if there is no feed content and false if there is.
     */
    boolean isFeedContentEmpty(){
        return postContentEditText.getText().toString().length() == 0;
    }

    @Override
    public void saveFeedAsDraft(Feed feed) {

    }

    @Override
    public void showPostError(String errorMessage) {

    }

    @Override
    public void sendFeedItem(Feed feed) {
//         Send feed item back to home activity into feed fragment to be added.
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PostContract.Presenter.ACTIVITY_RETURN_KEY, feed);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom);
        super.onPause();
    }
}
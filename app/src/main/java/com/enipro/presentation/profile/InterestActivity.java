package com.enipro.presentation.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.devspark.robototextview.widget.RobotoButton;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.injection.Injection;
import com.enipro.model.Enipro;
import com.enipro.model.Utility;
import com.enipro.presentation.generic.TagRecyclerAdapter;
import com.enipro.presentation.signup.AddInterestsActivity;
import com.enipro.presentation.signup.SignupPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InterestActivity extends AppCompatActivity implements ProfileContract.View {

    @BindView(R.id.int_tags_recycler)
    RecyclerView mTagsRecycler;
    @BindView(R.id.save_interests)
    RobotoButton save_interests;
    @BindView(R.id.interests)
    EditText interests;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private TagRecyclerAdapter tagRecyclerAdapter;
    ProfileContract.Presenter presenter;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, InterestActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        ButterKnife.bind(this);

        presenter = new ProfilePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);

        // Recycler view and adapter for tags
        mTagsRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTagsRecycler.setLayoutManager(linearLayoutManager);

        tagRecyclerAdapter = new TagRecyclerAdapter(Application.getActiveUser().getInterests(), 0);
        mTagsRecycler.setAdapter(tagRecyclerAdapter);

        // Click listener for the continue button
        save_interests.setOnClickListener(view -> {
            // Verify data and advance process
            if (tagRecyclerAdapter.getItemCount() == 0) {
                // Show a snack bar showing error
                Utility.showSnackBar(coordinatorLayout, "You must add interests to continue", true);
            } else {
                Application.getActiveUser().setInterests(tagRecyclerAdapter.getItems());
                // Send update to server and persist information in local storage and finish activity
                new AppExecutors().diskIO().execute(() -> {
                    EniproDatabase.getInstance(this).userDao().updateUser(Application.getActiveUser());
                });
                // TODO Come back to this
//                presenter.updateUser(Application.getActiveUser());
                finish();
            }
        });

        interests.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                // Check for text wrapping and get the content of the text in previous line to change background color
                if (!charSequence.toString().equals("")) {
                    String lastChar = charSequence.toString().substring(charSequence.length() - 1);
                    if (lastChar.equals(" ")) {
                        // Remove trailing with white space character
                        // Get charSequence and add to adapter
                        tagRecyclerAdapter.addItem(charSequence.toString().substring(0, charSequence.length() - 1));
                        interests.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onRequestDeleted() {

    }

    @Override
    public void onNetworkAdded() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCircleRemoved() {

    }

    @Override
    public void onCircleAdded() {

    }

    @Override
    public void onRequestSent() {

    }

    @Override
    public void onMentoringRequestSent() {

    }

    @Override
    public void onAddCircleRequestValidated() {

    }

    @Override
    public void onAddNetworkRequestValidated() {

    }

    @Override
    public void onAvailableRequestSent() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void onNetworkRemoved() {

    }
}
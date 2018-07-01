package com.enipro.presentation.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddInterestsActivity extends AppCompatActivity implements SignupContract.View {

    private SignupContract.Presenter presenter;
    private AddInterestsActivity.TagRecyclerAdapter tagRecyclerAdapter;

    public static final String TAG = ".AddInterestsActivity";

    @BindView(R.id.interests)
    EditText interests;
    @BindView(R.id.int_tags_recycler)
    RecyclerView mTagsRecycler;
    @BindView(R.id.continue_int)
    Button continue_int;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    
    public static Intent newIntent(Context context) {
        return new Intent(context, AddInterestsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_interests);
        ButterKnife.bind(this);

        // Get user object passed from previous activity
        User user = Parcels.unwrap(getIntent().getParcelableExtra(TAG));

        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.getInstance(this), this);
        presenter.attachView(this);

        // Recycler view and adapter for tags
        mTagsRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mTagsRecycler.setLayoutManager(linearLayoutManager);

        tagRecyclerAdapter = new AddInterestsActivity.TagRecyclerAdapter(null);
        mTagsRecycler.setAdapter(tagRecyclerAdapter);

        // Click listener for the continue button
        continue_int.setOnClickListener(view -> {
            // Verify data and advance process
            if (tagRecyclerAdapter.getItemCount() == 0) {
                // Show a snackbar showing error
                Utility.showSnackBar(coordinatorLayout, "You must add interests before continuing", true);
            } else {
                user.setInterests(tagRecyclerAdapter.getItems());
                user.setCountry("Nigeria");
                user.setMobile("+2348111014020");
                advanceProcess(user);
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
    public SignupContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void openApplication(User user) {

    }

    @Override
    public void showMessage(int message) {
    }

    @Override
    public void showMessage(String type, String message) {
    }

    @Override
    public void showMessageDialog(int title, int message) {
    }

    @Override
    public void showMessageDialog(int title, String message) {
    }

    @Override
    public void showMessageDialog(String title, int message) {
    }

    @Override
    public void showMessageDialog(String title, String message) {
    }

    @Override
    public void setViewError(View view, String errorMessage) {
    }

    @Override
    public String getSpinnerData(String spinner_name) {
        return null;
    }

    @Override
    public void advanceProcess(User user) {
        // Open add photo activity passing the user object as a bundle
        Intent addPhotoIntent = AddPhotoActivity.newIntent(this);
        addPhotoIntent.putExtra(AddPhotoActivity.TAG, Parcels.wrap(user));
        startActivity(addPhotoIntent);
    }

    /**
     * Recycler Adapter for tags view.
     */
    private class TagRecyclerAdapter extends RecyclerView.Adapter<AddInterestsActivity.TagRecyclerAdapter.ViewHolder> {

        List<String> tags;

        TagRecyclerAdapter(List<String> tags) {
            this.tags = tags;
        }

        @Override
        public int getItemCount() {
            if (tags == null) {
                return 0;
            }
            return tags.size();
        }

        @Override
        public AddInterestsActivity.TagRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interests_item, parent, false);
            return new AddInterestsActivity.TagRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AddInterestsActivity.TagRecyclerAdapter.ViewHolder holder, int position) {
            holder.tag_text.setText(tags.get(position));
            holder.tag_cancel.setVisibility(View.VISIBLE);
            holder.tag_cancel.setOnClickListener(v -> {
                tags.remove(position);
                notifyDataSetChanged();
            });
        }

        /**
         * Adds an item to the recycler adapter to be displayed in the recycler view.
         *
         * @param tag the feed comment item to be added.
         */
        public void addItem(String tag) {
            if (tags == null)
                tags = new ArrayList<>();
            this.tags.add(tag);
            notifyDataSetChanged();
        }

        public List<String> getItems() {
            List<String> retData = new ArrayList<>();
            retData.addAll(tags);
            return retData; // TODO Return a copy and not the object itself.
        }


        /**
         * Removes a feed comment item from the recycler view.
         *
         * @param tag      the item to remove
         * @param position the position of the item to remove.
         */
        public void removeItem(String tag, int position) {
            this.tags.remove(position);
            notifyDataSetChanged();
        }

        /**
         * Clear the list in the adapter
         */
        public void clear() {
            this.tags = null;
            notifyDataSetChanged();
        }

        /**
         * ViewHolder for tags text view.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tag_text;
            ImageButton tag_cancel;

            ViewHolder(View view) {
                super(view);
                tag_text = view.findViewById(R.id.tag_text);
                tag_cancel = view.findViewById(R.id.tag_cancel);
            }
        }
    }
}

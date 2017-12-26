package com.enipro.presentation.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.User;
import com.enipro.model.DateTimeStringProcessor;
import com.enipro.model.Enipro;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.profile.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.Util;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    // Private Instance Variables
    private List<Feed> feeds;

    // Presenter of class interacting with adapter
    FeedContract.Presenter presenter;
    private Context context;

    public FeedRecyclerAdapter(Context context, List<Feed> feedData, FeedContract.Presenter presenter){
        this.feeds = feedData;
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed_item, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Feed feedItem = feeds.get(position);
        // Make a request to get the user information making the post // TODO The user information is supposed to be gotten with a request to the feed.

        LocalCallback<User> callback = (user) -> {

            // Get data returned from callback which is a user object.
            String user_name = user.getFirstName() + " " + user.getLastName();
            holder.textName.setText(user_name);
            holder.textName.setOnClickListener(v -> onNameClickListener(user));

            ((Activity)context).runOnUiThread(() -> Picasso.with(context).load(user.getAvatar()).into(holder.userPostImage));
        };

        presenter.getUser(feedItem.getUser(), callback);
        org.joda.time.LocalDateTime date = feedItem.getUpdated_at().getUtilDate();

        holder.textContent.setText(feedItem.getContent().getText());
        holder.textPostDateTime.setText(DateTimeStringProcessor.process(date));
        holder.post_headline.setText(Application.getActiveUser().getUserType().toUpperCase());
        holder.childClickable.setOnClickListener(v -> onViewClickListener(feedItem));
        holder.commentButton.setOnClickListener(v -> onCommentClickListener(feedItem));
        holder.textContent.setOnClickListener(v -> onViewClickListener(feedItem));
    }

    /**
     * Activates the view comment view to add a new comment to a feed.
     */
    void onCommentClickListener(Feed feed){
        // Open the feed comment activity and activate the keyboard on the add comment edit text
        Intent intent = FeedCommentActivity.newIntent(context);
        intent.putExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, FeedContract.Presenter.ADD_COMMENT);
        intent.putExtra(FeedContract.Presenter.FEED, feed);
        context.startActivity(intent);
    }

    /**
     * Specifies what happens when the view representing the feed item is clicked which is opening
     * the feed comment activity to view the comments and probably add a comment.
     */
    void onViewClickListener(Feed feed){
        Intent intent = FeedCommentActivity.newIntent(context);
        intent.putExtra(FeedContract.Presenter.FEED, feed);
        context.startActivity(intent);
    }

    /**
     *
     * @param user
     */
    void onNameClickListener(User user){
        // Open an instance of Profile Activity with user passed as a parceable
        Intent intent = ProfileActivity.newIntent(context);
        context.startActivity(intent);
    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     * @param feedItem the feed item to be added.
     */
    public void addItem(Feed feedItem){
        this.feeds.add(0, feedItem);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    /**
     * Removes a feed item from the recycler view.
     * @param feedItem the item to remove
     * @param position the position of the item to remove.
     */
    public void removeItem(Feed feedItem, int position){
        this.feeds.remove(position);
    }

    /**
     * Called to save the state of the adapter in the feed fragment.
     * @return the list of feed items representing the adapter data.
     */
    ArrayList<Feed> onSaveInstanceState(){
        int size = getItemCount();
        ArrayList<Feed> items = new ArrayList<>(size);
        for(int i=0;i<size;i++){
            items.add(feeds.get(i));
        }
        return items;
    }

    /**
     * Called to restore the state of the adapter.
     * @param feed_items adapter data to use in restoration of adapter.
     */
    void onRestoreInstanceState(List<Feed> feed_items){
        feeds = feed_items;
        // Notify a change in data.

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // View items used in view holder
        RobotoTextView textName;
        RobotoTextView textPostDateTime;
        TextView textContent;
        RobotoTextView post_headline;
        ImageButton likeButton;
        ImageButton commentButton;
        ImageButton shareButton;
        ImageButton saveButton;
        View childClickable;
        CircleImageView userPostImage;

        private Context context;

        private boolean likeState = false; // The state of the like button (liked == true and !liked == false)
        private boolean savedState = false; // The state of the save button (saved == true and !saved = false)

        public ViewHolder(View view, Context context){

            super(view);
            this.context = context;

            textName = view.findViewById(R.id.user_post_name);
            userPostImage = view.findViewById(R.id.user_post_image);

            textPostDateTime = view.findViewById(R.id.user_post_date);
            textContent = view.findViewById(R.id.content);

            // Like Button
            likeButton = view.findViewById(R.id.post_like_button);
            likeButton.setOnClickListener(v -> onLikeClickListener());

            // Comment Button
            commentButton = view.findViewById(R.id.post_comment_button);
            // Share Button
            shareButton = view.findViewById(R.id.post_share_button);
            shareButton.setOnClickListener(v -> onShareClickListener());

            // Save Button
            saveButton = view.findViewById(R.id.post_save_button);
            saveButton.setOnClickListener(v -> onSaveClickListener());

            childClickable = view.findViewById(R.id.child_relative_layout);
            post_headline = view.findViewById(R.id.user_post_headline);
        }

        /**
         * Uses the state of the button to detect what to do. Changes the button icon to activated if true
         * and deactivated if false and also uses the feed presenter to send API calls to persist the state of
         * a feed.
         */
        void onLikeClickListener(){
            // Change drawable to a full favorite button and apply a one bounce animation on it.
            if(!likeState) {
                likeButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_full, null));
                applyLikeAnimation(likeButton);
            } else {
                likeButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_border, null));
                applyLikeAnimation(likeButton);
            }
            likeState = !likeState; // Invert like state.
            // TODO Call presenter to persist likes.
        }

        // TODO Implement properly.
        /**
         * Sends an action within an intent to deliver the feed item content to another application.
         */
        void onShareClickListener(){
            // Create an intent with a SEND Action
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain"); // Change Mime type to handle enipro posts.

            // Grab feed item information and put in intent.
            sharingIntent.putExtra(Intent.EXTRA_TEXT, textContent.getText().toString());
            context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_text)));
        }

        /**
         * Saves the post for the user to view later in saved posts which sends a save action to the API to persist the post
         * as a saved post by the user.
         */
        void onSaveClickListener(){
            // Change drawable to a full favorite button and apply a one bounce animation on it.
            if(!savedState) {
                saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark, null));
                applyLikeAnimation(saveButton);

                // Snackbar Saved
                Utility.showToast(context, R.string.saved, false);
            } else {
                saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_border, null));
                applyLikeAnimation(saveButton);
            }
            savedState = !savedState; // Invert saved state.
        }

        /**
         * Applies an animation to an image button in card_feed_item.
         * @param imageButton
         */
        void applyLikeAnimation(ImageButton imageButton){
            Animation bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
            imageButton.startAnimation(bounceAnimation);
        }
    }
}

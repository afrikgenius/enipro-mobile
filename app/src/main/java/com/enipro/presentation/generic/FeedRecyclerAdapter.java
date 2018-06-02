package com.enipro.presentation.generic;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.User;
import com.enipro.model.Constants;
import com.enipro.model.DateTimeStringProcessor;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.feeds.FeedCommentActivity;
import com.enipro.presentation.feeds.FeedContract;
import com.enipro.presentation.feeds.VideoActivity;
import com.enipro.presentation.payments.PaymentsFormActivity;
import com.enipro.presentation.post.PostActivity;
import com.enipro.presentation.profile.ProfileActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.ui.PlayerView;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalVideoView;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static org.apache.commons.io.FileUtils.getFile;


public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.ViewHolder> {

    // Private Instance Variables
    private List<Feed> feeds;
    private boolean allSaved = false;

    // Presenter of class interacting with adapter
    FeedContract.Presenter presenter;
    private Context context;


    public FeedRecyclerAdapter(Context context, List<Feed> feedData, FeedContract.Presenter presenter, boolean allSaved) {
        this.feeds = feedData;
        this.presenter = presenter;
        this.context = context;
        this.allSaved = allSaved;
    }

    @Override
    public int getItemCount() {
        if (feeds == null) {
            return 0;
        }
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
            ((Activity) context).runOnUiThread(() -> {
                // Get data returned from callback which is a user object.
                String user_name = user.getFirstName() + " " + user.getLastName();
                holder.textName.setText(user_name);
                holder.post_headline.setText(user.getHeadline());
                holder.post_header.setOnClickListener(v -> onHeaderClickListener(user));
                Picasso.with(context).load(user.getAvatar())
                        .placeholder(R.drawable.profile_image)
                        .into(holder.userPostImage);
            });
        };

        // Check if there is a clause to set all items as saved
        if (allSaved) {
            holder.savedState = true;
            holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_active, null));
        }

        // Check if application user has saved this feed item
        List<SavedFeed> savedFeeds = Application.getActiveUser().getSavedFeeds();
        for (SavedFeed savedFeed : savedFeeds) {
            if (savedFeed.getFeedId().equals(feedItem.get_id().get_$oid())) {
                // change saved drawable
                holder.savedState = true;
                holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_active, null));
            }
        }

        presenter.getUser(feedItem.getUser(), callback);
        org.joda.time.LocalDateTime date = feedItem.getUpdated_at().getUtilDate();

        holder.textContent.setText(feedItem.getContent().getText());
        holder.textPostDateTime.setText(DateTimeStringProcessor.process(date));
        holder.post_content.setOnClickListener(v -> onViewClickListener(feedItem));
        holder.commentButton.setOnClickListener(v -> onCommentClickListener(feedItem));
        holder.textContent.setOnClickListener(v -> onViewClickListener(feedItem));
        holder.moreOptions.setOnClickListener(v -> moreOptionsClickListener(feedItem, position));
        holder.saveButton.setOnClickListener(v -> onSaveClickListener(holder, feedItem));
        holder.shareButton.setOnClickListener(v -> onShareClickListener(feedItem));


        // Else show nothing
        if (feedItem.getComments().size() > 0)
            holder.post_comment_responses.setText(String.valueOf(feedItem.getComments().size()));

        // Recycler view and adapter for news feed items
        TagRecyclerAdapter tagRecyclerAdapter = new TagRecyclerAdapter(feedItem.getTags(), R.layout.tag_item);
        tagRecyclerAdapter.setCancellable(false);
        holder.tags_recyclerview.setAdapter(tagRecyclerAdapter);

        // If an image exists, the image is shown else if there is a video, the video is displayed.
        if (feedItem.getContent().getImage() != null) {
            // Check for number of lines contained in text and set max to 4
            holder.textContent.setMaxLines(Constants.FEED_CONTENT_MAX_LINES_IMAGE);
            // Make the view visible and set the media content.
            holder.post_image.setVisibility(View.VISIBLE);

            RequestOptions options = new RequestOptions().placeholder(R.drawable.bg_image);
            Glide.with(context).load(feedItem.getContent().getImage()).apply(options).into(holder.post_image);
        } else if (feedItem.getContent().getVideo() != null) {
            holder.textContent.setMaxLines(Constants.FEED_CONTENT_MAX_LINES_IMAGE);
            // Remove view from card
//            holder.videoLayout.setVisibility(View.VISIBLE);
//            holder.post_video.setVideoURI(Uri.parse(feedItem.getContent().getVideo()));
//            holder.post_video.setOnPreparedListener(mediaPlayer -> {
//                this.mediaPlayer = mediaPlayer;
//                holder.playButton.setVisibility(View.VISIBLE);
//                holder.post_video.seekTo(Constants.VIDEO_SEEK_TO); // Advance the video to a point to show a thumbnail.
//                holder.video_length.setVisibility(View.VISIBLE);
//
//                int duration = holder.post_video.getDuration();
//                int hours = duration / 3600;
//                int minutes = (duration / 60) - (hours * 60);
//                int seconds = duration - (hours * 3600) - (minutes * 60);
//                String formatted = String.format("%02d:%02d", minutes, seconds);
//                holder.video_length.setText(formatted);
//            });
//
//
//            // TODO This is a temporary solution here. This full adapter class should be refractored and well structured.
//
//            // Create an exoplayer
//            ExoPlayer player = ExoPlayerFactory.newSimpleInstance()
        }

        // TODO Move into video check above.
        holder.playButton.setOnClickListener(view -> {
            // Open video view activity and start playing the file
            Intent videoIntent = VideoActivity.newIntent(context);
            videoIntent.putExtra(Constants.VIDEO_PATH, feedItem.getContent().getVideo());
            Log.d(Application.TAG, feedItem.getContent().getVideo());
            context.startActivity(videoIntent);
        });

        // Check if the post contains a premium item, then make premium lock visible.
        if (feedItem.getPremiumDetails() != null) {
            holder.premiumLock.setVisibility(View.VISIBLE);
        }

        // Check if a document exists and when one is found make visible and make clickable
        if (feedItem.getContent().getDoc() != null) {
            holder.postDoc.setVisibility(View.VISIBLE);
            holder.postDoc.setOnClickListener(view -> onDocClickListener(feedItem));
        }
    }

    /**
     * Activates the view comment view to add a new comment to a feed.
     */
    void onCommentClickListener(Feed feed) {
        // Open the feed comment activity and activate the keyboard on the add comment edit text
        Intent intent = FeedCommentActivity.newIntent(context);
        intent.putExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, FeedContract.Presenter.ADD_COMMENT);
        intent.putExtra(FeedContract.Presenter.FEED, Parcels.wrap(feed));
        context.startActivity(intent);
    }

    /**
     * Sends an action within an intent to deliver the feed item content to another application.
     */
    void onShareClickListener(Feed feed) {
        // Create an intent with a SEND Action
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain"); // Change Mime type to handle enipro posts.

        // Grab feed item information and put in intent.
        sharingIntent.putExtra(Intent.EXTRA_TEXT, feed.getContent().getText());
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_text)));
    }


    /**
     * Listener operation that get executed when the doc button is clicked.
     * This opens the document attached to the feed item. if the feed has a premium content, it
     * checks if the user clicking the content has paid for it else it opens the payment window to
     * process payment for the item.
     *
     * @param feedItem the feed item that the doc was clicked.
     */
    void onDocClickListener(Feed feedItem) {
        // Get document object
        Document doc = feedItem.getContent().getDoc();
        //  Download the item and open using default app for that item.
        // TODO Try on all devices to see if this feature works.
        // In a case the feed item has been tagged premium, show item pricing and option to pay
        if (feedItem.getPremiumDetails() == null) {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(doc.getExtension());
            newIntent.setDataAndType(Uri.parse(doc.getUrl()), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        } else {
            // TODO Show option to pay for content. A dialog showing information like document name and price of content.
            // TODO Check if the user has paid for this item. Get current user then
            // TODO

            Intent paymentIntent = PaymentsFormActivity.newIntent(context);
            paymentIntent.putExtra(Constants.FEED_EXTRA, Parcels.wrap(feedItem));
            context.startActivity(paymentIntent);

        }
    }

    /**
     * Listener that listens when the more options item is clicked on the UI
     */
    void moreOptionsClickListener(Feed feed, int feed_position) {
        if (feed.getUser().equals(Application.getActiveUser().get_id().get_$oid())) {
            new MaterialDialog.Builder(context)
                    .items(R.array.app_user_more_items)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        switch (position) {
                            case 0: // Open post activity with predefined views
                                Intent intent = PostActivity.newIntent(context);
                                intent.putExtra(Constants.FEED_EXTRA, Parcels.wrap(feed));
                                context.startActivity(intent);
                                break;
                            case 1: // Show tags in a material dialog.
                                break;
                            case 2: // Delete post
                                presenter.removeFeed(feed);
                                removeItem(feed_position);
                                // call presenter to delete post from API
                                presenter.deleteFeed(feed);
                                break;
                        }
                    }).show();
        } else {
            // Not application users post. the user can hide
            new MaterialDialog.Builder(context)
                    .items(R.array.foreign_user_more_items)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        // Action Listeners for hiding post and
                        switch (position) {
                            case 0: // Hide Post
                                removeItem(feed_position);
                                break;
                            case 1: // Show tags in a material dialog
                                break;
                        }
                    }).show();
        }
    }

    /**
     * Specifies what happens when the view representing the feed item is clicked which is opening
     * the feed comment activity to view the comments and probably add a comment.
     */
    void onViewClickListener(Feed feed) {
        Intent intent = FeedCommentActivity.newIntent(context);
        intent.putExtra(FeedContract.Presenter.FEED, Parcels.wrap(feed));
        context.startActivity(intent);
    }

    /**
     * @param user
     */
    void onHeaderClickListener(User user) {
        // Open an instance of Profile Activity with user passed as a parceable
        Intent intent = ProfileActivity.newIntent(context);
        intent.putExtra(Constants.APPLICATION_USER, Parcels.wrap(user));
        context.startActivity(intent);
    }

    /**
     * Saves the post for the user to view later in saved posts which sends a save action to the API to persist the post
     * as a saved post by the user.
     */
    void onSaveClickListener(ViewHolder holder, Feed feed) {
        // Change drawable to a full favorite button and apply a one bounce animation on it.
        if (!holder.savedState) {
            holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_active, null));
            Utility.applyBounceAnimation(holder.saveButton, context);
            // Snack bar Saved
            Utility.showToast(context, R.string.saved, false);
            presenter.addSaved(feed);
        } else {
            holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_nactive, null));
            Utility.applyBounceAnimation(holder.saveButton, context);
            presenter.removeSaved(feed);
        }
        holder.savedState = !holder.savedState; // Invert saved state.
    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     *
     * @param feedItem the feed item to be added.
     */
    public void addItem(Feed feedItem) {
        this.feeds.add(0, feedItem);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    /**
     * Removes a feed item from the recycler view.
     *
     * @param position the position of the item to remove.
     */
    public void removeItem(int position) {
        this.feeds.remove(position);
        notifyItemRemoved(position);
    }


    public void setItems(List<Feed> feeds) {
        this.feeds = feeds;
        notifyDataSetChanged();
    }

    public void clear() {
        this.feeds = null;
        notifyDataSetChanged();
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
        ImageButton moreOptions;
        ImageButton postDoc;
        ImageButton premiumLock;
        View post_header;
        View post_content;
        RobotoTextView post_comment_responses;
        CircleImageView userPostImage;
        RecyclerView tags_recyclerview;
        ImageView post_image;
        PlayerView post_video;
        FrameLayout videoLayout;
        ImageButton playButton;
        RobotoTextView video_length;


        private Context context;
        private boolean likeState = false; // The state of the like button (liked == true and !liked == false)
        boolean savedState = false; // The state of the save button (saved == true and !saved = false)


        public ViewHolder(View view, Context context) {

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
            post_comment_responses = view.findViewById(R.id.post_comment_count);

            // Share Button
            shareButton = view.findViewById(R.id.post_share_button);

            // Save Button
            saveButton = view.findViewById(R.id.post_save_button);
            moreOptions = view.findViewById(R.id.more_options);

            postDoc = view.findViewById(R.id.post_doc);
            premiumLock = view.findViewById(R.id.premium_lock);

            post_headline = view.findViewById(R.id.user_post_headline);
            post_header = view.findViewById(R.id.post_header);
            post_content = view.findViewById(R.id.post_content);
            tags_recyclerview = view.findViewById(R.id.tags_post_recyclerview);
            tags_recyclerview.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            tags_recyclerview.setLayoutManager(linearLayoutManager);

            post_image = view.findViewById(R.id.post_image);
            post_video = view.findViewById(R.id.post_video);
            videoLayout = view.findViewById(R.id.video_layout);
            playButton = view.findViewById(R.id.play_pause_button);
            video_length = view.findViewById(R.id.video_length);
        }

        /**
         * Uses the state of the button to detect what to do. Changes the button icon to activated if true
         * and deactivated if false and also uses the feed presenter to send API calls to persist the state of
         * a feed.
         */
        void onLikeClickListener() {
            // Change drawable to a full favorite button and apply a one bounce animation on it.
            if (!likeState) {
                likeButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_full, null));
                Utility.applyBounceAnimation(likeButton, context);
            } else {
                likeButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_border, null));
                Utility.applyBounceAnimation(likeButton, context);
            }
            likeState = !likeState; // Invert like state.
            // TODO Call presenter to persist likes.
        }
    }
}

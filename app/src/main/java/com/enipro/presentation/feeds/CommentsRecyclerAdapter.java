package com.enipro.presentation.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.User;
import com.enipro.model.DateTimeStringProcessor;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {


    private List<FeedComment> comments;
    private Context context;
    private FeedContract.CommentPresenter presenter;

    private boolean likeState = false; // The state of the like button (liked == true and !liked == false)


    CommentsRecyclerAdapter(List<FeedComment> comments, Context context, FeedContract.CommentPresenter presenter) {
        this.comments = comments;
        this.context = context;
        this.presenter = presenter;
    }

    @Override
    public int getItemCount() {
        if (comments == null) {
            return 0;
        }
        return comments.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentsRecyclerAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FeedComment comment = comments.get(position);

        // Callback function passed to getUser in presenter.
        LocalCallback<User> callback = (user) -> {
            ((Activity) context).runOnUiThread(() -> {
                holder.comment_user_post_name.setText(user.getName());
                holder.comment_user_post_headline.setText(user.getHeadline());
                Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.profile_image).into(holder.comment_user_post_image);
            });
        };

        presenter.getUser(comment.getUser(), callback);
        org.joda.time.LocalDateTime date = comment.getUpdated_at().getUtilDate();

        holder.comment_content.setText(comment.getComment());
        holder.comment_user_post_date.setText(DateTimeStringProcessor.process(date));
        holder.comment_post_share_button.setOnClickListener(v -> onShareClickListener(holder.comment_content.getText().toString()));
        holder.comment_post_like_button.setOnClickListener(v -> onLikeClickListener(holder.comment_post_like_button));
        holder.comment_image.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(comment.getComment_image())
                .fit()
                .centerCrop()
                .transform(new RoundedCornersTransformation(10, 10))
                .placeholder(R.drawable.bg_image)
                .into(holder.comment_image);

        // Remove image view if content_image is null
        if (comment.getComment_image() == null)
            ((ViewGroup) holder.comment_image.getParent()).removeView(holder.comment_image);

        // TODO Complete this section
        holder.comment_image.setOnClickListener(v -> {
            List<String> imageList = new ArrayList<>();
            imageList.add(comment.getComment_image());
            new ImageViewer.Builder<>(context, imageList).setStartPosition(0x02).show();
        });
    }

    /**
     * Uses the state of the button to detect what to do. Changes the button icon to activated if true
     * and deactivated if false and also uses the feed presenter to send API calls to persist the state of
     * a feed.
     */
    void onLikeClickListener(ImageButton likeButton) {
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

    // TODO Implement properly.

    /**
     * Sends an action within an intent to deliver the feed item content to another application.
     */
    void onShareClickListener(String information) {
        // Create an intent with a SEND Action
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain"); // Change Mime type to handle enipro posts.

        // Grab feed item information and put in intent.
        sharingIntent.putExtra(Intent.EXTRA_TEXT, information);
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_text)));
    }

    /**
     * @param comments
     */
    public void setCommentItems(List<FeedComment> comments) {
        Collections.reverse(comments); // Reverse the list
        this.comments = comments;
        notifyDataSetChanged();
    }


    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     *
     * @param commentItem the feed comment item to be added.
     */
    public void addItem(FeedComment commentItem) {
        if (comments == null)
            comments = new ArrayList<>();
        this.comments.add(0, commentItem);
        notifyItemInserted(0); // Item added to the top of the view.
    }


    /**
     * Removes a feed comment item from the recycler view.
     *
     * @param commentItem the item to remove
     * @param position    the position of the item to remove.
     */
    public void removeItem(FeedComment commentItem, int position) {
        this.comments.remove(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView comment_user_post_image;
        final RobotoTextView comment_user_post_name;
        final RobotoTextView comment_user_post_headline;
        final RobotoTextView comment_user_post_date;
        final RobotoTextView comment_content;
        final ImageButton comment_post_like_button;
        final RobotoTextView comment_post_likes_count;
        final ImageButton comment_post_share_button;
        final ImageView comment_image;

        ViewHolder(View view, Context context) {
            super(view);

            comment_user_post_image = view.findViewById(R.id.comment_user_post_image);
            comment_user_post_name = view.findViewById(R.id.comment_user_post_name);
            comment_user_post_headline = view.findViewById(R.id.comment_user_post_headline);
            comment_user_post_date = view.findViewById(R.id.comment_user_post_date);
            comment_content = view.findViewById(R.id.comment_content);
            comment_post_like_button = view.findViewById(R.id.comment_post_like_button);
            comment_post_likes_count = view.findViewById(R.id.comment_post_likes_count);
            comment_post_share_button = view.findViewById(R.id.comment_post_share_button);
            comment_image = view.findViewById(R.id.comment_image);
        }
    }
}

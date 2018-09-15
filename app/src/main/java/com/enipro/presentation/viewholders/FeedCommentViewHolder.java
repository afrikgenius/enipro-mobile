package com.enipro.presentation.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.R;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.databinding.CommentItemBinding;

public class FeedCommentViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    private CommentItemBinding binding;

    public FeedCommentViewHolder(Context context, CommentItemBinding binding) {
        super(binding.getRoot());
        this.context = context;
        this.binding = binding;

    }

    public void bind(FeedComment comment) {
        binding.setComment(comment);
        // If there is a comment image, load it otherwise remove view
        if (comment.getComment_image() == null)
            ((ViewGroup) binding.commentImage.getParent()).removeView(binding.commentImage);
        else {
            binding.commentImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(comment.getComment_image())
                    .apply(new RequestOptions().placeholder(R.drawable.bg_image))
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().fitCenter())
                    .apply(new RequestOptions().transform(new RoundedCorners(10)))
                    .into(binding.commentImage);
        }

    }
}

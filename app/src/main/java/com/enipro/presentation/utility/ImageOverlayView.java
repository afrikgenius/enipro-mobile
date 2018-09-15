package com.enipro.presentation.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enipro.R;

public class ImageOverlayView extends RelativeLayout {

    private TextView likes;
    private TextView comments;

    public ImageOverlayView(Context context) {
        super(context);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setCommentsCount(int commentsCount) {
        comments.setText(String.valueOf(commentsCount));
    }

    public void setLikesCount(int likesCount) {
        likes.setText(String.valueOf(likesCount));
    }

    private void init() {
        View view = inflate(getContext(), R.layout.photo_viewer_overlay, this);
        likes = view.findViewById(R.id.photo_viewer_likes);
        comments = view.findViewById(R.id.photo_viewer_comments);

        view.findViewById(R.id.comments_ic).setOnClickListener(v -> {

        });

        view.findViewById(R.id.likes_ic).setOnClickListener(v -> {
        });


        // TODO Set click listeners for both comment and like buttons.
    }
}

package com.enipro.presentation.base;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.enipro.R;
import com.enipro.presentation.feeds.FeedCommentActivity;

public class PhotoActivity extends AppCompatActivity {

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


        // Get the photo uri from intent and display using Picasso library
    }
}

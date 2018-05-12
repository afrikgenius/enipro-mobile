package com.enipro.presentation.feeds;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.model.Constants;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoActivity extends AppCompatActivity {


    @BindView(R.id.video)
    UniversalVideoView videoView;
    @BindView(R.id. media_controller)
    UniversalMediaController mediaController;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, VideoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        String videoPath = getIntent().getExtras().getString(Constants.VIDEO_PATH);
        Log.d(Application.TAG, videoPath);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.start();
    }
}

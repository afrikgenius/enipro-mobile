package com.enipro.presentation.viewholders

import android.content.Context
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enipro.R
import com.enipro.data.remote.model.Feed
import com.enipro.databinding.CardFeedItemBinding
import com.enipro.injection.Injection
import com.enipro.model.Constants
import com.enipro.model.DateTimeStringProcessor
import com.enipro.presentation.feeds.FeedPresenter
import com.enipro.presentation.generic.TagRecyclerAdapter
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FeedViewHolder(private val context: Context, private val binding: CardFeedItemBinding) : RecyclerView.ViewHolder(binding.root) {


    init {
        binding.tagsPostRecyclerview.setHasFixedSize(true)
        binding.tagsPostRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    fun bindTo(feedItem: Feed) {
        binding.feed = feedItem
        binding.presenter = FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), context)

        // TODO Come back to the saved feed option.
        // Check if there is a clause to set all items as saved
//        if (allSaved) {
//            holder.savedState = true;
//            holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark, null));
//        }

        // Check if application user has saved this feed item
//        List<SavedFeed> savedFeeds = Application.getActiveUser().getSavedFeeds();
//        for (SavedFeed savedFeed : savedFeeds) {
//            if (savedFeed.getFeedId().equals(feedItem.get_id().getOid())) {
//                // change saved drawable
//                holder.savedState = true;
//                holder.saveButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark, null));
//            }
//        }

        val date = feedItem.updated_at!!.utilDate
        binding.userPostDate.text = DateTimeStringProcessor.process(date)

        // Recycler view and adapter for news feed items
        val tagRecyclerAdapter = TagRecyclerAdapter(feedItem.tags, R.layout.tag_item)
        tagRecyclerAdapter.setCancellable(false)
        binding.tagsPostRecyclerview.adapter = tagRecyclerAdapter

        // Checks for either a video or an image and displays its content accordingly and if there is none, does nothing
        // If an image exists, the image is shown else if there is a video, the video is displayed.
        if (feedItem.content!!.image != null) {
            // Check for number of lines contained in text and set max to 4
            binding.content.maxLines = Constants.FEED_CONTENT_MAX_LINES_TEXT
            // Make the view visible and set the media content.
            binding.postImage.visibility = View.VISIBLE
            Glide.with(context)
                    .load(feedItem.content!!.image)
                    .apply(RequestOptions().placeholder(R.drawable.bg_image))
                    .into(binding.postImage)
        } else if (feedItem.content!!.video != null) {
            binding.content.maxLines = Constants.FEED_CONTENT_MAX_LINES_TEXT
            // Remove view from card
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

            // TODO This is a temporary solution here. This full adapter class should be refractored and well structured.
            binding.videoLayout.visibility = View.VISIBLE
            val player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector()) // TODO Insert a track selector
            binding.postVideo.player = player

            val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "enipro"))
            val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(feedItem.content!!.video))
            player.prepare(mediaSource)
            player.playWhenReady = true
        }


        binding.executePendingBindings() // Do this immediately
    }


    companion object {
        fun create(parent: ViewGroup, context: Context): FeedViewHolder {
            val itemBinding = CardFeedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FeedViewHolder(
                    binding = itemBinding,
                    context = context
            )
        }
    }
}
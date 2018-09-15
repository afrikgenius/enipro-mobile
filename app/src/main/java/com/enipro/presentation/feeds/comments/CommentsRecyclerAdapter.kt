package com.enipro.presentation.feeds.comments

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.enipro.data.remote.model.FeedComment
import com.enipro.databinding.CommentItemBinding
import com.enipro.presentation.viewholders.FeedCommentViewHolder

class CommentsRecyclerAdapter(val context: Context) : PagedListAdapter<FeedComment, RecyclerView.ViewHolder>(FeedComment.DIFF_CALLBACK) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedCommentViewHolder(context, itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FeedCommentViewHolder).bind(getItem(position))
    }
}
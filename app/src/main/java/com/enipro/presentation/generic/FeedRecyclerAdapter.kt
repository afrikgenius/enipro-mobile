package com.enipro.presentation.generic

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.enipro.data.remote.model.Feed
import com.enipro.presentation.utility.NetworkState
import com.enipro.presentation.viewholders.FeedViewHolder
import com.enipro.presentation.viewholders.NetworkStateItemViewHolder

class FeedRecyclerAdapter(private val context: Context, private val retryCallback: () -> Unit) :
        PagedListAdapter<Feed, RecyclerView.ViewHolder>(Feed.DIFF_CALLBACK) {

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                FeedViewHolder.create(parent, context)
            }
            TYPE_PROGRESS -> {
                NetworkStateItemViewHolder.create(parent, retryCallback)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
        // TODO Look into !! being passed into both bindTo. Very important cos crashes are not cool.
            TYPE_PROGRESS -> (holder as NetworkStateItemViewHolder).bindTo(networkState!!)
            TYPE_ITEM -> (holder as FeedViewHolder).bindTo(getItem(position)!!)
        }
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }


    companion object {
        const val TYPE_PROGRESS = 0
        const val TYPE_ITEM = 1
    }

}
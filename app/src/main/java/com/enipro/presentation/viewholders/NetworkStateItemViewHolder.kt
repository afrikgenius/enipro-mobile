package com.enipro.presentation.viewholders

import android.content.Context
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enipro.R
import com.enipro.databinding.NetworkStateItemBinding
import com.enipro.model.Enipro
import com.enipro.presentation.utility.NetworkState
import com.enipro.presentation.utility.NetworkState.Status.FAILED
import com.enipro.presentation.utility.NetworkState.Status.RUNNING

class NetworkStateItemViewHolder(
        private val context: Context,
        private val binding: NetworkStateItemBinding,
        private val retryCallback: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

    fun bindTo(networkState: NetworkState?) {
        binding.progressBar.visibility = toVisibility(networkState?.status == RUNNING)
        if (networkState?.status == FAILED) {
            val snackbar = Snackbar.make(binding.networkStateView, context.getString(R.string.cannot_load_feeds), Snackbar.LENGTH_LONG)
            val tv: TextView = snackbar.view.findViewById(android.support.design.R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)

            snackbar.setAction(R.string.retry) {
                Log.d(Enipro.APPLICATION, "Retry Called from ViewHolder")
                retryCallback()
            }
            snackbar.show()
        }
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = NetworkStateItemBinding.inflate(inflater, parent, false)
            return NetworkStateItemViewHolder(parent.context.applicationContext, binding, retryCallback)
        }

        fun toVisibility(constraint: Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}
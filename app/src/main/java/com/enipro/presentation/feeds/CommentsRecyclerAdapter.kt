package com.enipro.presentation.feeds

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.enipro.R
import com.enipro.data.remote.model.FeedComment
import com.enipro.data.remote.model.User
import com.enipro.model.DateTimeStringProcessor
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class CommentsRecyclerAdapter(val context: Context, var comments: MutableList<FeedComment>?, val presenter: FeedContract.CommentPresenter) : RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments?.get(position)

        // TODO Network calls should not be done here

        presenter.getUser(comment?.user) { user: User ->
            (context as Activity).runOnUiThread {
                holder.name.text = user.name
                holder.headline.text = user.headline
                Glide.with(context).load(user.avatar).apply(RequestOptions().placeholder(R.drawable.profile_image)).into(holder.image)
            }
        }
        holder.content.text = comment?.comment
        holder.date.text = DateTimeStringProcessor.process(comment?.updated_at?.utilDate)

        // If there is a comment image, load it otherwise remove view
        if (comment?.comment_image == null)
            (holder.comment_image.parent as ViewGroup).removeView(holder.comment_image)
        else {
            holder.comment_image.visibility = View.VISIBLE
            Glide.with(context)
                    .load(comment.comment_image)
                    .apply(RequestOptions().placeholder(R.drawable.bg_image))
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().fitCenter())
                    .apply(RequestOptions().transform(RoundedCorners(10)))
                    .into(holder.comment_image)
        }
    }

    override fun getItemCount(): Int {
        return comments?.size ?: 0
    }

    fun setCommentItems(newComments: MutableList<FeedComment>) {
        newComments.reverse()
        comments = newComments
        notifyDataSetChanged()
    }

    fun addItem(commentItem: FeedComment) {
        if (comments == null)
            comments = ArrayList()
        comments?.add(0, commentItem)
        notifyItemInserted(0) // Item added to the top of the view.
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: CircleImageView = view.findViewById(R.id.comment_user_post_image)
        val name: TextView = view.findViewById(R.id.comment_user_post_name)
        val headline: TextView = view.findViewById(R.id.comment_user_post_headline)
        val date: TextView = view.findViewById(R.id.comment_user_post_date)
        val content: TextView = view.findViewById(R.id.comment_content)
        val like: ImageButton = view.findViewById(R.id.comment_post_like_button)
        val likes_count: TextView = view.findViewById(R.id.comment_post_likes_count)
        val share: ImageButton = view.findViewById(R.id.comment_post_share_button)
        val comment_image: ImageView = view.findViewById(R.id.comment_image)

        // TODO Add resource for video and implement click listeners for like, share in init etc

        init {
            // TODO Click listener calls go here
        }

    }
}
package com.enipro.presentation.feeds

import android.app.Activity.RESULT_OK
import android.support.v4.app.Fragment
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enipro.R
import com.enipro.data.remote.model.Feed
import com.enipro.data.remote.model.User
import com.enipro.injection.Injection
import com.enipro.model.Utility
import com.enipro.presentation.generic.FeedRecyclerAdapter
import com.enipro.presentation.post.PostActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_feed.*
import org.parceler.Parcels

class FeedFragment : Fragment(), FeedContract.View {

    private var presenter: FeedPresenter? = null
    private var adapter: FeedRecyclerAdapter? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    companion object {

        var FEED_ADAPTER_STATE = "com.enipro.presentation.feeds.FEED_ADAPTER_STATE"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feed_fab.setOnClickListener {
            startActivityForResult(PostActivity.newIntent(activity), FeedContract.Presenter.POST_FEED_REQUEST)
        }


        // TODO Incorporate a view model into the design at this stage
        swiperefresh_feeds.setOnRefreshListener {
            presenter!!.loadFeeds {
                refreshLayoutItems(it)
                swiperefresh_feeds.isRefreshing = false
            }
        }

        feeds_recycler_view.setHasFixedSize(true)
        feeds_recycler_view.addItemDecoration(Utility.DividerItemDecoration(activity))
        setRVScrollEvent()

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        feeds_recycler_view.layoutManager = linearLayoutManager

        presenter = FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), activity)
        presenter!!.attachView(this)


        adapter = FeedRecyclerAdapter(activity, null, presenter, false)
        feeds_recycler_view.adapter = adapter

        // TODO This should be done by the view model instead of being done here
        presenter!!.loadFeeds {
            refreshLayoutItems(it)
        }

        // TODO Provide a channel id for this builder
        mBuilder = NotificationCompat.Builder(activity!!.applicationContext, "")
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(0, 0, true)
        notificationManager = activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    private fun refreshLayoutItems(result: List<Feed>?) {
        if (result == null)
            no_feed_layout.visibility = View.VISIBLE
        else {
            adapter!!.clear()
            adapter!!.setItems(result)
        }
    }

    fun setRVScrollEvent() {
        feeds_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 || dy < 0 && feed_fab.isShown)
                    feed_fab.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    feed_fab.show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (swiperefresh_feeds.isRefreshing)
            swiperefresh_feeds.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FeedContract.Presenter.POST_FEED_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
                presenter!!.processFeed(Parcels.unwrap(data!!.getParcelableExtra(FeedContract.Presenter.ACTIVITY_RETURN_KEY)))
        }
    }

    override fun showPostNotification() {
        mBuilder!!.setContentTitle(resources.getString(R.string.sending_post))
        notificationManager!!.notify(0, mBuilder!!.build())
    }

    override fun showCompleteNotification() {
        mBuilder!!.setContentTitle(resources.getString(R.string.sent))
        notificationManager!!.notify(0, mBuilder!!.build())
    }

    override fun showErrorMessage() {

    }

    override fun showErrorNotification() {
        mBuilder!!.setContentTitle(resources.getString(R.string.noti_error)).setProgress(0, 0, false)
        notificationManager!!.notify(0, mBuilder!!.build())
    }

    override fun updateUI(feedItem: Feed?, user: User?) {
        if (no_feed_layout.visibility == View.VISIBLE)
            no_feed_layout.visibility = View.GONE
        adapter!!.addItem(feedItem)
        feeds_recycler_view.smoothScrollToPosition(0)
    }

    override fun onSavedFeedsRetrieved(feeds: MutableList<Feed>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoading() {
        progress_view.visibility = View.VISIBLE
        progress_view.startAnimation()
        feeds_recycler_view.visibility = View.GONE
    }

    override fun hideLoading() {
        progress_view.visibility = View.GONE
        progress_view.stopAnimation()
        feeds_recycler_view.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (swiperefresh_feeds.isRefreshing)
            swiperefresh_feeds.isRefreshing = false
        if (presenter != null)
            presenter!!.detachView()
    }
}
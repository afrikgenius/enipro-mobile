package com.enipro.presentation.feeds

import android.app.Activity.RESULT_OK
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NotificationCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.Feed
import com.enipro.databinding.FragmentFeedBinding
import com.enipro.db.EniproDatabase
import com.enipro.injection.AppExecutors
import com.enipro.injection.Injection
import com.enipro.model.Utility
import com.enipro.presentation.generic.FeedRecyclerAdapter
import com.enipro.presentation.post.PostActivity
import com.enipro.presentation.utility.NetworkState
import com.enipro.repository.FeedRepository
import com.enipro.viewmodels.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_feed.*

class FeedFragment : Fragment(), FeedContract.View {

    private var presenter: FeedPresenter? = null
    private var adapter: FeedRecyclerAdapter? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null

    private lateinit var viewModel: FeedViewModel
    private var binding: FragmentFeedBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feed, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.feedsRecyclerView?.layoutManager = LinearLayoutManager(activity)
        viewModel = getViewModel()

        binding?.feedsRecyclerView?.setHasFixedSize(true)
        binding?.feedsRecyclerView?.addItemDecoration(Utility.DividerItemDecoration(activity))
        setRVScrollEvent()

        presenter = FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), activity)
        presenter?.attachView(this)

        initAdapter()
        initSwipeToRefresh()
        viewModel.fetchFeedData() // Call to initiate a change in the dummy live data
        binding?.feedFab?.setOnClickListener {
            startActivityForResult(PostActivity.newIntent(context), FeedContract.Presenter.POST_FEED_REQUEST)
        }

        // TODO Provide a channel id for this builder
        mBuilder = NotificationCompat.Builder(activity!!.applicationContext, "")
                .setSmallIcon(R.drawable.ic_launcher)
                .setProgress(0, 0, true)
        notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }


    /**
     * Creates the view model that uses a database + network approach to handle the paging library
     * to update the UI.
     */
    private fun getViewModel(): FeedViewModel {
        return ViewModelProviders.of(activity as FragmentActivity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = FeedRepository(
                        context = activity?.applicationContext!!,
                        db = EniproDatabase.getInstance(activity?.applicationContext as Application)!!,
                        api = Injection.eniproRestService(),
                        exceutor = AppExecutors().diskIO(),
                        compositeDisposable = CompositeDisposable() // TODO Should be injected with Dagger instead
                )
                @Suppress("UNCHECKED_CAST")
                return FeedViewModel(repository = repository) as T
            }
        })[FeedViewModel::class.java]
    }

    private fun initSwipeToRefresh() {
        viewModel.refreshState.observe(this, Observer {
            swiperefresh_feeds.isRefreshing = it == NetworkState.LOADING
        })

        swiperefresh_feeds.setOnRefreshListener {
            viewModel.refresh()
        }
    }


    private fun initAdapter() {
        adapter = FeedRecyclerAdapter(activity?.applicationContext!!) {
            viewModel.retry()
        }
        binding?.feedsRecyclerView?.adapter = adapter

        viewModel.feeds.observe(this, Observer {
            adapter?.submitList(it)
        })

        viewModel.networkState.observe(this, Observer {
            adapter?.setNetworkState(it)
        })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FeedContract.Presenter.POST_FEED_REQUEST) {
            if (resultCode == RESULT_OK)
                presenter?.processFeed(data?.getParcelableExtra(FeedContract.Presenter.ACTIVITY_RETURN_KEY))
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

    override fun updateUI(feedItem: Feed?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null)
            presenter!!.detachView()
    }
}
package com.enipro.repository

import android.arch.paging.PagedList
import android.util.Log
import com.enipro.Application
import com.enipro.data.remote.EniproRestService
import com.enipro.data.remote.model.Feed
import com.enipro.data.remote.model.PaginatedResponse
import com.enipro.model.Enipro
import com.enipro.presentation.utility.PagingRequestHelper
import com.enipro.presentation.utility.createStatusLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor


/**
 * The feed boundary callback gets notified when user reaches the edge of the list such that the database
 * cannot provide any more data.
 */
class FeedBoundaryCallback(
        private val pageHandler: PageHandler,
        private val api: EniproRestService,
        private val ioExecutor: Executor,
        private val responseHandler: (PaginatedResponse<Feed>) -> Unit,
        private val compositeDisposable: CompositeDisposable,
        private val networkPageSize: Int) : PagedList.BoundaryCallback<Feed>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned no items. Queries the backend for data.
     */
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            pageHandler.setPage(1)
            compositeDisposable.add(api.fetchFeed(Application.getAuthToken(), 1, networkPageSize)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess(it), onError(it)))
        }
    }

    /**
     * Called when the user has reached the end of the list.
     */
    override fun onItemAtEndLoaded(itemAtEnd: Feed) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            val page = pageHandler.getLastPageNumber()
            if (page != 0L)
                compositeDisposable.add(api.fetchFeed(Application.getAuthToken(), page, networkPageSize)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onSuccess(it), onError(it)))
            else it.recordSuccess() // This is a quick hack to stop the progress bar from running because the network state status will remain RUNNING once this method is called
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Feed) {
        // ignored since we only append to whats in the database
    }


    /**
     * Every time it gets new items, boundary callback simply inserts them into the database and the
     * paging library takes care of refreshing the list.
     */
    private fun insertFeedsIntoDB(response: PaginatedResponse<Feed>, it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            responseHandler(response)
            it.recordSuccess()
        }
    }


    private fun onSuccess(it: PagingRequestHelper.Request.Callback): Consumer<PaginatedResponse<Feed>> {
        return Consumer { response ->
            if (response.pagination?.links?.next != null) {
                val page = pageHandler.getLastPageNumber()
                val newPageNumber = page + 1
                pageHandler.setPage(newPageNumber)
            } else {
                pageHandler.setPage(0)
            } // Will use this to check before onItemAtEndLoaded is called.
            insertFeedsIntoDB(response, it)
        }
    }

    private fun onError(it: PagingRequestHelper.Request.Callback): Consumer<Throwable> {
        return Consumer { throwable ->
            Log.d(Enipro.APPLICATION, "Error occurred ${throwable.message}")
            it.recordFailure(throwable)
        }
    }
}
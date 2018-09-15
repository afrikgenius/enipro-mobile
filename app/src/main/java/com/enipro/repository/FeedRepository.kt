package com.enipro.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.content.Context
import android.support.annotation.MainThread
import android.util.Log
import com.enipro.Application
import com.enipro.data.remote.EniproRestService
import com.enipro.data.remote.model.Feed
import com.enipro.data.remote.model.PaginatedResponse
import com.enipro.db.EniproDatabase
import com.enipro.model.Enipro
import com.enipro.presentation.generic.Listing
import com.enipro.presentation.utility.NetworkState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

/**
 * Repository implementation which uses a database paged list and a boundary callback to return a list
 * that loads in pages.
 */
class FeedRepository(
        val context: Context,
        val db: EniproDatabase,
        private val api: EniproRestService,
        private val exceutor: Executor,
        private val compositeDisposable: CompositeDisposable,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE) {

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 20
    }

    private val pageHandler: PageHandler = PageHandler(context) // TODO This should be injected

    /**
     * Inserts the paginated response of feeds into the database
     */
    private fun insertIntoDB(response: PaginatedResponse<Feed>) {
        response.result?.let { feeds ->
            db.runInTransaction {
                db.feed().insert(feeds) // Insert feed
            }
        }
    }

    /**
     * When refresh is called, a fresh network request is called and on arrival, the database table is
     * cleared and all items are inserted in a transaction.
     *
     * Since the UI Observes the database, it will be automatically updated after the database transaction
     * is finished.
     */
    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        Log.d(Enipro.APPLICATION, "Refresh Called in Repository")
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        // The last requested page should be changed at this point
        pageHandler.setPage(1)
        compositeDisposable.add(api.fetchFeed(Application.getAuthToken(), 1, networkPageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            exceutor.execute {
                                db.runInTransaction {
                                    db.feed().deleteAll()
                                    insertIntoDB(it)
                                }
                                networkState.postValue(NetworkState.LOADED)
                            }
                        },
                        {
                            // retrofit calls this on the main thread so its safe to set value
                            networkState.value = NetworkState.error(it.message)
                        }))
        return networkState
    }


    /**
     * Returns feed data back in the form of a listing back to the UI
     */
    fun getData(pageSize: Int): Listing<Feed> {

        // boundary callback that is triggered when user reaches the edge of the paged list.
        val boundaryCallback = FeedBoundaryCallback(
                pageHandler = pageHandler,
                api = api,
                responseHandler = this::insertIntoDB,
                ioExecutor = exceutor,
                networkPageSize = networkPageSize,
                compositeDisposable = compositeDisposable
        )
        val dataFactory = db.feed().selectPagedFeeds()

        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(pageSize)
                .build()
        val builder = LivePagedListBuilder(dataFactory, pagedListConfig).setBoundaryCallback(boundaryCallback)
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        return Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }
}
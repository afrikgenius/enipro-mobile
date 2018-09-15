package com.enipro.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.enipro.model.Enipro
import com.enipro.repository.FeedRepository

class FeedViewModel(private val repository: FeedRepository) : ViewModel() {

    companion object {
        const val DEFAULT_LOAD_PAGE_SIZE = 20
    }


    private val dummyLiveData = MutableLiveData<Unit>()
    private val repoResult = Transformations.map(dummyLiveData) { repository.getData(DEFAULT_LOAD_PAGE_SIZE) }

    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val feeds = Transformations.switchMap(repoResult) { it.pagedList }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }


    /**
     * A dummy function that triggers the dummy live data to fetch results anytime it is called.
     */
    fun fetchFeedData() {
        Log.d(Enipro.APPLICATION, "Fetch Feed Called")
        dummyLiveData.value = Unit
    }

    fun refresh() {
        Log.d(Enipro.APPLICATION, "Refresh Called in ViewModel")
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        Log.d(Enipro.APPLICATION, "Retry Called in ViewModel")
        repoResult.value?.retry?.invoke()
    }
}
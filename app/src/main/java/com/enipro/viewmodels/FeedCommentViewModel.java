package com.enipro.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.util.Log;

import com.enipro.data.remote.model.FeedComment;
import com.enipro.model.Enipro;
import com.enipro.presentation.feeds.paging.FeedCommentDataSource;
import com.enipro.presentation.feeds.paging.PagingDataFactory;
import com.enipro.presentation.utility.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FeedCommentViewModel extends ViewModel {

    private static final int INITIAL_LOAD_SIZE = 30;
    private static final int PAGE_SIZE = 20;

    private Executor executor;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<FeedComment>> liveData;
    private PagingDataFactory<FeedCommentDataSource> dataFactory;
    private String feedId;


    public FeedCommentViewModel(String feedID) {
        this.feedId = feedID;
        init();
    }


    public void init() {
        executor = Executors.newFixedThreadPool(5);

        // TODO Inject the data source using dagger and also inject the factory.
        dataFactory = new PagingDataFactory<>(new FeedCommentDataSource(feedId));

        networkState = Transformations.switchMap(dataFactory.getMutableLiveData(), FeedCommentDataSource::getNetworkState);

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(INITIAL_LOAD_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();

        // TODO Set a boundary callback for persistence in database
        liveData = new LivePagedListBuilder(dataFactory, pagedListConfig).setFetchExecutor(executor).build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<FeedComment>> getLiveData() {
        return liveData;
    }

    /**
     * Invalidates the data source for the paging library to trigger new data.
     * TODO: This should not necessarily invalidate all data in datasource in a case of integrating
     * TODO: with room database, it should just fetch new data instead of the whole thing from network.
     */
    public void invalidateDataSource() {
        dataFactory.getMutableLiveData().getValue().invalidate();
    }

}

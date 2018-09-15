package com.enipro.presentation.feeds.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.PaginatedResponse;
import com.enipro.presentation.utility.NetworkState;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseDataSource<K, V> extends PageKeyedDataSource<K, V> {

    CompositeDisposable compositeDisposable;
    MutableLiveData networkState;
    MutableLiveData initialLoading;

    public BaseDataSource() {
        // TODO Remember to inject rest api here.
        networkState = new MutableLiveData();

        compositeDisposable = new CompositeDisposable(); // TODO DAgger 2 should work here
        initialLoading = new MutableLiveData();
    }

    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<K> params, @NonNull LoadInitialCallback<K, V> callback) {
        initialLoading.postValue(NetworkState.LOADING);
        networkState.postValue(NetworkState.LOADING);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<K> params, @NonNull LoadCallback<K, V> callback) {
        networkState.postValue(NetworkState.LOADING);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<K> params, @NonNull LoadCallback<K, V> callback) {

    }

    public void onError(Throwable t) {
        String errorMessage = t == null ? "unknown error" : t.getMessage();
        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
    }

    public void onInitialResponseSuccess(PaginatedResponse<V> response, @NonNull LoadInitialCallback<Long, V> callback) {
        callback.onResult(response.getResult(), null, 2l);
        initialLoading.postValue(NetworkState.LOADED);
        networkState.postValue(NetworkState.LOADED);
    }

    public void onMoreResponseSuccess(PaginatedResponse<V> response, @NonNull LoadCallback<Long, V> callback, @NonNull LoadParams<Long> params) {
        Long nextKey = (response.getPagination().getLinks().getNext() == null) ? null : params.key + 1;
        callback.onResult(response.getResult(), nextKey);
        networkState.postValue(NetworkState.LOADED);
    }
}

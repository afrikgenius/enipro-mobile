package com.enipro.presentation.feeds.paging;

import android.support.annotation.NonNull;

import com.enipro.Application;
import com.enipro.data.remote.model.Feed;
import com.enipro.injection.Injection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FeedDataSource extends BaseDataSource<Long, Feed> {

    private static final int DEFAULT_PAGE_SIZE = 20;

    public FeedDataSource() {
        super();

    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, Feed> callback) {
        super.loadInitial(params, callback);
        compositeDisposable.add(Injection.eniproRestService()
                .fetchFeed(Application.getAuthToken(), 1, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onInitialResponseSuccess(response, callback), this::onError));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, Feed> callback) {
        super.loadAfter(params, callback);
        compositeDisposable.add(Injection.eniproRestService()
                .fetchFeed(Application.getAuthToken(), params.key, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onMoreResponseSuccess(response, callback, params), this::onError));
    }
}

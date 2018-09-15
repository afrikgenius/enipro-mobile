package com.enipro.presentation.feeds.paging;

import android.support.annotation.NonNull;

import com.enipro.Application;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.injection.Injection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FeedCommentDataSource extends BaseDataSource<Long, FeedComment> {

    private String feedId;

    public FeedCommentDataSource(String feed_id) {
        super();
        this.feedId = feed_id;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Long, FeedComment> callback) {
        super.loadInitial(params, callback);
        compositeDisposable.add(Injection.eniproRestService()
                .fetchComments(feedId, 1, Application.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onInitialResponseSuccess(response, callback), this::onError));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Long, FeedComment> callback) {
        super.loadAfter(params, callback);
        compositeDisposable.add(Injection.eniproRestService()
                .fetchComments(feedId, params.key, Application.getAuthToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> onMoreResponseSuccess(response, callback, params), this::onError));
    }
}

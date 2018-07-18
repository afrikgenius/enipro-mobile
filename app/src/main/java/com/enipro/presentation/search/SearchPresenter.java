package com.enipro.presentation.search;


import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.User;
import com.enipro.presentation.base.BasePresenter;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public class SearchPresenter extends BasePresenter<SearchContract.View> implements SearchContract.Presenter {

    SearchPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler) {
        super(restService, ioScheduler, mainScheduler);
    }


    @Override
    public void search(String term) {
        checkViewAttached();
        getView().showLoading();
        addDisposable(searchUsers(term).subscribeOn(ioScheduler).observeOn(mainScheduler)
                .subscribe(this::onNext, this::onError, this::onComplete));
    }

    public void onNext(List<User> users) {
        getView().hideLoading();
        getView().showSearchResults(users);
    }

    public void onError(Throwable e) {
        getView().hideLoading();
        getView().showError(
                e.getMessage()); // TODO You probably don't want this error to show to users - Might want to show a friendlier message :)
    }

    void onComplete() {

    }

    @Override
    public Observable<List<User>> searchUsers(final String searchTerm) {
        return Observable.defer(() -> restService.searchEniporUsers(searchTerm))
                .retryWhen(observable -> observable.flatMap(o -> {
                    if (o instanceof IOException) {
                        return Observable.just(null);
                    }
                    return Observable.error(o);
                }));
    }
}

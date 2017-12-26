package com.enipro.presentation.base;

import com.enipro.data.remote.EniproRestService;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    /* Private Instance Variable */
    private T view;

    private CompositeDisposable compositeSubscription = new CompositeDisposable();

    protected final Scheduler ioScheduler;
    protected final Scheduler mainScheduler;
    protected EniproRestService restService;

    public BasePresenter(EniproRestService restService, Scheduler ioScheduler, io.reactivex.Scheduler mainScheduler){
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
        this.restService = restService;
    }

    @Override
    public void attachView(T mvpView) {
        this.view = mvpView;
    }

    @Override
    public void detachView() {
        compositeSubscription.clear();
        view = null;
    }

    public T getView() {
        return view;
    }

    public void checkViewAttached() {
        if (!isViewAttached())
            throw new MvpViewNotAttachedException();
    }

    private boolean isViewAttached() {
        return view != null;
    }

    protected void addDisposable(Disposable disposable) {
        this.compositeSubscription.add(disposable);
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        MvpViewNotAttachedException() {
            super("Please call Presenter.attachView(MvpView) before requesting data to the Presenter");
        }
    }
}
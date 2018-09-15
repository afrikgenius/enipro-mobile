package com.enipro.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class FeedCommentViewModelFactory implements ViewModelProvider.Factory {

    private String feedID;

    public FeedCommentViewModelFactory(String feedID) {
        this.feedID = feedID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FeedCommentViewModel(feedID);
    }
}

package com.enipro.presentation.feeds.paging;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

public class PagingDataFactory<Source extends BaseDataSource> extends DataSource.Factory {

    private MutableLiveData<Source> mutableLiveData;
    private Source dataSource;

    public PagingDataFactory(Source dataSource) {
        this.mutableLiveData = new MutableLiveData<>();
        this.dataSource = dataSource;
    }

    @Override
    public DataSource create() {
        mutableLiveData.postValue(dataSource);
        return dataSource;
    }

    public MutableLiveData<Source> getMutableLiveData() {
        return mutableLiveData;
    }
}

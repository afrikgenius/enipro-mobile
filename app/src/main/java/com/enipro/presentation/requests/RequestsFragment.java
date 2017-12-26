package com.enipro.presentation.requests;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.enipro.R;
import com.enipro.injection.Injection;
import com.enipro.presentation.feeds.FeedContract;
import com.enipro.presentation.feeds.FeedPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class RequestsFragment extends Fragment implements RequestsContract.View {


    private RequestsContract.Presenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_requests, container, false);

        presenter = new RequestsPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), getContext());
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.enipro.presentation.search;


import com.enipro.data.remote.model.User;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;

import io.reactivex.Observable;

public class SearchContract {
    interface View extends MvpView {

        /**
         * Show search results from search request.
         */
        void showSearchResults(List<User> userList);

        /**
         * Shows an error when search returns an error
         *
         * @param message error message to display
         */
        void showError(String message);


        /**
         * Shows a loading progress bar.
         */
        void showLoading();

        /**
         *
         */
        void hideLoading();
    }

    interface Presenter extends MvpPresenter<SearchContract.View> {

        void search(String term);

        Observable<List<User>> searchUsers(final String searchTerm);

    }
}

package com.enipro.presentation.profile;


import com.enipro.data.remote.model.Request;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;

import io.reactivex.Observable;

public class ProfileContract {

    interface View extends MvpView {

        void showProgress();

        void dismissProgress();

        void onCircleAdded();

        void onCircleRemoved();

        void onNetworkAdded();

        void onNetworkRemoved();

        void onRequestSent();

        void onMentoringRequestSent();

        void onAddCircleRequestValidated();

        void onAddNetworkRequestValidated();

        void onError(Throwable throwable);

        void onRequestDeleted();

        void onAvailableRequestSent();
    }

    interface Presenter extends MvpPresenter<ProfileContract.View> {

        /**
         * Logs the current active user out of the application.
         */
        void logout();

        void getUser(String id, LocalCallback<User> localCallback);

        void addCircle(UserConnection userConnection);

        void addNetwork(UserConnection userConnection);

        void requestAddCircle(String sender, String recipient);

        void requestAddNetwork(String sender, String recipient);

        void removeCircle(String user_id);

        void removeNetwork(String user_id);

        void requestMentoring(String sender, String recipient);

        void availableForMentoring(String sender);

        void getRequest(String sender, String recipient, LocalCallback<List<Request>> requestLocalCallback);

        void getRequest(String request_id, LocalCallback<Request> localCallback);

        void deleteRequest();
    }

    interface EditView extends MvpView {
        void onProfileUpdated(User user);

        void showLoading();

        void hideLoading();
    }

    interface EditPresenter extends MvpPresenter<ProfileContract.EditView> {
        void updateUser(User user, String user_id);
    }

    public interface StudentSearchView extends MvpView {
        void showLoading();

        void hideLoading();

        void showSearchResults(List<User> users);

        void showError(String errorMessage);
    }

    public interface StudentSearchPresenter extends MvpPresenter<ProfileContract.StudentSearchView> {
        void search(String term);

        Observable<List<User>> searchUsers(final String searchTerm);
    }
}

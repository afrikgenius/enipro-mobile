package com.enipro.presentation.chat;

import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;


public class ChatContract {

    interface View extends MvpView {

    }

    interface Presenter extends MvpPresenter<ChatContract.View> {

        /**
         * Request code used to open user search activity for result.
         */
        int SEARCH_CHAT = 0x001;

        /**
         * Load all users a particular user can have a chat with.
         * @param user the information of the primary user.
         */
        void loadChatUsers(User user, LocalCallback<List<User>> localCallback);
    }

}

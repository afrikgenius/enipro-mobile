package com.enipro.presentation.messages;


import android.content.Context;

import com.enipro.data.remote.model.ChatUser;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.model.LocalCallback;
import com.enipro.presentation.base.MvpPresenter;
import com.enipro.presentation.base.MvpView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public class MessagesContract {

    interface View extends MvpView {

        void onMessagesLoaded();

        void onMessageDataReceived(User user, Message message);

        void onNullUsers();
    }

    interface MessageView extends MvpView {
        void onFirebaseMessageSent();

        void onMessageReceived(Message message);

        void onChatCreateError();

        void onChatCreateSuccess(UserConnection connection);

        void onMessageError();
    }

    interface MessagePresenter extends MvpPresenter<MessagesContract.MessageView> {
        void getMessageFromFirebaseUser(String senderUid, String receiverUid);

        void sendMessageToFirebaseUser(final Context context, final Message message, final String senderFirebaseToken, final User sender, String receiverFirebaseToken, final User user);

        void getChatUserFromFirebase(String email, LocalCallback<ChatUser> userLocalCallback);
    }

    interface Presenter extends MvpPresenter<MessagesContract.View> {

        void loadPreviousMessages();
    }

    interface SearchView extends MvpView {

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

        void onConnectedUsersLoaded(List<User> users);
    }

    interface SearchPresenter extends MvpPresenter<MessagesContract.SearchView> {

        void search(String term);

        /**
         * Connected users are those in users circle or network and are have a connection with.
         */
        void loadConnectedUsers();

    }

}

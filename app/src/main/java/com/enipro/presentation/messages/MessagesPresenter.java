package com.enipro.presentation.messages;


import android.content.Context;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.User;
import com.enipro.model.Constants;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.Scheduler;

public class MessagesPresenter extends BasePresenter<MessagesContract.View> implements MessagesContract.Presenter {


    MessagesPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
    }

    /**
     * Loads all chat users data with last messages that the application user has had a conversation with.
     */
    @Override
    public void loadPreviousMessages() {
        // TODO Since the messages are always saved, they should be retrieved for each user else
        // TODO gotten from firebase fresh.
        // TODO This should be preferably gotten from Application active user.
        // Get all user in connection with this current user in terms of chat.
        addDisposable(restService.getChats(Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(this::onNext, this::onError));
    }

    private void onNext(List<User> users) {
        if (users == null) {
            getView().onNullUsers();
            return;
        }

        User appUser = Application.getActiveUser();

        // For each item in the list, grab user UID and match with application UID to get the last message information
        // of the conversation
        for (int i = 0, size = users.size(); i < size; i++) {
            User user = users.get(i);

            final String room_type_1 = appUser.getFirebaseUID() + "_" + user.getFirebaseUID();
            final String room_type_2 = user.getFirebaseUID() + "_" + appUser.getFirebaseUID();
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(room_type_1)) {
                        // Grab last message
                        databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).orderByKey().limitToLast(1)
                                .addChildEventListener(new LastMessageValueEvent(user));
                    } else if (dataSnapshot.hasChild(room_type_2)) {
                        databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).orderByKey().limitToLast(1)
                                .addChildEventListener(new LastMessageValueEvent(user));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    private void onError(Throwable throwable) {
    }


    /**
     * Event listener that listens for value event in
     */
    private class LastMessageValueEvent implements ChildEventListener {

        User user;

        public LastMessageValueEvent(User user) {
            this.user = user;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Message message = dataSnapshot.getValue(Message.class);

            // Send message information and user information to view to handle
            getView().onMessageDataReceived(user, message);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

    }

}
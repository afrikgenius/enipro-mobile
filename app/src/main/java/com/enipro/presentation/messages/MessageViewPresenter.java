package com.enipro.presentation.messages;

import android.content.Context;

import com.enipro.Application;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.ChatUser;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.base.BasePresenter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.reactivex.Scheduler;

public class MessageViewPresenter extends BasePresenter<MessagesContract.MessageView> implements MessagesContract.MessagePresenter {


    MessageViewPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
    }

    @Override
    public void getChatUserFromFirebase(String uid, LocalCallback<ChatUser> userLocalCallback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userLocalCallback.respond(dataSnapshot.getValue(ChatUser.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // TODO Do something here.
                    }
                });
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Message chat, final String senderFirebaseToken, final User sender, final String receiverFirebaseToken, final User receiver) {
        final String room_type_1 = chat.getSenderUid() + "_" + chat.getReceiverUid();
        final String room_type_2 = chat.getReceiverUid() + "_" + chat.getSenderUid();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS)
                .getRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().setValue(chat);
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).push().setValue(chat);
                        } else {
                            databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).push().setValue(chat);
                            getMessageFromFirebaseUser(chat.getSenderUid(), chat.getReceiverUid());

                            // Create chat item with user information in user object for both users.
//                            final UserConnection userConnection = new UserConnection(receiver.get_id().getOid());
//                            addDisposable(restService.createChat(sender.get_id().getOid(), userConnection)
//                                    .subscribeOn(ioScheduler)
//                                    .observeOn(mainScheduler)
//                                    .subscribe(user -> getView().onChatCreateSuccess(userConnection), throwable -> getView().onChatCreateError()));

                            // Create chat item on the receivers user information also
//                            final UserConnection secondUserConnection = new UserConnection(sender.get_id().getOid());
//                            addDisposable(restService.createChat(receiver.get_id().getOid(), secondUserConnection)
//                                    .subscribeOn(ioScheduler)
//                                    .observeOn(mainScheduler)
//                                    .subscribe());
                        }
                        // TODO Save the message in local storage.
//                        new AppExecutors().diskIO().execute(() -> EniproDatabase.getInstance(context).messageDao().insertMessage(chat));

                        // send push notification to the receiver
                        // Since chat sender is the application user, the username can be the application user's name
                        String username = Application.getActiveUser().getName();
                        String id = Application.getActiveUser().get_id().getOid();
                        Utility.sendPushNotificationToReceiver(username, id, chat.getText(), chat.getSenderUid(), senderFirebaseToken, receiverFirebaseToken, Constants.MESSAGE_ID);
                        getView().onFirebaseMessageSent();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to send message.
                    }
                });
    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new FirebaseChildEventListener());
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new FirebaseChildEventListener());
                } else {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().onMessageError();
            }
        });
    }


    private class FirebaseChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Message message = dataSnapshot.getValue(Message.class);

            // When message gets out, convert firebase timestamp to date and setCreated date of message object.
            // TODO Apply timezone to get accurate time
            message.setCreatedAt(new Date(message.getTimestampLong()));
            getView().onMessageReceived(message);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }
    }
}

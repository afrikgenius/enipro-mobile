package com.enipro.firebase;

import android.util.Log;

import com.enipro.Application;
import com.enipro.model.Enipro;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Handles all push notifications from and to firebase cloud messaging.
 */

public class EniproMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(Enipro.APPLICATION, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(Enipro.APPLICATION, "Message data payload: " + remoteMessage.getData());
            Log.d(Enipro.APPLICATION, "Message From:" + remoteMessage.getFrom());

            // Check the remote message "from" field to know topic the message is for.
            if(remoteMessage.getFrom().equals(Application.FirebaseTopics.TOPICS_PREFIX + Application.FirebaseTopics.FEED_TOPIC)) {
                // TODO Send it to feed fragment to update UI


            } else {
                // TODO In a case of request, update request fragment UI.
            }

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // TODO Use Firebase Job Dispatcher
            } else {
                // TODO Display
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(Enipro.APPLICATION, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        // In some situations, FCM may not deliver a message. This occurs when there are too many messages (>100) pending for your app
        // on a particular device at the time it connects or if the device hasn't connected to FCM in more than one month.
        // In these cases, you may receive a callback to FirebaseMessagingService.onDeletedMessages() When the app instance receives
        // this callback, it should perform a full sync with your app server. If you haven't sent a message to the app on that device
        // within the last 4 weeks, FCM won't call onDeletedMessages()

        //TODO Handle this type of messages.
    }
}

package com.enipro.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.events.NotificationEvent;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.enipro.presentation.messages.MessageActivity;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.requests.RequestBroadcastReceiver;
import com.enipro.presentation.requests.RequestsFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Handles all push notifications from and to firebase cloud messaging.
 */

public class EniproMessagingService extends FirebaseMessagingService implements MessagingContract.View {

    private MessagingContract.Presenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();

        presenter = new MessagingPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread());
        presenter.attachView(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            // Check the remote message "from" field to know topic the message is for.
            if (remoteMessage.getFrom().equals(Application.FirebaseTopics.TOPICS_PREFIX + Application.FirebaseTopics.FEED_TOPIC)) {
                // TODO Send it to feed fragment to update UI
            } else {
                // An incoming chat message
                String title = remoteMessage.getData().get(Constants.TITLE);
                String message = remoteMessage.getData().get(Constants.TEXT);
                String username = remoteMessage.getData().get(Constants.USERNAME);
                String uid = remoteMessage.getData().get(Constants.UID);
                String fcmToken = remoteMessage.getData().get(Constants.FCM_TOKEN);
                String unique_id = remoteMessage.getData().get(Constants.UNIQUE_IDENTIFIER);

                NotificationCompat.Builder notificationBuilder;
                // Based on the unique id, we can know the type of notification it is and provide appropriate actions.
                switch (unique_id) {
                    case Constants.MESSAGE_ID:
                        // Don't show notification if chat activity is open.
                        if (!Application.isMessageActivityOpen()) {
                            // Get user information based on username.
                            presenter.getUser(username, user -> showMessageNotification(user, title, message, username, uid, fcmToken));
                        } else {
                            EventBus.getDefault().post(new NotificationEvent(title, message, username, uid, fcmToken));
                        }
                        break;
                    case Constants.MENTORING_REQUEST_ID:
                        presenter.getUser(username, user -> {
                            Intent mentoringIntent = new Intent(this, RequestsFragment.class);
                            mentoringIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Create pending intent to accept or reject request
                            // TODO Remember to work on the accept and decline intents to allow actions to be passed.
                            Intent acceptIntent = new Intent(this, RequestBroadcastReceiver.class);
                            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent declineIntent = new Intent(this, RequestBroadcastReceiver.class);
                            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Utility.showRequestNotification(this, user, mentoringIntent, acceptPendingIntent,
                                    declinePendingIntent, title, message, username, Constants.MENTORING_REQ_NOTIFICATION_CHANNEL);
                        });
                        break;
                    case Constants.MENTORING_REQUEST_REC:
                        presenter.getUser(username, user -> {
                            Intent profileIntent = new Intent(this, ProfileActivity.class);
                            profileIntent.putExtra(Constants.APPLICATION_USER, user);
                            Utility.showNotification(this, profileIntent, title, message, username, uid, fcmToken);

                            // TODO Since mentoring request has been accepted, a mentoring schedule must have been specified by mentor.
                            // TODO Register an alarm manager to alert at that schedule specified.
                        });
                        break;
                    case Constants.CIRCLE_REQUEST:
                        presenter.getUser(username, user -> {
                            Intent circleRequestIntent = new Intent(this, RequestsFragment.class);
                            circleRequestIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Create pending intent to accept or reject request
                            // TODO Remember to work on the accept and decline intents to allow actions to be passed.
                            Intent acceptIntent = new Intent(this, RequestBroadcastReceiver.class);
                            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent declineIntent = new Intent(this, RequestBroadcastReceiver.class);
                            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Utility.showRequestNotification(this, user, circleRequestIntent, acceptPendingIntent,
                                    declinePendingIntent, title, message, username, Constants.CIRCLE_REQUEST_NOTIFICATION_CHANNEL);
                        });
                        break;
                    case Constants.NETWORK_REQUEST:
                        presenter.getUser(username, user -> {
                            Intent networkRequestIntent = new Intent(this, RequestsFragment.class);
                            networkRequestIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Create pending intent to accept or reject request
                            Intent acceptIntent = new Intent(this, RequestBroadcastReceiver.class);
                            acceptIntent.putExtra(Constants.BROADCAST_REQUEST_EXTRA, Constants.NETWORK_REQUEST_BROADCAST);
                            PendingIntent acceptPendingIntent = PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent declineIntent = new Intent(this, RequestBroadcastReceiver.class);
                            PendingIntent declinePendingIntent = PendingIntent.getBroadcast(this, 0, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Utility.showRequestNotification(this, user, networkRequestIntent, acceptPendingIntent,
                                    declinePendingIntent, title, message, username, Constants.NETWORK_REQUEST_NOTIFICATION_CHANNEL);
                        });
                        break;
                }
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
//            Log.d(Enipro.APPLICATION, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    public void showMessageNotification(User user, String title, String message, String receiver, String receiverUid, String firebaseToken) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        }

        // Intent that opens the message activity when notification is clicked.
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(Constants.MESSAGE_CHAT_RETURN_KEY, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Create remote input and notification reply action button
        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(Constants.KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_launcher,
                getString(R.string.reply_label), replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.MESSAGE_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .addAction(action);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(receiverUid, 0, notificationBuilder.build());
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

package com.enipro.presentation.messages;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.ChatUser;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.User;
import com.enipro.data.remote.model.UserConnection;
import com.enipro.db.EniproDatabase;
import com.enipro.events.NotificationEvent;
import com.enipro.injection.AppExecutors;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageActivity extends AppCompatActivity implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener, MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        DateFormatter.Formatter, MessagesContract.MessageView {


    private static final int TOTAL_MESSAGES_COUNT = 100;

    private MessagesContract.MessagePresenter presenter;

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private MessagesList messagesList;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private User applicationUser;
    private User user;
    private ChatUser chatUser;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, MessageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.chat_message_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        applicationUser = Application.getActiveUser();
        // Get intent
        user = Parcels.unwrap(getIntent().getParcelableExtra(Constants.MESSAGE_CHAT_RETURN_KEY));
//        user = getIntent().getParcelableExtra(Constants.MESSAGE_CHAT_RETURN_KEY);
        getSupportActionBar().setTitle(user.getName());

        presenter = new MessageViewPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);

        // Get users profile containing firebaseUID and firebaseToken based on the UID from firebase Database
        presenter.getChatUserFromFirebase(user.getFirebaseUID(), (user) -> chatUser = user);

        // Image Loader used in the chat messages.
        imageLoader = (imageView, url) -> Picasso.with(this).load(url).into(imageView);

        messagesList = findViewById(R.id.messagesList);
        messagesAdapter = new MessagesListAdapter<>(applicationUser.getId(), null); // To omit avatars with messages
        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(messagesAdapter);

        MessageInput input = findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

        // Retrieve messages from firebase with the user passed into the activity if there was a previous chat.
        presenter.getMessageFromFirebaseUser(applicationUser.getFirebaseUID(), user.getFirebaseUID());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this); // Register to listen to incoming message events
    }

    @Override
    protected void onResume() {
        super.onResume();
        Application.setMessageActivityOpen(true);
        // Register broadcast receiver to receive messages from notification
        registerReceiver(messageReceiver, new IntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Application.setMessageActivityOpen(false);
        // Unregister the message broadcast receiver.
        unregisterReceiver(messageReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                // TODO Delete message from firebase
                // presenter.deleteMessages();
                messagesAdapter.deleteSelectedMessages();

                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                Utility.showToast(this, R.string.copied_message, true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages();
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
//        new Handler().postDelayed(() -> {
//            ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
//            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
//            messagesAdapter.addToEnd(messages, false);
//        }, 1000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return (message) -> {
            String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.getCreatedAt());

            String text = message.getText();
            if (text == null) text = "[attachment]";

            return String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.getUser().getName(), text, createdAt);
        };
    }


    @Override
    public void onAddAttachments() {
        new MaterialDialog.Builder(this)
                .items(R.array.attachement_items)
                .itemsCallback((dialog, itemView, position, text) -> {
                    switch (position) {
                        case 0:
                            launchGallery();
                            break;
                        case 1:
                            selectAttachement();
                            break;
                    }
                })
                .show();
    }

    /**
     * Requests a permission to access photos and opens the gallery to select a picture.
     */
    private void launchGallery() {
        boolean check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, getResources().getString(R.string.external_storage_rationale));
        if (check) {
            // Open gallery
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
        }
    }

    /**
     * Opens an intent to select a document with the mime types listed.
     */
    void selectAttachement() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimetypes = {
                "application/pdf",
                "application/powerpoint",
                "application/msword",
                "text/plain",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, Utility.DOC_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "Camera permission has now been granted.");
                // Launch Camera to take a picture
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
            } else {
                Log.i(Application.TAG, "Camera permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "External Storage permission has now been granted");
                // Open gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
            } else {
                Log.i(Application.TAG, "External storage permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utility.GALLERY_REQUEST_CODE: // Photo picked from gallery
                if (resultCode == RESULT_OK) {
//                    Bitmap imageBitmap = null;
//                    try {
//                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
//                        // Store a reference to original bitmap to send to Firebase
//                    } catch (FileNotFoundException fnfe) {
//                        FirebaseCrash.log(fnfe.getMessage());
//                    }
//                    StorageReference storageReference = FirebaseStorage
//                            .getInstance()
//                            .getReference()
//                            .child(Constants.FIREBASE_CHATS_IMAGES_LOCATION + applicationUser.getFirebaseUID() + "/" + Utility.getRandomIdentifier() + ".jpg");
//                    Message message = new Message(null, applicationUser.getEmail(), user.getEmail(), applicationUser.getFirebaseUID(), user.getFirebaseUID(), null);
////                    message.setImage(new Message.Image(downloadURL));
//                    message.setUser(applicationUser);
//                    Utility.uploadImageFirebase(storageReference, imageBitmap, (downloadURL) -> {
//                        presenter.sendMessageToFirebaseUser(this, message, applicationUser.getFirebaseToken(), applicationUser, user.getFirebaseToken(), user);
//                    });
                }
                break;
            case Utility.DOC_REQUEST_CODE:
                break;
            default: // DO NOTHING
        }
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        String messageString = input.toString();

        // Send message to firebase and add the message to UI
        Message message = new Message(null, applicationUser.getEmail(), user.getEmail(), applicationUser.getFirebaseUID(), user.getFirebaseUID(), messageString);
        message.setUser(applicationUser);
        presenter.sendMessageToFirebaseUser(this, message, applicationUser.getFirebaseToken(), applicationUser, user.getFirebaseToken(), user);
        return true;
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return getString(R.string.date_header_today);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Application.setMessageActivityOpen(false);
        EventBus.getDefault().unregister(this); // Unregister listening to incoming message events.
    }

    /**
     * Acts as a subscriber that is called when a notification event from the publisher i.e
     * When a message notification is received.
     *
     * @param notificationEvent
     */
    @Subscribe
    public void onNotificationEvent(NotificationEvent notificationEvent) {
        Log.d(Application.TAG, "Notification Event Called.");
        // Should only be called in a case where there is no data in the adapter
        if (messagesAdapter == null || messagesAdapter.getItemCount() == 0)
            presenter.getMessageFromFirebaseUser(applicationUser.getFirebaseUID(), notificationEvent.getUid());
    }

    /**
     * Triggered when the message has been sent to the recipient of the message.
     */
    @Override
    public void onFirebaseMessageSent() {

    }

    @Override
    public void onMessageReceived(Message message) {
        // Add message to UI then if an error occurs, show a snack bar and remove message
        messagesAdapter.addToStart(message, true);
    }

    @Override
    public void onMessageError() {
        // Show snackbar and remove message from message list adapter

    }

    @Override
    public void onChatCreateError() {

    }

    @Override
    public void onChatCreateSuccess(UserConnection connection) {
        // Save the connection data into the database
        User appUser = Application.getActiveUser();
        List<UserConnection> chats = appUser.getChats();
        chats.add(connection);
        appUser.setChats(chats);
        Application.setActiveUser(appUser);
        new AppExecutors().diskIO().execute(() -> EniproDatabase.getInstance(this).userDao().updateUser(Application.getActiveUser()));
    }


    // TODO Complete the broadcast receiver information.
    /**
     * Broadcast receiver that receives messages for a particular conversation with a particular user
     * which is identified by the user information passed in the receiver intent.
     */
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}

package com.enipro.presentation.messages

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.ChatUser
import com.enipro.data.remote.model.Message
import com.enipro.data.remote.model.User
import com.enipro.data.remote.model.UserConnection
import com.enipro.db.EniproDatabase
import com.enipro.events.NotificationEvent
import com.enipro.injection.AppExecutors
import com.enipro.injection.Injection
import com.enipro.model.Constants
import com.enipro.model.Utility
import com.enipro.presentation.messages.viewmodel.MessageViewModel
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_message.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.parceler.Parcels
import java.text.SimpleDateFormat
import java.util.*


class MessageActivity : AppCompatActivity(), MessagesContract.MessageView, MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener, MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        DateFormatter.Formatter {

    private var user: User? = null
    private var presenter: MessagesContract.MessagePresenter? = null
    private var messagesAdapter: MessagesListAdapter<Message>? = null
    private var viewModel: MessageViewModel? = null
    private var menuInstance: Menu? = null
    private var selectionCount: Int? = null

    // TODO Change the name of the class with a better representation
    private var chatUser: ChatUser? = null


    /**
     * Broadcast receiver that receives messages for a particular conversation with a particular user
     * which is identified by the user information passed in the receiver intent.
     */
    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO This should be completed to act on the received message
        }
    }


    companion object {

        val TOTAL_MESSAGES_COUNT = 100

        fun newIntent(context: Context, user: User?): Intent {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra(Constants.MESSAGE_CHAT_RETURN_KEY, Parcels.wrap(user))
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setSupportActionBar(chat_message_toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        // Initialise the view model to hold data for the activity class.
        viewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)

        user = Parcels.unwrap(intent!!.getParcelableExtra(Constants.MESSAGE_CHAT_RETURN_KEY))
        supportActionBar!!.title = user!!.name


        // TODO This should be done in the view model
        presenter = MessageViewPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this)
        presenter!!.attachView(this)

        // TODO This should also be in the view model
        presenter!!.getChatUserFromFirebase(user!!.getFirebaseUID(), { user -> chatUser = user })

        // Image loader is null in order to omit messages with avatars
        messagesAdapter = MessagesListAdapter(Application.getActiveUser().id, null)
        messagesAdapter!!.enableSelectionMode(this)
        messagesAdapter!!.setLoadMoreListener(this)
        messagesAdapter!!.setDateHeadersFormatter(this)
        messagesList.setAdapter(messagesAdapter)

        input.setInputListener(this)
        input.setAttachmentsListener(this)

        // TODO This should also be in the view model
        presenter!!.getMessageFromFirebaseUser(Application.getActiveUser().getFirebaseUID(), user!!.getFirebaseUID())
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Application.setMessageActivityOpen(false)
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        Application.setMessageActivityOpen(true)
        // Register broadcast receiver to receive messages from notification
        registerReceiver(messageReceiver, IntentFilter())
    }

    override fun onPause() {
        super.onPause()
        Application.setMessageActivityOpen(false)
        unregisterReceiver(messageReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInstance = menu
        menuInflater!!.inflate(R.menu.chat_actions_menu, menu)
        onSelectionChanged(0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_delete -> {
                // TODO Delete message from firebase
                // presenter.deleteMessages();
                messagesAdapter!!.deleteSelectedMessages()
            }
            R.id.action_copy -> {
                messagesAdapter!!.copySelectedMessagesText(this, getMessageStringFormatter(), true)
                Utility.showToast(this, R.string.copied_message, true)
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (selectionCount != 0)
            messagesAdapter!!.unselectAllItems()
        else
            super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Utility.GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
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
            }
            Utility.DOC_REQUEST_CODE -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "Camera permission has now been granted.")
                // Launch Camera to take a picture
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE)//zero can be replaced with any action codes
            } else {
                Log.i(Application.TAG, "Camera permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "External Storage permission has now been granted")
                // Open gallery
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE)
            } else {
                Log.i(Application.TAG, "External storage permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun format(date: Date?): String {
        if (DateFormatter.isToday(date))
            return getString(R.string.date_header_today)
        else if (DateFormatter.isYesterday(date))
            return getString(R.string.date_header_yesterday)
        else
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR)
    }

    override fun onAddAttachments() {
        MaterialDialog.Builder(this)
                .items(R.array.attachement_items)
                .itemsCallback({ _, _, position, _ ->
                    when (position) {
                        0 -> launchGallery()
                        1 -> selectAttachement()
                    }
                }).show()
    }

    private fun launchGallery() {
        val check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE,
                resources.getString(R.string.external_storage_rationale))
        if (check) {
            // Open gallery
            val pickPhotoIntent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickPhotoIntent, Utility.GALLERY_REQUEST_CODE)
        }
    }

    /**
     * Opens an intent to select a document with the mime types listed.
     */
    private fun selectAttachement() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        val mimetypes = arrayOf("application/pdf", "application/powerpoint", "application/msword", "text/plain", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        startActivityForResult(intent, Utility.DOC_REQUEST_CODE)
    }

    /**
     * Acts as a subscriber that is called when a notification event from the publisher i.e
     * When a message notification is received.
     */
    @Subscribe
    internal fun onNotificationEvent(notificationEvent: NotificationEvent?) {
        if (messagesAdapter == null || messagesAdapter!!.itemCount == 0)
            presenter!!.getMessageFromFirebaseUser(Application.getActiveUser().firebaseUID, notificationEvent!!.uid) // TODO This should be done in the view model also
    }

    private fun getMessageStringFormatter(): MessagesListAdapter.Formatter<Message> {
        return object : MessagesListAdapter.Formatter<Message> {
            override fun format(message: Message?): String {
                val createdAt = SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message!!.createdAt)

                var text: String? = message.getText()
                if (text == null) text = "[attachment]"

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.user.name, text, createdAt)
            }
        }
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        val appUser = Application.getActiveUser()
        // Send message to firebase and add message to UI
        val message = Message(null, appUser.email, user!!.email, appUser.firebaseUID, user!!.firebaseUID, input.toString())
        message.user = appUser

        // TODO This should obviously be taken care of in the view model
        presenter!!.sendMessageToFirebaseUser(this, message, appUser.firebaseToken, appUser, user!!.firebaseToken, user)
        return true
    }

    override fun onSelectionChanged(count: Int) {
        selectionCount = count
        menuInstance!!.findItem(R.id.action_delete).isVisible = count > 0
        menuInstance!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT)
            loadMessages()
    }

    // TODO This function should be implemented in kotlin and made to load more messages
    private fun loadMessages() {
        //        new Handler().postDelayed(() -> {
        //            ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
        //            lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
        //            messagesAdapter.addToEnd(messages, false);
        //        }, 1000);
    }


    /**
     * Triggered when the message has been sent to the recipient of the message.
     */
    override fun onFirebaseMessageSent() {

    }

    override fun onMessageReceived(message: Message) {
        // Add message to UI then if an error occurs, show a snack bar and remove message
        messagesAdapter!!.addToStart(message, true)
    }

    override fun onMessageError() {
        // Show snackbar and remove message from message list adapter

    }

    override fun onChatCreateError() {

    }

    override fun onChatCreateSuccess(connection: UserConnection) {
        // Save the connection data into the database
        val appUser = Application.getActiveUser()
        val chats = appUser.getChats()
        chats.add(connection)
        appUser.setChats(chats)
        Application.setActiveUser(appUser)
        AppExecutors().diskIO().execute { EniproDatabase.getInstance(this).userDao().updateUser(Application.getActiveUser()) }
    }


}
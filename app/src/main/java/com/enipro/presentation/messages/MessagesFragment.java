package com.enipro.presentation.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Dialog;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.Message;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.enipro.presentation.feeds.FeedContract;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

public class MessagesFragment extends Fragment implements MessagesContract.View, DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    private MessagesContract.Presenter presenter;
    private MessageRecyclerAdapter adapter;

    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;
    private DialogsList dialogsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        FloatingActionButton fab = view.findViewById(R.id.add_message_fab);
        fab.setOnClickListener(v -> startActivityForResult(MessagesSearch.newIntent(getActivity()), Constants.MESSAGE_SEARCH));

        presenter = new MessagesPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), getActivity());
        presenter.attachView(this);

        presenter.loadPreviousMessages();

        imageLoader = (imageView, url) -> Picasso.with(getActivity()).load(url).into(imageView);

        dialogsList = view.findViewById(R.id.dialogsList);
        dialogsAdapter = new DialogsListAdapter<>(R.layout.message_item, imageLoader);

        // Load previous dialogs if exists from Firebase Real time Database.
//        dialogsAdapter.setItems(DialogsFixtures.getDialogs());
        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);
        dialogsList.setAdapter(dialogsAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == Constants.MESSAGE_SEARCH) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get user information from intent
                User user = data.getParcelableExtra(Constants.MESSAGE_SEARCH_RETURN_KEY);
                // Open message activity to start a conversation with the user
                Intent chatIntent = MessageActivity.newIntent(getActivity());
                chatIntent.putExtra(Constants.MESSAGE_CHAT_RETURN_KEY, user);
                startActivity(chatIntent);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // This is essential because messages should be loaded immediately a new chat is initiated.
        presenter.loadPreviousMessages();
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        // Get clicked dialog from the dialog list adapter and send the user and get back result
        User user = dialog.getUsers().get(0); // TODO This is in a case of a single user and not multiple users in the chat
        Intent chatIntent = MessageActivity.newIntent(getActivity());
        chatIntent.putExtra(Constants.MESSAGE_CHAT_RETURN_KEY, user);
        startActivity(chatIntent);
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        Utility.showToast(getActivity(), dialog.getDialogName(), false);
    }

    public MessagesContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void onMessagesLoaded() {

    }

    @Override
    public void onMessageDataReceived(User user, Message message) {
        boolean exists = dialogsAdapter.updateDialogWithMessage(user.getId(), message);
        if (!exists) {
            ArrayList<User> users = new ArrayList<>();
            users.add(user);
            Dialog dialog = new Dialog(user.getId(), user.getName(), user.getAvatar(), users, message, 0);
            dialogsAdapter.addItem(0, dialog);
        }
    }

    /**
     * Makes the view elements showing no chat activity visible.
     */
    @Override
    public void onNullUsers() {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
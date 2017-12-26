package com.enipro.presentation.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enipro.R;
import com.enipro.data.remote.model.Dialog;
import com.enipro.model.Utility;
import com.enipro.presentation.search.UserSearchActivity;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements DateFormatter.Formatter, DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    private DialogsList dialogsList;
    ImageLoader imageLoader;
    DialogsListAdapter<Dialog> dialogsAdapter;

    /**
     * Returns a new intent to open an instance of this activity.
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ChatActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.fab_chat);
        fab.setOnClickListener(v ->  {
            // Launch search to search for users to have a chat with
            startActivityForResult(ChatUsersActivity.newIntent(this), ChatContract.Presenter.SEARCH_CHAT);
        });


        // Image loader used to load dialog images
        imageLoader = (imageView, url) -> Picasso.with(this).load(url).into(imageView);

        dialogsList = findViewById(R.id.dialogsList);
        dialogsAdapter = new DialogsListAdapter<>(imageLoader);

        // TODO Get active chat with current user.
        dialogsAdapter.setItems(new ArrayList<>());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);
        dialogsAdapter.setDatesFormatter(this);
        dialogsList.setAdapter(dialogsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = ChatMessagesActivity.newIntent(this);
        startActivity(intent);
    }

    @Override
    public void onDialogLongClick(Dialog dialog) {
        Utility.showToast(
                this,
                dialog.getDialogName(),
                false);
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.date_header_yesterday);
        } else if (DateFormatter.isCurrentYear(date)) {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}

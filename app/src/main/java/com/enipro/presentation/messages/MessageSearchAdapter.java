package com.enipro.presentation.messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Message Search Adapter
 */
public class MessageSearchAdapter extends RecyclerView.Adapter<MessageSearchAdapter.ViewHolder> {

    private final Context context;
    private List<User> users;
    private LocalCallback<User> itemLocalCallback;

    /**
     * Creates a new Message Search Adapter.
     *
     * @param users             list of users for this adapter
     * @param context           the context which the adapter resides
     * @param itemClickCallback callback function to be executed when a view item is clicked.
     */
    MessageSearchAdapter(List<User> users, Context context, LocalCallback<User> itemClickCallback) {
        this.users = users;
        this.context = context;
        this.itemLocalCallback = itemClickCallback;
    }

    @Override
    public int getItemCount() {
        if (users == null) {
            return 0;
        }
        return users.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_users_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.textViewHeadline.setText(user.getHeadline());
        holder.textViewName.setText(user.getName());
        holder.item.setOnClickListener(v -> {
            // Initiate a chat with the user if non exists else open chat with user
            itemLocalCallback.respond(user);
        });
        Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.profile_image).into(holder.imageViewAvatar);
    }

    void setItems(List<User> userList) {
        this.users = userList;
        notifyDataSetChanged();
    }

    void clear() {
        this.users = null;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewHeadline;
        final TextView textViewName;
        final CircleImageView imageViewAvatar;
        final View item;

        ViewHolder(View view) {
            super(view);

            imageViewAvatar = view.findViewById(R.id.chat_user_image);
            textViewName = view.findViewById(R.id.chat_user_name);
            textViewHeadline = view.findViewById(R.id.chat_user_headline);
            item = view.findViewById(R.id.item);
        }

    }
}

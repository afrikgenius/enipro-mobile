package com.enipro.presentation.chat;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A recycler adapter that holds data for users another user can possibly have a chat with.
 */
public class ChatUsersRecyclerAdapter extends RecyclerView.Adapter<ChatUsersRecyclerAdapter.ViewHolder> {


    /**
     * List of users in the adapter
     */
    private List<User> users;
    private Context context;

    ChatUsersRecyclerAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed_item, parent, false);
        return new ChatUsersRecyclerAdapter.ViewHolder(view, context);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.user_type.setText(user.getUserType());
        holder.user_name.setText(user.getName());
        ((Activity)context).runOnUiThread(() -> Picasso.with(context).load(user.getAvatar()).into(holder.user_image));
    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     * @param user the user to be added.
     */
    public void addItem(User user){
        this.users.add(0, user);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    /**
     * Removes a user from the recycler view.
     * @param user the item to remove
     * @param position the position of the item to remove.
     */
    public void removeItem(User user, int position){
        this.users.remove(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView user_name;
        TextView user_type;


        Context context;

        ViewHolder(View view, Context context){
            super(view);
            this.context = context;

            user_image = view.findViewById(R.id.chat_user_image);
            user_name = view.findViewById(R.id.chat_user_name);
            user_type = view.findViewById(R.id.chat_user_type);
        }

    }
}

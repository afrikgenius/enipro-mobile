package com.enipro.presentation.search;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.model.LocalCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context context;
    private List<User> users;
    private LocalCallback<User> itemLocalCallback;

    /**
     * Creates a new Search Adapter.
     *
     * @param users             list of users for this adapter
     * @param context           the context which the adapter resides
     * @param itemClickCallback callback function to be executed when a view item is clicked.
     */
    SearchAdapter(List<User> users, Context context, LocalCallback<User> itemClickCallback) {
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
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_search_item, parent, false);
        return new SearchAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        User user = users.get(position);

        holder.textViewHeadline.setText(user.getHeadline());
        holder.textViewName.setText(user.getName());
        holder.search_item.setOnClickListener(v -> {
            // Initiate a chat with the user if non exists else open chat with user
            itemLocalCallback.respond(user);
        });
        RequestOptions options = new RequestOptions().placeholder(R.drawable.profile_image);
        Glide.with(context).load(user.getAvatar()).apply(options).into(holder.imageViewAvatar);
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
        final View search_item;

        ViewHolder(View view) {
            super(view);

            imageViewAvatar = view.findViewById(R.id.message_search_avatar);
            textViewName = view.findViewById(R.id.message_search_name);
            textViewHeadline = view.findViewById(R.id.message_search_headline);
            search_item = view.findViewById(R.id.search_item);
        }

    }
}

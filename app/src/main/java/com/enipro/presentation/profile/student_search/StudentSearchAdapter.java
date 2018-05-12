package com.enipro.presentation.profile.student_search;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.R;
import com.enipro.data.remote.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentSearchAdapter extends RecyclerView.Adapter<StudentSearchAdapter.ViewHolder> {

    private List<User> students;
    private Context context;
    private List<User> selectedUsers = new ArrayList<>();

    public StudentSearchAdapter(Context context, List<User> students) {
        this.context = context;
        this.students = students;
    }

    @Override
    public int getItemCount() {
        if (students == null) {
            return 0;
        }
        return students.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = students.get(position);

        holder.user_headline.setText(user.getHeadline());
        holder.user_name.setText(user.getName());
        holder.item.setOnClickListener(view -> {
            if (holder.selected) {
                holder.selected = false;
                holder.check.setVisibility(View.VISIBLE);
                selectedUsers.remove(user);
            } else {
                holder.selected = true;
                holder.check.setVisibility(View.GONE);
                // Add current user to selected user list
                selectedUsers.add(user);
            }
        });
        RequestOptions options = new RequestOptions().placeholder(R.drawable.profile_image);
        Glide.with(context).load(user.getAvatar()).apply(options).into(holder.user_image);
    }

    void setItems(List<User> userList) {
        this.students = userList;
        notifyDataSetChanged();
    }

    void clear() {
        this.students = null;
        notifyDataSetChanged();
    }

    /**
     * Returns the selected users in the student search.
     *
     * @return
     */
    List<User> getSelectedUsers() {
        return selectedUsers;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final CircleImageView user_image;
        final TextView user_name;
        final TextView user_headline;
        final View item;
        final CardView cardItem;
        final ImageView check;
        boolean selected = false;

        ViewHolder(View view) {
            super(view);
            user_image = view.findViewById(R.id.user_image);
            user_name = view.findViewById(R.id.user_name);
            user_headline = view.findViewById(R.id.user_headline);
            item = view.findViewById(R.id.item);
            cardItem = view.findViewById(R.id.card_item);
            check = view.findViewById(R.id.check);
        }
    }
}

package com.enipro.presentation.messages;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {


    private List<Object> messages;
    private Context context;
    private MessagesContract.Presenter presenter;

    MessageRecyclerAdapter(Context context, List<Object> messages, MessagesContract.Presenter presenter){
        this.context = context;
        this.messages = messages;
        this.presenter = presenter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageRecyclerAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object message = messages.get(position);
        // TODO
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Holder to hold view items used in for the recycler adapter.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chat_user_image;
        RobotoTextView chat_user_name;
        RobotoTextView chat_last_message;
        RobotoTextView chat_last_time;

        ViewHolder(View view, Context context) {
            super(view);
//
//            chat_user_image = view.findViewById(R.id.chat_user_image);
//            chat_user_name = view.findViewById(R.id.chat_user_name);
//            chat_last_message = view.findViewById(R.id.chat_last_message);
//            chat_last_time = view.findViewById(R.id.chat_last_time);
        }
    }
}

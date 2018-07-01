package com.enipro.presentation.requests;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Request;
import com.enipro.model.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsRecyclerAdapter extends RecyclerView.Adapter<RequestsRecyclerAdapter.ViewHolder> {

    private List<Request> requests;
    private RequestsContract.Presenter presenter;
    private Context context;
    private CoordinatorLayout layout;
    private RequestInteractor interactor;

    RequestsRecyclerAdapter(Context context, List<Request> requests, RequestsContract.Presenter presenter, CoordinatorLayout layout, RequestInteractor interactor) {
        this.requests = requests;
        this.presenter = presenter;
        this.context = context;
        this.layout = layout;
        this.interactor = interactor;
    }

    @Override
    public int getItemCount() {
        if (requests == null) {
            return 0;
        }
        return requests.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requests.get(position);
        // Based on the user type.
        // TODO This should not be done based on user type and it should be done based on request type.
        switch (request.getType()) {
            case Constants.TYPE_CIRCLE:
                break;
            case Constants.TYPE_NETWORK:
                break;
            case Constants.TYPE_MENTORING:
                break;
            case Constants.TYPE_TUTORING:
                break;
        }


        switch (Application.getActiveUser().getUserType().toUpperCase()) {
            case Constants.STUDENT:
                // Check if the request has been accepted then remove it.
                if (request.getStatus().toUpperCase().equals(Constants.CONNECTION_ACCEPTED)) {
                    removeItem(position);
                    break;
                }
                // Get user information of recipient.
                presenter.getUser(request.getRecipient(), user -> {
                    ((Activity) context).runOnUiThread(() -> {
                        // Get data returned from callback which is a user object.
                        String user_name = user.getFirstName() + " " + user.getLastName();
                        holder.req_user_name.setText(user_name);
                        holder.req_user_headline.setText(user.getHeadline());
                        Glide.with(context)
                                .load(user.getAvatar())
                                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                                .into(holder.req_user_image);
                    });
                });
                holder.decline_request.setVisibility(View.GONE);
                holder.accept_request.setVisibility(View.GONE);
                holder.sent_indicator.setVisibility(View.VISIBLE);
                break;
            case Constants.PROFESSIONAL:
                // Get user information of sender.
                presenter.getUser(request.getSender(), user -> {
                    ((Activity) context).runOnUiThread(() -> {
                        // Get data returned from callback which is a user object.
                        String user_name = user.getFirstName() + " " + user.getLastName();
                        holder.req_user_name.setText(user_name);
                        holder.req_user_headline.setText(user.getHeadline());
                        Glide.with(context)
                                .load(user.getAvatar())
                                .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                                .into(holder.req_user_image);
                        holder.accept_request.setOnClickListener(view -> interactor.processAcceptance(request, user, position));
                    });
                });
                holder.decline_request.setOnClickListener(view -> interactor.processDecline(request, position));
                break;

            // TODO Support should be added for company and professional.
        }
    }

    public void setItems(List<Request> requests) {
        Collections.reverse(requests); // Reverse the list
        this.requests = requests;
        notifyDataSetChanged();
    }

    public void addItem(Request request) {
        if (requests == null)
            requests = new ArrayList<>();
        this.requests.add(0, request);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    public void removeItem(int position) {
        this.requests.remove(position);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView req_user_image;
        TextView req_user_name;
        TextView req_user_headline;
        TextView sent_indicator;
        ImageButton accept_request;
        ImageButton decline_request;

        ViewHolder(View view) {
            super(view);
            req_user_image = view.findViewById(R.id.req_user_image);
            req_user_name = view.findViewById(R.id.req_user_name);
            req_user_headline = view.findViewById(R.id.req_user_headline);
            accept_request = view.findViewById(R.id.accept_request);
            decline_request = view.findViewById(R.id.cancel_request);
            sent_indicator = view.findViewById(R.id.sent_indicator);
        }

    }
}

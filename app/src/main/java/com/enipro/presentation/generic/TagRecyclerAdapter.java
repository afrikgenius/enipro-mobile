package com.enipro.presentation.generic;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.enipro.R;

import java.util.ArrayList;
import java.util.List;

public class TagRecyclerAdapter extends RecyclerView.Adapter<TagRecyclerAdapter.ViewHolder> {

    List<String> tags;
    boolean cancellable = true;
    private int alternateResource;

    /**
     * @param tags       the list of tags to use as data for view.
     * @param resourceID an alternate resource layout to use for inflation. Insert 0 to signify no alternate resource
     */
    public TagRecyclerAdapter(List<String> tags, int resourceID) {
        this.tags = tags;
        this.alternateResource = resourceID;
    }

    @Override
    public int getItemCount() {
        if (tags == null) {
            return 0;
        }
        return tags.size();
    }

    @Override
    public TagRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resource = R.layout.interests_item;
        if (alternateResource != 0)
            resource = alternateResource;
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return new TagRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagRecyclerAdapter.ViewHolder holder, int position) {
        holder.tag_text.setText(tags.get(position));
        if (cancellable)
            holder.tag_cancel.setVisibility(View.VISIBLE);
        holder.tag_cancel.setOnClickListener(v -> {
            tags.remove(position);
            notifyDataSetChanged();
        });
    }

    /**
     * Allows for a the items in the recycler adapter to be cancellable or not.
     *
     * @param cancel true or false
     */
    public void setCancellable(boolean cancel) {
        this.cancellable = cancel;
    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     *
     * @param tag the feed comment item to be added.
     */
    public void addItem(String tag) {
        if (tags == null)
            tags = new ArrayList<>();
        this.tags.add(tag);
        notifyDataSetChanged();
    }

    public List<String> getItems() {
        List<String> retData = new ArrayList<>();
        retData.addAll(tags);
        return retData; // TODO Return a copy and not the object itself.
    }

    /**
     * Sets the items of the adapter as passed
     *
     * @param items the items for the adapter.
     */
    public void setItems(List<String> items) {
        this.tags = items;
        notifyDataSetChanged();
    }

    /**
     * Removes a feed comment item from the recycler view.
     *
     * @param tag      the item to remove
     * @param position the position of the item to remove.
     */
    public void removeItem(String tag, int position) {
        this.tags.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Clear the list in the adapter
     */
    public void clear() {
        this.tags = null;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for tags text view.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tag_text;
        ImageButton tag_cancel;

        ViewHolder(View view) {
            super(view);
            tag_text = view.findViewById(R.id.tag_text);
            tag_cancel = view.findViewById(R.id.tag_cancel);
        }
    }
}

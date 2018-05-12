package com.enipro.presentation.generic;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.R;
import com.enipro.data.remote.model.Experience;

import java.util.List;

public class ExpRecyclerAdapter extends RecyclerView.Adapter<ExpRecyclerAdapter.ViewHolder> {

    // Private Instance Variables
    private List<Experience> experiences;

    public ExpRecyclerAdapter(List<Experience> experiences) {
        this.experiences = experiences;
    }

    @Override
    public int getItemCount() {
        if (experiences == null) {
            return 0;
        }
        return experiences.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.experience_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Experience experience = experiences.get(position);
        holder.exp_organisation.setText(experience.getOrganisation());
        holder.exp_industry.setText(experience.getIndustry());
        holder.exp_role.setText(experience.getRole());
        holder.exp_year_from.setText(experience.getFrom());
        holder.exp_year_to.setText(experience.getTo());
    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     *
     * @param experience the experience item to be added.
     */
    public void addItem(Experience experience) {
        this.experiences.add(0, experience);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    /**
     * Removes an item from the recycler view.
     *
     * @param position the position of the item to remove.
     */
    public void removeItem(int position) {
        this.experiences.remove(position);
        notifyItemRemoved(position);
    }


    public void setItems(List<Experience> experiences) {
        this.experiences = experiences;
        notifyDataSetChanged();
    }

    public void clear() {
        this.experiences = null;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // View items used in view holder
        RobotoTextView exp_organisation;
        RobotoTextView exp_industry;
        RobotoTextView exp_role;
        RobotoTextView exp_year_from;
        RobotoTextView exp_year_to;

        public ViewHolder(View view) {

            super(view);
            exp_organisation = view.findViewById(R.id.exp_organisation);
            exp_industry = view.findViewById(R.id.exp_industry);
            exp_role = view.findViewById(R.id.exp_role);
            exp_year_from = view.findViewById(R.id.exp_year_from);
            exp_year_to = view.findViewById(R.id.exp_year_to);
        }
    }
}

package com.enipro.presentation.generic;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.R;
import com.enipro.data.remote.model.Education;

import java.util.List;


public class EduRecyclerAdapter extends RecyclerView.Adapter<EduRecyclerAdapter.ViewHolder> {

    // Private Instance Variables
    private List<Education> educationList;

    public EduRecyclerAdapter(List<Education> educationList) {
        this.educationList = educationList;
    }

    @Override
    public int getItemCount() {
        if (educationList == null) {
            return 0;
        }
        return educationList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.education_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Education education = educationList.get(position);
        holder.education_school.setText(education.getSchool());
        String degree_course = education.getDegree() + ", " + education.getCourse();
        holder.degree_course.setText(degree_course);
        holder.education_year_from.setText(education.getFrom());
        holder.education_year_to.setText(education.getTo());

    }

    /**
     * Adds an item to the recycler adapter to be displayed in the recycler view.
     *
     * @param education the education item to be added.
     */
    public void addItem(Education education) {
        this.educationList.add(0, education);
        notifyItemInserted(0); // Item added to the top of the view.
    }

    /**
     * Removes an item from the recycler view.
     *
     * @param position the position of the item to remove.
     */
    public void removeItem(int position) {
        this.educationList.remove(position);
        notifyItemRemoved(position);
    }


    public void setItems(List<Education> eduList) {
        this.educationList = eduList;
        notifyDataSetChanged();
    }

    public void clear() {
        this.educationList = null;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // View items used in view holder
        RobotoTextView education_school;
        RobotoTextView degree_course;
        RobotoTextView education_year_from;
        RobotoTextView education_year_to;

        public ViewHolder(View view) {

            super(view);
            education_school = view.findViewById(R.id.education_school);
            degree_course = view.findViewById(R.id.degree_course);
            education_year_from = view.findViewById(R.id.education_year_from);
            education_year_to = view.findViewById(R.id.education_year_to);
        }
    }
}

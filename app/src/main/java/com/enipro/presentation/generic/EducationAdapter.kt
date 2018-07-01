package com.enipro.presentation.generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enipro.R
import com.enipro.data.remote.model.Education

class EducationAdapter(private var educationList: MutableList<Education>) : RecyclerView.Adapter<EducationAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return educationList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.education_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val education = educationList[position]
        holder!!.school.text = education.school
        val degreeCourse = education.degree + ", " + education.course
        holder.course.text = degreeCourse
        holder.from.text = education.from
        holder.to.text = education.to
    }

    fun addItem(education: Education) {
        educationList.add(0, education)
        notifyItemInserted(0)
    }

    /**
     * Removes an item from the adapter
     */
    fun removeItem(position: Int) {
        educationList.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val school: TextView = view.findViewById(R.id.education_school)
        val course: TextView = view.findViewById(R.id.degree_course)
        val from: TextView = view.findViewById(R.id.education_year_from)
        val to: TextView = view.findViewById(R.id.education_year_to)

    }
}
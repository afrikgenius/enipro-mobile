package com.enipro.presentation.generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enipro.R
import com.enipro.data.remote.model.Experience

class ExperienceAdapter(private var experiences: MutableList<Experience>) : RecyclerView.Adapter<ExperienceAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return experiences.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.experience_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val experience = experiences[position]
        holder.organisation.text = experience.organisation
        holder.industry.text = experience.industry
        holder.role.text = experience.role
        holder.from.text = experience.from
        holder.to.text = experience.to
    }

    fun addItem(experience: Experience) {
        experiences.add(0, experience)
        notifyItemInserted(0)
    }

    /**
     * Removes an item from the adapter
     */
    fun removeItem(position: Int) {
        experiences.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val organisation: TextView = view.findViewById(R.id.exp_organisation)
        val industry: TextView = view.findViewById(R.id.exp_industry)
        val role: TextView = view.findViewById(R.id.exp_role)
        val from: TextView = view.findViewById(R.id.exp_year_from)
        val to: TextView = view.findViewById(R.id.exp_year_to)
    }
}
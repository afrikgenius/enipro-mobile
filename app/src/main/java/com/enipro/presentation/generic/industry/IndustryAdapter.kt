package com.enipro.presentation.generic.industry

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enipro.R

class IndustryAdapter(val context: Context, val items: List<String>) : RecyclerView.Adapter<IndustryAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.industry_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.industry_name.text = items[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val industry_name: TextView = view.findViewById(R.id.industry_name)
//        val checkSign: ImageView = view.findViewById(R.id.check_sign)
    }
}
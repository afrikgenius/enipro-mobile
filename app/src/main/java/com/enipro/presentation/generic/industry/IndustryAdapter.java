package com.enipro.presentation.generic.industry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.R;
import com.enipro.presentation.generic.FeedRecyclerAdapter;

import java.util.List;

public class IndustryAdapter extends RecyclerView.Adapter<IndustryAdapter.ViewHolder> {

    // Private Instance variables
    List<String> items;
    Context context;

    public IndustryAdapter(Context context, List<String> items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.industry_item, parent, false);
        return new IndustryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String industry = items.get(position);
        holder.industry_name.setText(industry);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RobotoTextView industry_name;
        ImageView checkSign;

        ViewHolder(View view) {
            super(view);

            industry_name = view.findViewById(R.id.industry_name);
            checkSign = view.findViewById(R.id.check_sign);
        }

    }
}

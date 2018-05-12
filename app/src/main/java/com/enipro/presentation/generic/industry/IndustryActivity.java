package com.enipro.presentation.generic.industry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.enipro.R;
import com.enipro.model.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IndustryActivity extends AppCompatActivity {

    @BindView(R.id.industry_list_recycler)
    RecyclerView mRecyclerView;

    private IndustryAdapter industryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industry);
        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        // Create an adapter and pass items into adapter
        industryAdapter = new IndustryAdapter(this, null);
        mRecyclerView.setAdapter(industryAdapter);

    }
}

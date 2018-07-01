package com.enipro.presentation.generic.industry

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.enipro.R
import com.enipro.model.Utility
import kotlinx.android.synthetic.main.activity_industry.*

class IndustryActivity : AppCompatActivity() {

    private var adapter: IndustryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_industry)

        industry_list_recycler.setHasFixedSize(true)
        industry_list_recycler.addItemDecoration(Utility.DividerItemDecoration(this))

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        industry_list_recycler.layoutManager = linearLayoutManager

        adapter = IndustryAdapter(this, resources.getStringArray(R.array.industries).toList())
        industry_list_recycler.adapter = adapter
    }
}
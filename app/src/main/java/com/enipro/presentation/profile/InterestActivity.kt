package com.enipro.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.enipro.Application
import com.enipro.R
import com.enipro.db.EniproDatabase
import com.enipro.injection.AppExecutors
import com.enipro.injection.Injection
import com.enipro.model.Utility
import com.enipro.presentation.generic.TagRecyclerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_interest.*

class InterestActivity : AppCompatActivity(), ProfileContract.View {

    private var tagRecyclerAdapter: TagRecyclerAdapter? = null
    internal var presenter: ProfileContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest)

        presenter = ProfilePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this)
        presenter?.attachView(this)

        // Recycler view and adapter for tags
        int_tags_recycler!!.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        int_tags_recycler!!.layoutManager = linearLayoutManager

        tagRecyclerAdapter = TagRecyclerAdapter(Application.getActiveUser().interests, 0)
        int_tags_recycler!!.adapter = tagRecyclerAdapter

        // Click listener for the continue button
        save_interests!!.setOnClickListener { _ ->
            // Verify data and advance process
            if (tagRecyclerAdapter!!.itemCount == 0) {
                // Show a snack bar showing error
                Utility.showSnackBar(coordinatorLayout, "You must add interests to continue", true)
            } else {
                Application.getActiveUser().interests = tagRecyclerAdapter!!.items
                // Send update to server and persist information in local storage and finish activity
                AppExecutors().diskIO().execute { EniproDatabase.getInstance(this)!!.user().updateUser(Application.getActiveUser()) }
                // TODO Come back to this
                //                presenter.updateUser(Application.getActiveUser());
                finish()
            }
        }

        interests!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {

                // Check for text wrapping and get the content of the text in previous line to change background color
                if (charSequence.toString() != "") {
                    val lastChar = charSequence.toString().substring(charSequence.length - 1)
                    if (lastChar == " ") {
                        // Remove trailing with white space character
                        // Get charSequence and add to adapter
                        tagRecyclerAdapter!!.addItem(charSequence.toString().substring(0, charSequence.length - 1))
                        interests!!.setText("")
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onRequestDeleted() {

    }

    override fun onNetworkAdded() {

    }

    override fun onError(throwable: Throwable) {

    }

    override fun onCircleRemoved() {

    }

    override fun onCircleAdded() {

    }

    override fun onRequestSent() {

    }

    override fun onMentoringRequestSent() {

    }

    override fun onAddCircleRequestValidated() {

    }

    override fun onAddNetworkRequestValidated() {

    }

    override fun onAvailableRequestSent() {

    }

    override fun showProgress() {

    }

    override fun dismissProgress() {

    }

    override fun onNetworkRemoved() {

    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, InterestActivity::class.java)
        }
    }
}
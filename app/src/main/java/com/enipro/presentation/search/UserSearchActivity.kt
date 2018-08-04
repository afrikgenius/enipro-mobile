package com.enipro.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.enipro.R
import com.enipro.data.remote.model.User
import com.enipro.injection.Injection
import com.enipro.model.LocalCallback
import com.enipro.presentation.profile.ProfileActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_search.*
import kotlinx.android.synthetic.main.search_toolbar.*

class UserSearchActivity : AppCompatActivity(), SearchContract.View {

    private var messagePresenter: SearchContract.Presenter? = null
    private var usersAdapter: SearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_search)

        val toolbar = findViewById<Toolbar>(R.id.message_search_toolbar)
        setSupportActionBar(toolbar)

        // On arrow back press, finish the activity.
        findViewById<View>(R.id.arrow_back).setOnClickListener { v -> finish() }

        // Message Presenter
        messagePresenter = SearchPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread())
        messagePresenter!!.attachView(this)

        onSearchContentChanged()
        search_clear!!.setOnClickListener { v -> search!!.setText("") }

        // Recycler view and adapter for news feed items
        search_recycler_view!!.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        search_recycler_view!!.layoutManager = linearLayoutManager
        usersAdapter = SearchAdapter(null, this, LocalCallback<User> { this.onSearchItemClicked(it) })
        search_recycler_view!!.adapter = usersAdapter
    }

    /**
     * Content change listener method that performs request to API when search content
     * is changed.
     */
    fun onSearchContentChanged() {
        search!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    search_clear!!.visibility = View.VISIBLE
                    // Send to presenter to search for user with new text
                    messagePresenter!!.search(charSequence.toString())
                } else {
                    search_clear!!.visibility = View.GONE
                    // Clear data in adapter
                    usersAdapter!!.clear()
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }

            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {

            }
        })
    }


    override fun showSearchResults(userList: List<User>) {
        search_recycler_view?.visibility = View.VISIBLE
        text_view_error_msg?.visibility = View.GONE
        usersAdapter!!.setItems(userList)
    }

    override fun showError(message: String) {
        text_view_error_msg!!.visibility = View.VISIBLE
        search_recycler_view!!.visibility = View.GONE
        text_view_error_msg!!.text = message
    }

    override fun showLoading() {
        search_progress_bar!!.visibility = View.VISIBLE
        search_progress_bar!!.startAnimation()
        search_recycler_view!!.visibility = View.GONE
        text_view_error_msg!!.visibility = View.GONE
    }

    override fun hideLoading() {
        search_progress_bar!!.visibility = View.GONE
        search_progress_bar!!.stopAnimation()
        search_recycler_view!!.visibility = View.VISIBLE
        text_view_error_msg!!.visibility = View.GONE
    }

    private fun onSearchItemClicked(user: User) {
        startActivity(ProfileActivity.newIntent(this, user))
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, UserSearchActivity::class.java)
        }
    }

}

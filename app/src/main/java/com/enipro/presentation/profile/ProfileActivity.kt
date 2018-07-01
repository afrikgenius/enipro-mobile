package com.enipro.presentation.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.User
import com.enipro.injection.Injection
import com.enipro.model.Constants
import com.enipro.model.LocalCallback
import com.enipro.model.Utility
import com.enipro.presentation.generic.EducationAdapter
import com.enipro.presentation.generic.ExperienceAdapter
import com.enipro.presentation.messages.MessageActivity
import com.enipro.presentation.profile.student_search.StudentSearch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile_edit.*
import org.parceler.Parcels

class ProfileActivity : AppCompatActivity(), ProfileContract.View {


    private var user: User? = null
    private var presenter: ProfileContract.Presenter? = null
    private var progressDialog: MaterialDialog? = null
    private var interactor: ProfileInteractor? = null

    // Only initialize these adapters if there is an education or experience data
    private var educationAdapter: EducationAdapter? = null
    private var experienceAdapter: ExperienceAdapter? = null

    companion object {

        /**
         * Returns a new intent to open an instance of this activity.
         */
        fun newIntent(context: Context, user: User): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(Constants.APPLICATION_USER, Parcels.wrap(user))
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(profile_toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }

        user = Parcels.unwrap(intent.getParcelableExtra(Constants.APPLICATION_USER))

        // TODO This should be done in a view model
        presenter = ProfilePresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this)
        presenter?.attachView(this)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        //TODO  Look for a proper way to represent this
        education_recyclerview.setHasFixedSize(true)
        education_recyclerview.addItemDecoration(Utility.DividerItemDecoration(this))
        education_recyclerview.layoutManager = linearLayoutManager

        val linearLayoutManager2 = LinearLayoutManager(this)
        linearLayoutManager2.orientation = LinearLayoutManager.VERTICAL
        experience_recyclerview.setHasFixedSize(true)
        experience_recyclerview.addItemDecoration(Utility.DividerItemDecoration(this))
        experience_recyclerview.layoutManager = linearLayoutManager2

        evaluateUser()
        populateViewItems()
        setClickListeners()
    }


    /**
     * Checks if logged in user profile is open or another users profile is open
     * by the logged in user and does some hiding of views.
     */
    private fun evaluateUser() {
        if (user?.id.equals(Application.getActiveUser().id)) {

            // Logged in user profile is open
            when (user?.userType?.toUpperCase(Application.getLocale())) {
                Constants.STUDENT -> {
                    action_layout.visibility = View.GONE
                }
                Constants.PROFESSIONAL -> {
                    action_btn.text = Constants.AVAILABLE_FOR_MENTORING
                }
            }
        } else {

            // Another user profile is opened by logged in user
            edit_profile.visibility = View.GONE
            settings.visibility = View.GONE
            interest_post_card.visibility = View.GONE
            interactor = ProfileInteractor(user, presenter, LocalCallback<String> { this.onInteractorProcessCompleted(it) })
            interactor!!.process() // Process
        }
    }

    /**
     * Runs as a call back function after the interactor process has been completed.
     */
    private fun onInteractorProcessCompleted(connectionString: String) {
        action_btn.text = connectionString
        when (connectionString) {
            Constants.CONNECTION_IN_CIRCLE, Constants.CONNECTION_IN_NETWORK, Constants.CONNECTION_MENTORING -> {
                //  Change background of action to something else
                message_user.visibility = View.VISIBLE
                action_btn.background = resources.getDrawable(R.drawable.oval_button)
                action_btn.setTextColor(resources.getColor(R.color.colorPrimary))
            }
            Constants.CONNECTION_REQUEST_MENTORING -> {
            }
            Constants.NO_CONNECTION -> action_btn.visibility = View.GONE
        }
    }

    private fun populateViewItems() {
        profile_name.text = user!!.name
        profile_headline.text = user!!.headline
        country.text = user!!.country
        user_type.text = user!!.userType
        Glide.with(this).load(user!!.avatar).apply(RequestOptions().placeholder(R.drawable.profile_image)).into(profile_imageview)
        Glide.with(this).load(user!!.avatar_cover).apply(RequestOptions().placeholder(R.drawable.bg_image)).into(cover_photo)

        // Education and Experience adapters should not be created if there is no
        //  education or experience in users profile
        // TODO Check if this works
        if (user!!.education.isNotEmpty()) {
            educationAdapter = EducationAdapter(user!!.education)
            education_recyclerview.adapter = educationAdapter
        } else
            education_recyclerview.visibility = View.GONE

        // Check for experiences and remove recycler view if there is no experience
        if (user!!.experiences.isNotEmpty()) {
            experienceAdapter = ExperienceAdapter(user!!.experiences)
            experience_recyclerview.adapter = experienceAdapter
        } else
            experience_recyclerview.visibility = View.GONE

        // Remove items not needed in activity based on user types
        when (user?.userType?.toUpperCase(Application.getLocale())) {
            Constants.STUDENT -> {
                bio_layout.visibility = View.GONE
                exp_card.visibility = View.GONE
            }
            Constants.COMPANY -> {
                edu_layout.visibility = View.GONE
                exp_card.visibility = View.GONE
            }
            Constants.SCHOOL -> {
                education_card.visibility = View.GONE
                bio_layout.visibility = View.GONE
                exp_card.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Application.profileEdited) {
            user = Application.getActiveUser()
            populateViewItems()
            Application.profileEdited = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return false
        }
    }

    /**
     * Sets click listeners for profile view items.
     */
    private fun setClickListeners() {
        bio_text.setOnClickListener { _ -> startActivity(BioActivity.newIntent(this)) }
        edit_profile.setOnClickListener { _ -> startActivity(ProfileEditActivity.newIntent(this)) }
        interests_layout.setOnClickListener { _ -> startActivity(InterestActivity.newIntent(this)) }
//        saved_layout.setOnClickListener { _ -> startActivity(ViewPostsActivity.newIntent(this)) }
        message_user.setOnClickListener { _ -> startActivity(MessageActivity.newIntent(this, user)) }

        // Action listeners
        action_btn.setOnClickListener {

            when (action_btn.text.toString()) {
                Constants.CONNECTION_ADD_CIRCLE -> {
                    presenter!!.requestAddCircle(Application.getActiveUser().id, user!!.id)
                }
                Constants.CONNECTION_IN_CIRCLE -> {
                    MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Remove " + user!!.name + " from circle")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive { _, _ ->
                                presenter!!.removeCircle(user!!.id)
                            }
                            .show()
                }
                Constants.CONNECTION_ADD_NETWORK -> {
                    presenter!!.requestAddNetwork(Application.getActiveUser().id, user!!.id)
                }
                Constants.CONNECTION_IN_NETWORK -> {
                    MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Remove " + user!!.name + " from network")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive { _, _ ->
                                presenter!!.removeNetwork(user!!.id)
                            }
                            .show()
                }
                Constants.CONNECTION_REQUEST_MENTORING -> {
                    presenter!!.requestMentoring(Application.getActiveUser().id, user!!.id)
                }
                Constants.CONNECTION_MENTORING -> {
                }
                Constants.AVAILABLE_FOR_MENTORING -> {
                    startActivity(StudentSearch.newIntent(this))
                }
                Constants.CONNECTION_PENDING -> {
                    MaterialDialog.Builder(this)
                            .title(R.string.remove)
                            .content("Withdraw Mentoring Request")
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .onPositive { _, _ ->
                                presenter!!.deleteRequest() // This removes the request from
                            }.show()
                }
            }
        }
    }

    override fun showProgress() {
        progressDialog = MaterialDialog.Builder(this).content(R.string.wait).progress(true, 0).show()
    }

    private fun updateProfileView(btntext: String, color: Int, drawable: Int, message_visibility: Int) {
        action_btn.text = btntext
        action_btn.setTextColor(resources.getColor(color))
        action_btn.background = resources.getDrawable(drawable)
        message_user.visibility = message_visibility
    }

    override fun onCircleAdded() {
        updateProfileView(Constants.CONNECTION_IN_CIRCLE, R.color.colorPrimary, R.drawable.oval_button, View.VISIBLE)
    }

    override fun onCircleRemoved() {
        updateProfileView(Constants.CONNECTION_ADD_CIRCLE, R.color.white, R.drawable.button_bg, View.GONE)
    }

    override fun onNetworkAdded() {
        updateProfileView(Constants.CONNECTION_IN_NETWORK, R.color.colorPrimary, R.drawable.oval_button, View.VISIBLE)
    }

    override fun onNetworkRemoved() {
        updateProfileView(Constants.CONNECTION_ADD_NETWORK, R.color.white, R.drawable.button_bg, View.GONE)
    }

    override fun onAvailableRequestSent() {
        updateProfileView(Constants.MENTORING_REQUEST_SENT, R.color.colorPrimary, R.drawable.oval_button, View.GONE)
    }

    override fun onMentoringRequestSent() {

        action_btn.text = Constants.CONNECTION_PENDING
        action_btn.setTextColor(resources.getColor(R.color.colorPrimary))
        action_btn.background = resources.getDrawable(R.drawable.oval_button)

        // Send a notification to profile user that a request for mentoring has been sent.
        val appUser = Application.getActiveUser()
        Utility.sendPushNotificationToReceiver(appUser.name, appUser.id, "wants to be mentored by you.",
                appUser.firebaseUID, appUser.firebaseToken, user!!.firebaseToken, Constants.MENTORING_REQUEST_ID)

    }

    override fun onAddCircleRequestValidated() {

        action_btn.text = Constants.CONNECTION_PENDING
        action_btn.setTextColor(resources.getColor(R.color.colorPrimary))
        action_btn.background = resources.getDrawable(R.drawable.oval_button)

        // Send a push notification to the user about the add to circle request
        val appUser = Application.getActiveUser()
        Utility.sendPushNotificationToReceiver(appUser.name, appUser.id, "wants to be in your circle",
                appUser.firebaseUID, appUser.firebaseToken, user!!.firebaseToken, Constants.CIRCLE_REQUEST)
    }

    override fun onAddNetworkRequestValidated() {
        action_btn.text = Constants.CONNECTION_PENDING
        action_btn.setTextColor(resources.getColor(R.color.colorPrimary))
        action_btn.background = resources.getDrawable(R.drawable.oval_button)

        // Send notification to the profile user
        val appUser = Application.getActiveUser()
        Utility.sendPushNotificationToReceiver(appUser.name, appUser.id, "wants to be in your network",
                appUser.firebaseUID, appUser.firebaseToken, user!!.firebaseToken, Constants.NETWORK_REQUEST)

    }

    override fun onRequestSent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestDeleted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(throwable: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dismissProgress() {
        progressDialog!!.dismiss()
        progressDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (progressDialog != null && progressDialog!!.isShowing)
            dismissProgress()
    }
}

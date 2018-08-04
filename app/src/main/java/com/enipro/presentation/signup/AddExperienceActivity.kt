package com.enipro.presentation.signup


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import com.enipro.R
import com.enipro.data.remote.model.Experience
import com.enipro.data.remote.model.User
import com.enipro.db.EniproDatabase
import com.enipro.injection.Injection
import com.enipro.model.Utility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_experience.*
import java.util.*

class AddExperienceActivity : AppCompatActivity(), SignupContract.View {

    private var presenter: SignupContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_experience)

        // Get user object passed from previous activity
        val user = intent.getParcelableExtra<User>(TAG)

        presenter = SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.getInstance(this), this)
        presenter?.attachView(this)

        continue_exp!!.setOnClickListener { _ ->
            val indust = industry!!.text.toString()
            val organsation = org!!.text.toString()
            val job_title = role!!.text.toString()
            val start_yr = start_year!!.text.toString()
            val end_yr: String
            if (end_year?.visibility == View.GONE)
                end_yr = "Present"
            else
                end_yr = end_year?.text.toString()

            val headline = "$job_title, $organsation"

            val experience = Experience(indust, organsation, job_title, start_yr, end_yr)
            val experiences = ArrayList<Experience>()
            experiences.add(experience)
            user.experiences = experiences
            user.headline = headline
            advanceProcess(user)
        }

        start_year!!.setOnClickListener { view -> Utility.showDatePickerDialog(this, start_year) }
        end_year!!.setOnClickListener { view -> Utility.showDatePickerDialog(this, end_year) }
        checkBox!!.setOnClickListener { view ->
            if ((view as CheckBox).isChecked) {
                end_year!!.visibility = View.GONE
            } else
                end_year!!.visibility = View.VISIBLE
        }
    }

    override fun showProgress() {

    }

    override fun dismissProgress() {

    }

    override fun openApplication(user: User) {

    }

//    override fun getPresenter(): SignupContract.Presenter? {
//        return presenter
//    }

    override fun showMessage(message: Int) {}

    override fun showMessage(type: String, message: String) {}

    override fun showMessageDialog(title: Int, message: Int) {}

    override fun showMessageDialog(title: Int, message: String) {}

    override fun showMessageDialog(title: String, message: Int) {}

    override fun showMessageDialog(title: String, message: String) {}

    override fun setViewError(view: View, errorMessage: String) {}

    override fun getSpinnerData(spinner_name: String): String? {
        return null
    }

    override fun advanceProcess(user: User) {
        val intent = AddInterestsActivity.newIntent(this)
        intent.putExtra(AddInterestsActivity.TAG, user)
        startActivity(intent)
    }

    companion object {

        val TAG = ".AddExpeienceActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, AddExperienceActivity::class.java)
        }
    }
}

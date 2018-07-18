package com.enipro.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.text.InputType
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.enipro.R
import com.enipro.data.remote.model.User
import com.enipro.injection.Injection
import com.enipro.model.*
import com.enipro.presentation.home.HomeActivity
import com.enipro.presentation.signup.SignUpActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import org.parceler.Parcels

class LoginActivity : FragmentActivity(), LoginContract.View {

    private var loginPresenter: LoginContract.Presenter? = null
    private var progressDialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold)

        // Extract data from edit text.
        val editTextIds = intArrayOf(R.id.emailAddressLogin, R.id.passwordLogin)
        val _extractor = EditTextDataExtractor(this, editTextIds)

        // Register a validation service for data validation.
        val validationService = ApplicationService.getInstance(ServiceType.ValidationService) as ValidationService

        // Initialise presenter and attach view.
        loginPresenter = LoginPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), validationService, this)
        loginPresenter?.attachView(this)

        // Attach view items to presenter
        loginPresenter?.attachViewItems(LoginContract.View.EMAIL, emailAddressLogin)
        loginPresenter?.attachViewItems(LoginContract.View.PASSWORD, passwordLogin)

        // Register the validation service.
        try {
            validationService.register(loginPresenter, findViewById(R.id.btnLoginInLogin),
                    ValidationService.LISTENER_VIEW_ON_CLICK, _extractor)
        } catch (e: Exception) {
            //            Log.e("ERROR", "Activity did not implement DataValidator");
        }

        val txtSignUp = findViewById<TextView>(R.id.clickable_sign_up_text)
        txtSignUp.setOnClickListener { _ ->
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.forgot_password).setOnClickListener { _ ->
            MaterialDialog.Builder(this)
                    .title(R.string.forgot_pass)
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .input(R.string.input_content, R.string.empty) { _, charSequence ->
                        // Call presenter to send message to API
                        loginPresenter!!.sendFPRequest(charSequence.toString())
                    }
                    .show()
        }

        emailAddressLogin!!.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val emailAddress = emailAddressLogin!!.text.toString()
                val emailValid = validationService.validateEmail(emailAddress)
                if (!emailValid || emailAddress.equals("", ignoreCase = true))
                    emailAddressLogin!!.error = "Please enter a valid email"
            }
        }
    }

    override fun showMessage(message: Int) {
        val snack = Snackbar.make(login_coordinator, message, Snackbar.LENGTH_LONG)
        (snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView)
                .setTextColor(resources.getColor(R.color.white))
        snack.show()
    }

    override fun showMessage(type: String, message: String) {
        when (type) {
            LoginContract.View.MESSAGE_SNACKBAR -> {
                val snack = Snackbar.make(login_coordinator, message, Snackbar.LENGTH_LONG)
                (snack.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView)
                        .setTextColor(resources.getColor(R.color.white))
                snack.show()
            }
            LoginContract.View.MESSAGE_DIALOG ->
                MaterialDialog.Builder(this)
                        .title(R.string.incorrect)
                        .content(message)
                        .positiveText(R.string.ok)
                        .show()
        }
    }

    override fun openApplication(user: User) {
        val intent = HomeActivity.newIntent(this, user)
//        intent.putExtra(Constants.APPLICATION_USER, Parcels.wrap(user))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showProgress() {
        progressDialog = MaterialDialog.Builder(this)
                .content(R.string.wait)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show()
    }

    override fun dismissProgress() {
        progressDialog?.dismiss()
    }

    override fun setViewError(view: View, errorMessage: String) {
        if (view === emailAddressLogin || view === passwordLogin)
            view.error = errorMessage
    }

    override fun onPause() {
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginPresenter!!.detachView()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
package com.enipro.presentation.requests

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.Request
import com.enipro.data.remote.model.SessionSchedule
import com.enipro.data.remote.model.User
import com.enipro.events.NotificationEvent
import com.enipro.firebase.FirebaseNotificationBuilder
import com.enipro.injection.Injection
import com.enipro.model.Constants
import com.enipro.model.Utility
import com.enipro.presentation.messages.MessageActivity
import com.enipro.presentation.requests.session_schedule.SessionScheduleActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_requests.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RequestsFragment : Fragment(), RequestsContract.View, RequestInteractor {


    private var presenter: RequestsContract.Presenter? = null
    private var adapter: RequestsRecyclerAdapter? = null
    private var requestPackage: RequestPackage? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = RequestsPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), activity)
        presenter!!.attachView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swiperefresh_req.setOnRefreshListener {
            presenter!!.getRequests(Application.getActiveUser().id)
        }
        req_recycler_view.setHasFixedSize(true)
        req_recycler_view.addItemDecoration(Utility.DividerItemDecoration(activity))

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        req_recycler_view.layoutManager = linearLayoutManager

        adapter = RequestsRecyclerAdapter(activity, null, presenter, coordinatorLayout, this)
        req_recycler_view.adapter = adapter

        // TODO Multiple request will be retrieved and sorted based on types and displayed in recycler views for each type of request.
        presenter!!.getRequests(Application.getActiveUser().id)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Executes when result is returned.
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.SCHEDULE_DATA_REQUESTCODE -> {

                    // Get session schedule object from intent
                    val schedule = data!!.getParcelableExtra<Parcelable>(Constants.SESSION_SCHEDULE_DATA) as SessionSchedule?
                    val request = requestPackage!!.request
                    request!!.schedule = schedule
                    // Perform acceptance of request
                    presenter!!.acceptRequest(request)
                }
            }
        }
    }

    override fun processAcceptance(request: Request?, user: User?, position: Int) {
        requestPackage = RequestPackage(request, user, position)

        //  Open activity to show session schedule and session timings.
        val scheduleIntent = SessionScheduleActivity.newIntent(activity)
        startActivityForResult(scheduleIntent, Constants.SCHEDULE_DATA_REQUESTCODE)
    }

    override fun processDecline(request: Request?, position: Int) {
        presenter!!.declineRequest(request)
        adapter!!.removeItem(position)
        Utility.showSnackBar(coordinatorLayout, "Request Rejected", true)
    }

    @Subscribe
    fun onNotificationEvent(notificationEvent: NotificationEvent?) {
        // Should only be called in a case where there is no data in the adapter
//        if (messagesAdapter == null || messagesAdapter.getItemCount() == 0)
//            presenter.getMessageFromFirebaseUser(applicationUser.getFirebaseUID(), notificationEvent.getUid());


    }

    override fun onRequestsCollected(requests: MutableList<Request>?) {
        if (swiperefresh_req.isRefreshing)
            swiperefresh_req.isRefreshing = false
        adapter!!.setItems(requests)
    }

    override fun onRequestsError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestAccepted() {
        adapter!!.removeItem(requestPackage!!.position)
        val snackbar = Snackbar.make(coordinatorLayout, R.string.request_accepted, Snackbar.LENGTH_LONG)
                .setAction(Constants.MESSAGE) {
                    val intent = MessageActivity.newIntent(activity!!.applicationContext, requestPackage!!.user)
                    startActivity(intent)
                }
        val tv = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        snackbar.show()

        // Send a notification to sender that the request has been accepted.
        FirebaseNotificationBuilder.initialize()
                .title(Application.getActiveUser().name)
                .message("accepted your mentoring request.")
                .username(Application.getActiveUser().id)
                .uid(Application.getActiveUser().firebaseUID)
                .firebaseToken(Application.getActiveUser().firebaseToken)
                .receiverFirebaseToken(requestPackage!!.user!!.firebaseToken)
                .uniqueIdentifier(Constants.MENTORING_REQUEST_REC)
                .send()
    }

    override fun onRequestDeclined() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null)
            presenter!!.detachView()
    }

    /**
     * A request bundle/ package that stores a request with its position in the list
     * of requests and represents a request that is either being accepted or being declined
     * at a point in time.
     */
    internal inner class RequestPackage(var request: Request? // The request that is being operated on.
                                        , var user: User? // the user that sent the request
                                        , var position: Int // the position of the request in the list of requests.
    )

}
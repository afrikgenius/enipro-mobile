package com.enipro.presentation.messages

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.enipro.R
import com.enipro.data.remote.model.Dialog
import com.enipro.data.remote.model.Message
import com.enipro.data.remote.model.User
import com.enipro.injection.Injection
import com.enipro.model.Constants
import com.enipro.model.Utility
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_messages.*
import org.parceler.Parcels

class MessagesFragment : Fragment(), MessagesContract.View, DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog> {

    private var presenter: MessagesContract.Presenter? = null
    private var imageLoader: ImageLoader? = null
    private var dialogsAdapter: DialogsListAdapter<Dialog>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_message_fab.setOnClickListener {
            startActivityForResult(MessagesSearch.newIntent(activity), Constants.MESSAGE_SEARCH)
        }

        // TODO This part should be done in a view model
        presenter = MessagesPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), activity)
        presenter!!.attachView(this)

        // TODO This part should be done in a view model
        presenter!!.loadPreviousMessages()

        imageLoader = ImageLoader { imageView: ImageView?, url: String? ->
            Glide.with(context as Context).load(url).into(imageView as ImageView)
        }


        dialogsAdapter = DialogsListAdapter(R.layout.message_item, imageLoader)

        dialogsAdapter!!.setOnDialogClickListener(this)
        dialogsAdapter!!.setOnDialogClickListener(this)
        dialogsList.setAdapter(dialogsAdapter)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.MESSAGE_SEARCH) {
            if (resultCode == RESULT_OK) {
                val user = data!!.getParcelableExtra<Parcelable>(Constants.MESSAGE_SEARCH_RETURN_KEY)
                startActivity(MessageActivity.newIntent(activity!!.applicationContext, Parcels.unwrap(user)))

            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter!!.loadPreviousMessages()
    }

    override fun onDialogClick(dialog: Dialog?) {
        startActivity(MessageActivity.newIntent(activity!!.applicationContext, dialog!!.users[0]))
    }

    override fun onDialogLongClick(dialog: Dialog?) {
        Utility.showToast(activity, dialog!!.dialogName, false)
    }

    override fun onMessagesLoaded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageDataReceived(user: User, message: Message?) {
        val exists = dialogsAdapter!!.updateDialogWithMessage(user.id, message)
        if (exists) {
            val users = ArrayList<User>()
            users.add(user)
            val dialog = Dialog(user.id, user.name, user.avatar as String, users, message, 0)
            dialogsAdapter!!.addItem(0, dialog)
        }
    }

    override fun onNullUsers() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
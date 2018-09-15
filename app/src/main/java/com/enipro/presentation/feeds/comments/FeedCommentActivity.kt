package com.enipro.presentation.feeds.comments

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.Feed
import com.enipro.data.remote.model.FeedComment
import com.enipro.data.remote.model.User
import com.enipro.databinding.ActivityFeedCommentBinding
import com.enipro.injection.Injection
import com.enipro.model.Enipro
import com.enipro.model.Utility
import com.enipro.presentation.feeds.FeedContract
import com.enipro.presentation.feeds.FeedPresenter
import com.enipro.presentation.utility.NetworkState
import com.enipro.viewmodels.FeedCommentViewModel
import com.enipro.viewmodels.FeedCommentViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed_comment.*
import kotlinx.android.synthetic.main.comment_footer.*
import java.io.FileNotFoundException

class FeedCommentActivity : AppCompatActivity(), FeedContract.CommentView {


    private var feed: Feed? = null
    private var commentPresenter: FeedContract.CommentPresenter? = null
    private var adapter: CommentsRecyclerAdapter? = null
    private var imageUploadBitmap: Bitmap? = null
    private var mStorageRef: StorageReference? = null
    private var binding: ActivityFeedCommentBinding? = null

    private var viewModel: FeedCommentViewModel? = null

    private var feedPresenter: FeedContract.Presenter? = null

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, FeedCommentActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setHomeButtonEnabled(true)

        feed = intent.extras.getParcelable(FeedContract.Presenter.FEED)

        viewModel = ViewModelProviders.of(this, FeedCommentViewModelFactory(feed?._id?.oid)).get(FeedCommentViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_comment)
        binding?.feed = feed

        feedPresenter = FeedPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this) //, binding)

        commentPresenter = FeedCommentPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this)
        commentPresenter!!.attachView(this)

        binding?.presenter = feedPresenter as FeedPresenter

        // If there is an image
        if (feed?.content?.image != null) {
            media_layout.visibility = View.VISIBLE
            post_comment_image.visibility = View.VISIBLE
            Glide.with(this).load(feed?.content?.image).apply(RequestOptions().placeholder(R.drawable.bg_image)).into(post_comment_image)
        } else if (feed?.content?.video != null) {
            media_layout.visibility = View.VISIBLE
            video_comment_layout.visibility = View.VISIBLE

            // TODO User ExoPlayer here to start video play
        }

        binding?.feedsCommentRecyclerView?.setHasFixedSize(true)
        binding?.feedsCommentRecyclerView?.addItemDecoration(Utility.DividerItemDecoration(this))
        val layoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding?.feedsCommentRecyclerView?.layoutManager = layoutManager
        adapter = CommentsRecyclerAdapter(this)

        viewModel?.liveData?.observe(this, Observer<PagedList<FeedComment>> {
            adapter?.submitList(it)
        })

        viewModel?.networkState?.observe(this, Observer<NetworkState> {
            Log.d(Enipro.APPLICATION, "State:" + it?.status)
            when {
                it?.status == NetworkState.Status.RUNNING -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                }
                it?.status == NetworkState.Status.FAILED -> {
                    showErrorMessage()
                }
                it?.status == NetworkState.Status.SUCCESS -> {
                    if (adapter?.itemCount == 0)
                        binding?.progressBar?.visibility = View.GONE
                }
            }
        })
        binding?.feedsCommentRecyclerView?.adapter = adapter

        // Get intent that started the activity and get extra for keyboard event.
        if (intent.getIntExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, 0x01) == FeedContract.Presenter.ADD_COMMENT) {
            // Request focus for edit text and Open the keyboard
            comment_editText.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }

        setActionButtonListeners()
        setOnCommentContentChange()


        setImageUploadListener()

        // When the send button is clicked. The comment is added to the adapter and sent to the API to be persisted and also in Local Storage.
        send_comment.setOnClickListener { _ ->
            // Check internet connection before sending post.
            if (!Utility.isInternetConnected(this))
                Utility.showSnackBar(coordinator, "Comment post failed. No Internet Connection", true)
            else {
                val comment = FeedComment()
                comment.comment = comment_editText.text.toString()
//                comment.user = Application.getActiveUser().id

                mStorageRef = FirebaseStorage.getInstance().reference
                // navigate to profile_avatar reference
                mStorageRef = mStorageRef?.child("images/feeds/comments/" + comment.imageName + ".jpg")
                // Upload comment image if exists to firebase and get download URL
                if (imageUploadBitmap != null) {

                    // Sending the avatar to firebase storage is done on a separate thread and persisting user with
                    // a call to the API should be done when the avatar is successfully sent to firebase storage.
                    commentPresenter?.uploadCommentImageFirebase(mStorageRef, imageUploadBitmap) { downloadURL ->
                        comment.comment_image = downloadURL
                        // Send data for persistence.
                        commentPresenter?.persistComment(comment, feed?.userObject?.id, feed?._id?.oid)
                    }

                    // Delete image
                    image_upload.setImageDrawable(null)
                    image_upload.visibility = View.GONE
                    imageUploadBitmap = null
                } else {
                    // Just persist the comment
                    commentPresenter?.persistComment(comment, feed?.userObject?.id, feed?._id?.oid)
                }
            }
            // Collapse keyboard and empty edit text before sending to API
            comment_editText.setText("")
            Utility.collapseKeyboard(this)

            // TODO  Display comment in a dull view and wait for it to load properly then remove dull view
            // TODO and on Error make comment_error text view visible
        }

    }

    private fun setImageUploadListener() {
        image_upload.setOnClickListener {
            MaterialDialog.Builder(this)
                    .items(R.array.photo_edit_items)
                    .itemsCallback { _, _, which, _ ->
                        if (which == 0) {
                            // Edit Title of image
                        } else {
                            // Delete image
                            image_upload.setImageDrawable(null)
                            image_upload.visibility = View.GONE
                        }
                    }
                    .show()
        }
    }

    private fun setActionButtonListeners() {
        camera.setOnClickListener {
            val check = Utility.checkPermission(this, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, resources.getString(R.string.camera_rationale))
            if (check) {
                // Launch camera
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE)//zero can be replaced with any action codes
            }
        }
        gallery.setOnClickListener {
            val check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, resources.getString(R.string.external_storage_rationale))
            if (check) {
                // Open gallery
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE)
            }
        }
        post_comment_button.setOnClickListener { _ ->
            comment_editText.requestFocus()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        } // Activate comment edit text
        //        post_share_button.setOnClickListener(v -> Utility.shareContent());
    }

    private fun setOnCommentContentChange() {
        comment_editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.isNotEmpty()) {
                    send_comment.setTextColor(resources.getColor(R.color.colorPrimary))
                    send_comment.isClickable = true
                } else {
                    send_comment.setTextColor(resources.getColor(R.color.material_grey_400_))
                    send_comment.isClickable = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Launch Camera to take a picture
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE)//zero can be replaced with any action codes
            } else {
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Open gallery
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE)
            } else {
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Utility.CAMERA_REQUEST_CODE // Camera selected
            -> if (resultCode == Activity.RESULT_OK) {
                val extras = data.extras
                val imageBitmap = extras!!.get("data") as Bitmap

                // Store a reference to original bitmap to send to Firebase
                imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false)
                image_upload.visibility = View.VISIBLE
                image_upload.setImageBitmap(Utility.editBitmapForEditText(imageBitmap, this, 10, Utility.PHOTO_SCALE_SIZE).bitmap)
            }
            Utility.GALLERY_REQUEST_CODE // Photo picked from gallery
            -> if (resultCode == Activity.RESULT_OK) {
                val imageBitmap: Bitmap?
                try {
                    imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data.data!!))
                    // Store a reference to original bitmap to send to Firebase
                    imageUploadBitmap = imageBitmap?.copy(Bitmap.Config.ARGB_8888, false)
                } catch (fnfe: FileNotFoundException) {
                    // TODO Handle error here.
                }

                image_upload.visibility = View.VISIBLE

//                Picasso.with(this).load(data.data)
//                        .resize(Utility.PHOTO_SCALE_SIZE, Utility.PHOTO_SCALE_SIZE)
//                        .centerCrop()
//                        .transform(RoundedCornersTransformation(10, 10))
//                        .into(image_upload)

                Glide.with(this)
                        .load(data.data)
                        .apply(RequestOptions().placeholder(R.drawable.bg_image))
                        .apply(RequestOptions().centerCrop())
                        .apply(RequestOptions().transform(RoundedCorners(10)))
                        .into(image_upload)
            }
        }// DO NOTHING
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle arrow click to return to previous activity
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showErrorNotification() {}

    override fun updateUI(commentItem: FeedComment, user: User) {
        //        Log.d(Application.TAG, "Comment sent to adapter");
//        commentAdapter?.addItem(commentItem)
        feeds_comment_recycler_view.smoothScrollToPosition(0)
        if (feed?.comments!!.isNotEmpty())
            comment_text.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        // Display snack bar showing
        val snackbar = Snackbar
                .make(coordinator, "Error Loading Comments.Please Try Again", Snackbar.LENGTH_LONG)
                .setAction("RETRY") { _ ->
                    binding?.progressBar?.visibility = View.VISIBLE
                    viewModel?.invalidateDataSource()
                }
        // Change text color of text
        snackbar.setActionTextColor(Color.RED)
        (snackbar.view.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView).setTextColor(Color.WHITE)
        snackbar.show()

        // Also hide progress bar
        binding?.progressBar?.visibility = View.GONE
    }

    override fun showLoading() {
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        binding?.progressBar?.visibility = View.GONE
    }
}

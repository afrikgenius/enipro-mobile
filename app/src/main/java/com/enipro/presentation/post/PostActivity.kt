package com.enipro.presentation.post

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enipro.Application
import com.enipro.R
import com.enipro.data.remote.model.Feed
import com.enipro.data.remote.model.FeedContent
import com.enipro.data.remote.model.PremiumDetails
import com.enipro.injection.Injection
import com.enipro.model.*
import com.enipro.presentation.generic.TagRecyclerAdapter
import com.google.firebase.crash.FirebaseCrash
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.content_post.*
import kotlinx.android.synthetic.main.footer_post.*
import kotlinx.android.synthetic.main.media_layout.*
import kotlinx.android.synthetic.main.post_toolbar.*
import org.parceler.Parcels
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class PostActivity : AppCompatActivity(), PostContract.View {


    private var presenter: PostPresenter? = null
    private var tagsAdapter: TagRecyclerAdapter? = null
    private var imageUploadBitmap: Bitmap? = null
    private var videoPath: String? = null
    private var documentFile: File? = null // File attached to the post object.
    private var premiumDetails: PremiumDetails? = null
    private var retriever: MediaMetadataRetriever? = null
    private var isPremium = false
    // Premium content details
    private var bankNumberEditText: EditText? = null
    private var amountEditText: EditText? = null
    private var bankName: String = String()

    companion object {

        val NONE = 0
        val IMAGE = 1
        val VIDEO = 2
        val DOCUMENT = 3

        /**
         * Returns a new intent to open an instance of this activity
         */
        fun newIntent(context: Context?): Intent {
            return Intent(context, PostActivity::class.java)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        setSupportActionBar(post_toolbar)

        presenter = PostPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(),
                ApplicationService.getInstance(ServiceType.ValidationService) as ValidationService?)
        presenter!!.attachView(this)


        tags_recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        tags_recyclerview.layoutManager = layoutManager

        tagsAdapter = TagRecyclerAdapter(null, 0)
        tags_recyclerview.adapter = tagsAdapter

        // TODO Check for intent with post/feed data to bind with views

        // Action when the activity close button is clicked
        post_close.setOnClickListener {
            if (post_content_edit.text.toString().isEmpty())
                finish()
            else {
                MaterialDialog.Builder(this)
                        .content(R.string.save_as_draft)
                        .positiveText(R.string.save)
                        .onPositive { _, _ ->
                            // TODO Save draft in the application for later edit
                        }
                        .negativeText(R.string.discard)
                        .onNegative { _, _ -> finish() }
                        .show()
            }
        }

        // Post button unfades and is clickable
        onFeedContentChange()
        onTagContentChange() // When content of tag edit text is changed.
        setActionButtonListeners()


        post_feed.setOnClickListener {
            val content = FeedContent()
            content.text = post_content_edit.text.toString()
            val feed = Feed()

            // There must be tags attached to a feed item which matches with interests of a user
            if (tagsAdapter!!.itemCount == 0) {
                Utility.showToast(this, R.string.enforce_tags, true)
                return@setOnClickListener
            }

            feed.tags = tagsAdapter!!.items
            // Check visibility of both image and video view and based on the visible view, upload media to firebase and return downloadURL
            // Only one media item can be uploaded for a post. Both views have visibility of gone and we are working on the view present.
            if (image_view.visibility == View.VISIBLE && media_view.visibility == View.VISIBLE) {
                // Image View is visible and check for null reference in imageUploadBitmap
                if (image_view != null && imageUploadBitmap != null) {
                    // Send all content to Feed Fragment and leave post activity immediately.
                    // Save temp bitmap in Application class.
                    content.mediaType = FeedContent.MediaType.IMAGE
                    Application.saveTempBitmap(imageUploadBitmap)
                    Application.setFeedMediaIdentifier(Application.BITMAP_IDENTIFIER_FEEDS)
                }
            } else if (video_view_layout.visibility == View.VISIBLE && media_view.visibility == View.VISIBLE) {
                // Video view is visible and check for null reference in video Upload file.
                // TODO Handle this appropriately.
                if (video_view != null) {
                    // Get video from video view
                    content.mediaType = FeedContent.MediaType.VIDEO
                    Application.saveTempVideoPath(videoPath)
                    Application.setFeedMediaIdentifier(Application.VIDEO_IDENTIFIER_FEEDS)
                }
            }

            // Check if a document exists in the post
            if (doc_layout.visibility == View.VISIBLE && documentFile != null) {
                Application.setDocumentPostFile(documentFile)
                content.isDocExists = true
            }

            // Check if there is a premium data to send
            if (premiumDetails != null)
                feed.premiumDetails = premiumDetails
            feed.content = content
            sendFeedItem(feed)

        }

        // Animate activity into the screen from the bottom
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold)
    }


    override fun onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (retriever != null)
            retriever!!.release()
    }

    private fun onFeedContentChange() {
        post_content_edit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    post_feed.setTextColor(resources.getColor(R.color.colorPrimary))
                    post_feed.isClickable = true
                } else {
                    post_feed.setTextColor(resources.getColor(R.color.material_grey_400_))
                    post_feed.isClickable = false
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun prepopulateView(feed: Feed) {
        post_content_edit.setText(feed.content?.text)

        // Check for images and videos
        if (feed.content?.image != null) {
            val imageBitmap: Bitmap
            try {
                imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(Uri.parse(feed.content?.image)))
                // Store a reference to original bitmap to send to Firebase
                imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false)
            } catch (fnfe: FileNotFoundException) {
                FirebaseCrash.log(fnfe.message)
            }
            Glide.with(this)
                    .load(feed.content?.video)
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().fitCenter())
                    .into(image_view)
            modifyMediaViews(true, IMAGE)
        } else if (feed.content?.video != null) {
            val videoURI = Uri.parse(feed.content?.video)
            // TODO Review to use exoplayer
//            video_view.setVideoURI(videoURI)
//            video_view.start()
            videoPath = Utility.getVideoPath(this, videoURI)
            modifyMediaViews(true, VIDEO)
        }

        // Check for premium details available
        if (feed.premiumDetails != null)
            premium.setImageDrawable(resources.getDrawable(R.drawable.ic_lock_active))

        // Check for tags
        tags_layout.visibility = View.VISIBLE
        tags_view.visibility = View.VISIBLE
        tagsAdapter!!.items = feed.tags

        // Check for a document
        if (feed.content?.doc != null) {
            doc_layout.visibility = View.VISIBLE
            // Display file name of doc
            val document = feed.content?.doc
            doc_name.text = document?.name
        }
    }

    override fun saveFeedAsDraft(feed: Feed?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setActionButtonListeners() {
        video.setOnClickListener {
            MaterialDialog.Builder(this).items(R.array.video_items)
                    .itemsCallback { _, _, which, _ ->
                        permissionCheck(which, FeedContent.MediaType.VIDEO)
                    }.show()
        }
        camera.setOnClickListener {
            MaterialDialog.Builder(this).items(R.array.photo_items)
                    .itemsCallback { _, _, which, _ -> permissionCheck(which, FeedContent.MediaType.IMAGE) }
                    .show()
        }

        tags.setOnClickListener { _ ->
            if (tags_layout.visibility == View.VISIBLE) {
                // Clear tags already added
                add_tags.setText("")
                tags_layout.visibility = View.GONE
                tags_view.visibility = View.GONE

                // Clear tags already attached.
                tagsAdapter!!.clear()
            } else {
                tags_layout.visibility = View.VISIBLE
                tags_view.visibility = View.VISIBLE
                add_tags.requestFocus()
            }
        }
        tags_clear.setOnClickListener { _ -> add_tags.setText("") }
        media_cancel.setOnClickListener { _ ->
            if (media_view.visibility == View.VISIBLE) {
                // Delete image from image_view or video view and set visibility to gone
                image_view.setImageDrawable(null)
//                video_view.setVideoPath("")
                media_view.visibility = View.GONE
                image_view.visibility = View.GONE
                video_view_layout.visibility = View.GONE
                modifyMediaViews(false, NONE) //  Make video and camera action button unblur
            }
        }

        // Set the post to contain a premium content and change the drawable of the icon
        premium.setOnClickListener {
            if (isPremium) {
                premium.setImageDrawable(resources.getDrawable(R.drawable.ic_lock_outline))
                premiumDetails = null
                isPremium = !isPremium
            } else {
                // Open material dialog and ask user to put in an amount for the content and also
                // provide account information where the proceeds go to.
                if (documentFile == null) {
                    Utility.showToast(this, "A document must be uploaded to be premium", true)
                } else {
                    val bankSpinner: MaterialSpinner
                    val materialDialog = MaterialDialog.Builder(this)
                            .negativeText(R.string.cancel)
                            .positiveText(R.string.ok)
                            .customView(R.layout.premium_content_view, false)
                            .onPositive { _, _ ->
                                // Validate all information provided to check for empty strings and nulls
                                val amount = amountEditText!!.text.toString()
                                val bankAccountNumber = bankNumberEditText!!.text.toString()

                                // TODO Check the account number details using NUBAN Account number checker.

                                if (amount == "" || bankAccountNumber == "") {
                                    Utility.showToast(this, "All fields are mandatory for a premium content.", true)
                                } else {
                                    premiumDetails = PremiumDetails(bankAccountNumber, bankName, Integer.valueOf(amount))
                                    premium.setImageDrawable(resources.getDrawable(R.drawable.ic_lock_active))
                                    isPremium = !isPremium
                                }
                            }
                            .onNegative { _, _ ->

                            }
                            .build()

                    val customView = materialDialog.customView
                    bankSpinner = customView!!.findViewById(R.id.bank_name)
                    bankSpinner.setItems(Arrays.asList(*resources.getStringArray(R.array.banks)))
                    bankSpinner.setOnItemSelectedListener { _, _, _, item -> bankName = item.toString() }
                    bankNumberEditText = customView.findViewById(R.id.bank_account_number)
                    amountEditText = customView.findViewById(R.id.amount)

                    materialDialog.show()
                }
            }
        }
        doc.setOnClickListener { permissionCheck(Constants.OPEN_EXTERNAL_STORAGE, FeedContent.MediaType.DOC) }
        doc_cancel.setOnClickListener {
            if (documentFile != null) {
                documentFile = null
                doc_layout.visibility = View.GONE
            }
        }
    }


    /**
     * Opens an intent to select a document with the mime types listed.
     */
    private fun selectAttachement() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        val mimetypes = arrayOf("application/pdf", "application/powerpoint", "application/msword", "text/plain", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        startActivityForResult(intent, Utility.DOC_REQUEST_CODE)
    }

    private fun modifyMediaViews(visible: Boolean, choice: Int) {
        if (visible) {
            video.setImageResource(R.drawable.ic_video_blur)
            video.isClickable = false
            camera.setImageResource(R.drawable.ic_camera_blur)
            camera.isClickable = false
            media_view.visibility = View.VISIBLE
            if (choice == IMAGE)
                image_view.visibility = View.VISIBLE
            else if (choice == VIDEO) video_view_layout.visibility = View.VISIBLE
        } else {
            video.setImageResource(R.drawable.ic_video)
            video.isClickable = true
            camera.setImageResource(R.drawable.ic_camera)
            camera.isClickable = true
        }
    }

    private fun onTagContentChange() {
        add_tags.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(charSequence: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence, start: Int, before: Int, count: Int) {
                if (charSequence.isNotEmpty()) {
                    tags_clear.visibility = View.VISIBLE
                } else {
                    tags_clear.visibility = View.GONE
                }

                // Check for text wrapping and get the content of the text in previous line to change background color
                if (charSequence.toString() != "") {
                    val lastChar = charSequence.toString().substring(charSequence.length - 1)
                    if (lastChar == " ") {
                        // Remove trailing with white space character
                        // Get charSequence and add to adapter
                        tagsAdapter!!.addItem(charSequence.toString().substring(0, charSequence.length - 1))
                        add_tags.setText("")
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }


    private fun permissionCheck(which: Int, mediaType: Int) {
        if (which == 0) {
            // Check camera permission and request permission if not granted.
            if (Utility.checkPermission(this, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, resources.getString(R.string.camera_rationale))) {
                when (mediaType) {
                    FeedContent.MediaType.IMAGE -> {
                        // Launch camera
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE)//zero can be replaced with any action codes
                    }
                    FeedContent.MediaType.VIDEO -> {
                        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        startActivityForResult(intent, Utility.VIDEO_CAPTURE_REQUEST_CODE)
                    }
                }
            }
        } else {
            // Open gallery
            if (Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + mediaType, resources.getString(R.string.external_storage_rationale))) {
                when (mediaType) {
                    FeedContent.MediaType.IMAGE -> {
                        // Open gallery
                        val pickPhoto = Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE)
                    }
                    FeedContent.MediaType.VIDEO -> {
                        // Open Gallery to choose a video
                        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(i, Utility.VIDEO_GALLERY_REQUEST_CODE)
                    }
                    FeedContent.MediaType.DOC -> selectAttachement()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(Application.TAG, "Camera permission has now been granted.")
                // Launch Camera to take a picture
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE)//zero can be replaced with any action codes
            } else {
//                Log.i(Application.TAG, "Camera permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.IMAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(Application.TAG, "External Storage permission has now been granted")
                // Open gallery
                val pickPhoto = Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE)
            } else {
//                Log.i(Application.TAG, "External storage permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.DOC) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(Application.TAG, "External Storage permission has now been granted")
                selectAttachement()
            } else {
//                Log.i(Application.TAG, "External storage permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.VIDEO) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(Application.TAG, "External Storage permission has now been granted")
                // Open Gallery to choose a video
                val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, Utility.VIDEO_GALLERY_REQUEST_CODE)
            } else {
//                Log.i(Application.TAG, "External storage permission was not granted ")
                // Show snackbar.
                Snackbar.make(window.decorView.rootView, R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Utility.CAMERA_REQUEST_CODE -> {
                    val extras = data!!.extras
                    val imageBitmap = extras!!.get("data") as Bitmap

                    // Store a reference to original bitmap to send to Firebase
                    imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false)
                    modifyMediaViews(true, IMAGE)
                    image_view.setImageBitmap(imageBitmap)
                }
                Utility.GALLERY_REQUEST_CODE -> {
                    val imageBitmap: Bitmap
                    try {
                        imageBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(data!!.data))
                        // Store a reference to original bitmap to send to Firebase
                        imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false)
                    } catch (fnfe: FileNotFoundException) {
                        FirebaseCrash.log(fnfe.message)
                    }

                    modifyMediaViews(true, IMAGE)
                    Glide.with(this)
                            .load(data?.data)
                            .apply(RequestOptions().centerCrop())
                            .apply(RequestOptions().fitCenter())
                            .into(image_view)
                }
                Utility.VIDEO_GALLERY_REQUEST_CODE -> {
                    if (lengthVideo(data!!.data) > Constants.VIDEO_LIMIT_MILLIS)
                        Utility.showToast(this, R.string.video_limit, true)
                    else {
                        // TODO Review to use exo player
//                        video_view.setVideoURI(data.data)
//                        video_view.start()
                        videoPath = Utility.getVideoPath(this, data.data)
                        modifyMediaViews(true, VIDEO)
                    }
                }
                Utility.VIDEO_CAPTURE_REQUEST_CODE -> {
                    if (lengthVideo(data!!.data) > Constants.VIDEO_LIMIT_MILLIS)
                        Utility.showToast(this, R.string.video_limit, true)
                    else {
                        // TODO Review to use exo player
//                        video_view.setVideoURI(data.data)
//                        video_view.start()
                        videoPath = Utility.getVideoPath(this, data.getData())
                        modifyMediaViews(true, VIDEO)
                    }
                }
                Utility.DOC_REQUEST_CODE -> {
                    val file = File(Utility.getPath(this, data!!.data))
                    if (file.length() > Constants.DOC_LIMIT_BYTES)
                        Utility.showToast(this, R.string.doc_limit, true)
                    else {
                        documentFile = file
                        doc_layout.visibility = View.VISIBLE
                        doc_name.text = file.name
                    }
                }
            }
        }
    }

    override fun sendFeedItem(feed: Feed) {
        // Send feed item back to home activity into feed fragment to be added.
        val resultIntent = Intent()
        resultIntent.putExtra(PostContract.Presenter.ACTIVITY_RETURN_KEY, Parcels.wrap(feed))
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun showPostError(errorMessage: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun lengthVideo(videoURI: Uri?): Long {
        retriever = MediaMetadataRetriever()
        //use one of overloaded setDataSource() functions to set your data source
        retriever!!.setDataSource(this, Uri.fromFile(File(Utility.getVideoPath(this, videoURI)!!)))
        val time = retriever!!.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return java.lang.Long.parseLong(time)
    }
}
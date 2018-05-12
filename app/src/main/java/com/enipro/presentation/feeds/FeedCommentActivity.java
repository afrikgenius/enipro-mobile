package com.enipro.presentation.feeds;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devspark.robototextview.widget.RobotoTextView;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedComment;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.DateTimeStringProcessor;
import com.enipro.model.Utility;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalVideoView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class FeedCommentActivity extends AppCompatActivity implements FeedContract.CommentView {

    private Feed feedData;
    private User user;

    private CommentsRecyclerAdapter commentAdapter;
    private RecyclerView mRecyclerView;
    private FeedContract.CommentPresenter presenter;
    StorageReference mStorageRef;
    private CircularProgressView progressBar;


    private Bitmap imageUploadBitmap = null;
    private List<FeedComment> commentList = new ArrayList<>();


    @BindView(R.id.comment_editText)
    EditText commentEditText;
    @BindView(R.id.user_post_image)
    CircleImageView user_image;
    @BindView(R.id.user_post_name)
    RobotoTextView user_name;
    @BindView(R.id.user_post_headline)
    RobotoTextView user_headline;
    @BindView(R.id.user_post_date)
    RobotoTextView post_date;
    @BindView(R.id.content)
    RobotoTextView feed_content;
    @BindView(R.id.send_comment)
    Button sendComment;
    @BindView(R.id.camera)
    ImageButton camera;
    @BindView(R.id.gallery)
    ImageButton gallery;
//    @BindView(R.id.at_reference)
//    ImageButton _reference;
    @BindView(R.id.image_upload)
    ImageView image_upload;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.comment_text)
    RobotoTextView comment_text;
    @BindView(R.id.post_like_button)
    ImageButton post_like_button;
    @BindView(R.id.post_comment_button)
    ImageButton post_comment_button;
    @BindView(R.id.post_share_button)
    ImageButton post_share_button;
    @BindView(R.id.post_save_button)
    ImageButton post_save_button;
    @BindView(R.id.post_comment_responses)
    RobotoTextView post_comment_responses;
    @BindView(R.id.post_likes_count)
    RobotoTextView post_likes_count;
    @BindView(R.id.media_layout)
    View media_layout;
    @BindView(R.id.post_image)
    ImageView post_image;
    @BindView(R.id.post_video)
    UniversalVideoView post_video;
    @BindView(R.id.video_layout)
    View video_layout;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, FeedCommentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comment);
        ButterKnife.bind(this); // Bind butterknife to this activity.

        Toolbar toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        presenter = new FeedCommentPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);

        // Collect feed data from feed fragment.
        feedData = getIntent().getExtras().getParcelable(FeedContract.Presenter.FEED);
        post_date.setText(DateTimeStringProcessor.process(feedData.getUpdated_at().getUtilDate()));
        feed_content.setText(feedData.getContent().getText());

        if (feedData.getContent().getImage() != null) {
            // Image exists in the
            media_layout.setVisibility(View.VISIBLE);
            post_image.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions().placeholder(R.drawable.bg_image);
            Glide.with(this).load(feedData.getContent().getImage()).apply(options).into(post_image);
        } else if (feedData.getContent().getVideo() != null) {
            media_layout.setVisibility(View.VISIBLE);
            video_layout.setVisibility(View.VISIBLE);
            post_video.setVideoURI(Uri.parse(feedData.getContent().getVideo()));
            post_video.setOnPreparedListener(mediaPlayer -> {
                // Start the media player
                mediaPlayer.start();
            });
        }

        progressBar = findViewById(R.id.progress_bar);
        // Recycler view and adapter for news feed items
        mRecyclerView = findViewById(R.id.feeds_comment_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new Utility.DividerItemDecoration(this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        commentAdapter = new CommentsRecyclerAdapter(null, this, presenter);
        mRecyclerView.setAdapter(commentAdapter);

        presenter.getUser(feedData.getUser(), user_data -> {
            runOnUiThread(() -> {
                user = user_data;
                user_name.setText(user.getName());
                user_headline.setText(user.getHeadline());
                Picasso.with(this).load(user.getAvatar()).into(user_image); // Load user image

                if (feedData.getComments().size() != 0)
                    comment_text.setVisibility(View.VISIBLE);
                // Load comments
                commentAdapter.setCommentItems(feedData.getComments());
            });
        });

        // Get intent that started the activity and get extra for keyboard event.
        if (getIntent().getIntExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, 0x01) == FeedContract.Presenter.ADD_COMMENT) {
            // Request focus for edit text and Open the keyboard
            commentEditText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        // Set on click listeners for action buttons which includes (camera, gallery and reference).
        setActionButtonListeners();
        setOnCommentContentChange(); // When content of the comment edit text changes.

        // Listener for the image upload view that opens a material dialog when clicked.
        setImageUploadListener();

        // When the send button is clicked. The comment is added to the adapter and sent to the API to be persisted and also in Local Storage.
        sendComment.setOnClickListener(v -> {
            // Check internet connection before sending post.
            if (!Utility.isInternetConnected(this))
                Utility.showSnackBar(coordinatorLayout, "Comment post failed. No Internet Connection", true);
            else {
                FeedComment comment = new FeedComment();
                comment.setComment(commentEditText.getText().toString());
                comment.setUser(Application.getActiveUser().get_id().get_$oid());

                mStorageRef = FirebaseStorage.getInstance().getReference();
                // navigate to profile_avatar reference
                mStorageRef = mStorageRef.child("images/feeds/comments/" + comment.getImageName() + ".jpg");
                // Upload comment image if exists to firebase and get download URL
                if (imageUploadBitmap != null) {

                    // Sending the avatar to firebase storage is done on a separate thread and persisting user with
                    // a call to the API should be done when the avatar is successfully sent to firebase storage.
                    presenter.uploadCommentImageFirebase(mStorageRef, imageUploadBitmap, (downloadURL) -> {
                        comment.setComment_image(downloadURL);
                        // Send data for persistence.
                        presenter.persistComment(comment, user.get_id().get_$oid(), feedData.get_id().get_$oid());
                    });

                    // Delete image
                    image_upload.setImageDrawable(null);
                    image_upload.setVisibility(View.GONE);
                    imageUploadBitmap = null;
                } else {
                    // Just persist the comment
                    presenter.persistComment(comment, user.get_id().get_$oid(), feedData.get_id().get_$oid());
                }
            }
            // Collapse keyboard and empty edit text before sending to API
            commentEditText.setText("");
            Utility.collapseKeyboard(this);

            // TODO  Display comment in a dull view and wait for it to load properly then remove dull view
            // TODO and on Error make comment_error text view visible
        });
    }

    /**
     * On Click listener that gets activated to open a material dialog to delete or edit the name
     * of the image to be uploaded when clicked.
     */
    void setImageUploadListener() {
        image_upload.setOnClickListener(v -> {
            new MaterialDialog.Builder(this)
                    .items(R.array.photo_edit_items)
                    .itemsCallback((dialog, view, which, text) -> {
                        if (which == 0) {
                            // Edit Title of image
                        } else {
                            // Delete image
                            image_upload.setImageDrawable(null);
                            image_upload.setVisibility(View.GONE);
                        }
                    })
                    .show();
        });
    }


    /**
     * Click listener for comment action button at the top of comment box and comment buttons (like, comment, share and save)
     */
    void setActionButtonListeners() {

        camera.setOnClickListener(v -> launchCamera()); // Camera button
        gallery.setOnClickListener(v -> launchGallery()); // Gallery button
        post_comment_button.setOnClickListener(v -> {
            commentEditText.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }); // Activate comment edit text
//        post_share_button.setOnClickListener(v -> Utility.shareContent());
        // TODO @ Reference button
    }

    void setOnCommentContentChange() {
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 0) {
                    sendComment.setTextColor(getResources().getColor(R.color.colorPrimary));
                    sendComment.setClickable(true);
                } else {
                    sendComment.setTextColor(getResources().getColor(R.color.material_grey_400_));
                    sendComment.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "Camera permission has now been granted.");
                // Launch Camera to take a picture
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
            } else {
                Log.i(Application.TAG, "Camera permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "External Storage permission has now been granted");
                // Open gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
            } else {
                Log.i(Application.TAG, "External storage permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utility.CAMERA_REQUEST_CODE: // Camera selected
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");

                    // Store a reference to original bitmap to send to Firebase
                    imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    image_upload.setVisibility(View.VISIBLE);
                    image_upload.setImageBitmap(Utility.editBitmapForEditText(imageBitmap, this, 10, Utility.PHOTO_SCALE_SIZE).getBitmap());
                }
                break;
            case Utility.GALLERY_REQUEST_CODE: // Photo picked from gallery
                if (resultCode == RESULT_OK) {
                    Bitmap imageBitmap = null;
                    try {
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        // Store a reference to original bitmap to send to Firebase
                        imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    } catch (FileNotFoundException fnfe) {
                        // TODO Handle error here.
                    }
                    image_upload.setVisibility(View.VISIBLE);
                    Picasso.with(this).load(data.getData())
                            .resize(Utility.PHOTO_SCALE_SIZE, Utility.PHOTO_SCALE_SIZE)
                            .centerCrop()
                            .transform(new RoundedCornersTransformation(10, 10))
                            .into(image_upload);
                }
                break;
            default: // DO NOTHING
        }
    }

    /**
     * Requests a permission for the camera intent and opens the camera application
     * in order to take a photo.
     */
    private void launchCamera() {
        // Check camera permission and request permission if not granted.
        boolean check = Utility.checkPermission(this, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, getResources().getString(R.string.camera_rationale));
        if (check) {
            // Launch camera
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
        }
    }

    /**
     * Requests a permission to access photos and opens the gallery to select a picture.
     */
    private void launchGallery() {
        boolean check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, getResources().getString(R.string.external_storage_rationale));
        if (check) {
            // Open gallery
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle arrow click to return to previous activity
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showErrorNotification() {
    }

    @Override
    public void updateUI(FeedComment commentItem, User user) {
        Log.d(Application.TAG, "Comment sent to adapter");
        commentAdapter.addItem(commentItem);
        mRecyclerView.smoothScrollToPosition(0);
        if (feedData.getComments().size() != 0)
            comment_text.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateFeedData(List<FeedComment> comments) {
        feedData.setComments(comments);
        commentAdapter.setCommentItems(comments);
        if (comments.size() != 0)
            comment_text.setVisibility(View.VISIBLE);
        else
            comment_text.setVisibility(View.GONE);
//        post_comment_responses.setText(comments.size());
    }

    @Override
    public void showErrorMessage() {
        // Display snack bar showing
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Error Loading Comments.Please Try Again", Snackbar.LENGTH_LONG)
                .setAction("RETRY", (view) -> {
                    // Load comments again
                    presenter.loadComments(user.get_id().get_$oid(), feedData.get_id().get_$oid());
                });
        // Change text color of text
        snackbar.setActionTextColor(Color.RED);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();

        // Also hide progress bar
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }
}

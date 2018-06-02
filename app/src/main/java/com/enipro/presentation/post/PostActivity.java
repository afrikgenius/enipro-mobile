package com.enipro.presentation.post;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.robototextview.widget.RobotoEditText;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.data.remote.model.PremiumDetails;
import com.enipro.injection.Injection;
import com.enipro.model.ApplicationService;
import com.enipro.model.Constants;
import com.enipro.model.ServiceType;
import com.enipro.model.Utility;
import com.enipro.model.ValidationService;
import com.enipro.presentation.generic.TagRecyclerAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;

import org.parceler.Parcels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PostActivity extends AppCompatActivity implements PostContract.View {

    private static final String EXTRA_DATA = "EXTRA_DATA";

    private static final int NONE = 0;
    private static final int IMAGE = 1;
    private static final int VIDEO = 2;
    private static final int DOCUMENT = 3;

    // State of a plain feed until premium is set
    private boolean isPremium = false;

    private PostContract.Presenter presenter;
    private TagRecyclerAdapter tagRecyclerAdapter;
    private RecyclerView mRecyclerView;
    Bitmap imageUploadBitmap;
    String videoPath;
    File documentFile; // File attached to the post object.
    MediaMetadataRetriever retriever;

    // Premium content details
    private EditText bankNumberEditText;
    private EditText amountEditText;
    String bankName;
    private PremiumDetails premiumDetails = null;


    @BindView(R.id.post_content_edit)
    EditText postContentEditText;
    @BindView(R.id.post_feed)
    Button post_button;
    @BindView(R.id.add_tags)
    RobotoEditText add_tags;
    @BindView(R.id.tags_clear)
    ImageButton tags_clear;
    @BindView(R.id.camera)
    ImageButton camera;
    @BindView(R.id.video)
    ImageButton video;
    @BindView(R.id.doc)
    ImageButton doc;
    @BindView(R.id.tags)
    ImageButton tags;
    @BindView(R.id.tags_layout)
    View tags_layout;
    @BindView(R.id.tags_view)
    View tags_view;
    @BindView(R.id.image_view)
    ImageView image_view;
    @BindView(R.id.video_view)
    UniversalVideoView video_view;
    @BindView(R.id.video_view_layout)
    FrameLayout videoViewLayout;
    @BindView(R.id.media_controller)
    UniversalMediaController mediaController;
    @BindView(R.id.media_cancel)
    ImageButton media_cancel;
    @BindView(R.id.media_view)
    View media_view;
    @BindView(R.id.premium)
    ImageButton premium;
    @BindView(R.id.doc_layout)
    View doc_layout;
    @BindView(R.id.doc_icon)
    ImageView doc_icon;
    @BindView(R.id.doc_name)
    TextView doc_name;
    @BindView(R.id.doc_cancel)
    ImageView doc_cancel;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, PostActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ValidationService validationService = (ValidationService) ApplicationService.getInstance(ServiceType.ValidationService);
        presenter = new PostPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), validationService);
        presenter.attachView(this);

        // Recycler view and adapter for news feed items
        mRecyclerView = findViewById(R.id.tags_recyclerview);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        tagRecyclerAdapter = new TagRecyclerAdapter(null, 0);
        mRecyclerView.setAdapter(tagRecyclerAdapter);
        video_view.setMediaController(mediaController);

        // TODO Check if an intent extra data exists and prepopulate fields with the data.
        if (getIntent().getParcelableExtra(Constants.FEED_EXTRA) != null)
            prepopulateView(getIntent().getParcelableExtra(Constants.FEED_EXTRA));

        // Close button to close activity.
        findViewById(R.id.post_close).setOnClickListener(view -> {
            if (isFeedContentEmpty())
                this.finish();
            else {
                // Wrap feed content into a feed object
                Feed feed = new Feed();

                // Spin up a dialog and ask the user to save or discard.
                new MaterialDialog.Builder(this)
                        .content(R.string.save_as_draft)
                        .positiveText(R.string.save)
                        .onPositive((dialog, which) -> {
                            // TODO Save draft in the application for later edit.
                            saveFeedAsDraft(feed);
//                            this.finish();
                        })
                        .negativeText(R.string.discard)
                        .onNegative((dialog, which) -> this.finish())
                        .show();
            }
        });

        // Post button unfades and is clickable
        onFeedContentChange();
        onTagContentChange(); // When content of tag edit text is changed.
        setActionButtonListeners();

        // Post button that collects feed content information.
        findViewById(R.id.post_feed).setOnClickListener(v -> {
            FeedContent content = new FeedContent();
            content.setText(postContentEditText.getText().toString());
            Feed feed = new Feed();
            if (tagRecyclerAdapter.getItemCount() == 0) {
                Utility.showToast(this, R.string.enforce_tags, true);
                return;
            }

            feed.setTags(tagRecyclerAdapter.getItems());
            // Check visibility of both image and video view and based on the visible view, upload media to firebase and return downloadURL
            // Only one media item can be uploaded for a post. Both views have visibility of gone and we are working on the view present.
            if (image_view.getVisibility() == View.VISIBLE && media_view.getVisibility() == View.VISIBLE) {
                // Image View is visible and check for null reference in imageUploadBitmap
                if (image_view != null && imageUploadBitmap != null) {
                    // Send all content to Feed Fragment and leave post activity immediately.
                    // Save temp bitmap in Application class.
                    content.setMediaType(FeedContent.MediaType.IMAGE);
                    Application.saveTempBitmap(imageUploadBitmap);
                    Application.setFeedMediaIdentifier(Application.BITMAP_IDENTIFIER_FEEDS);
                }
            } else if (videoViewLayout.getVisibility() == View.VISIBLE && media_view.getVisibility() == View.VISIBLE) {
                // Video view is visible and check for null reference in video Upload file.
                // TODO Handle this appropriately.
                if (video_view != null) {
                    // Get video from video view
                    content.setMediaType(FeedContent.MediaType.VIDEO);
                    Application.saveTempVideoPath(videoPath);
                    Application.setFeedMediaIdentifier(Application.VIDEO_IDENTIFIER_FEEDS);
                }
            }

            // Check if a document exists in the post
            if (doc_layout.getVisibility() == View.VISIBLE && documentFile != null) {
                Application.setDocumentPostFile(documentFile);
                content.setDocExists(true);
            }

            // Check if there is a premium data to send
            if (premiumDetails != null)
                feed.setPremiumDetails(premiumDetails
                );
            feed.setContent(content);
            sendFeedItem(feed);
        });

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);
    }


    /**
     * Populates all view elements with data from a feed information.
     *
     * @param feed the feed information.
     */
    private void prepopulateView(Feed feed) {
        postContentEditText.setText(feed.getContent().getText());

        // Check for images and videos
        if (feed.getContent().getImage() != null) {
            Bitmap imageBitmap;
            try {
                imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(feed.getContent().getImage())));
                // Store a reference to original bitmap to send to Firebase
                imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
            } catch (FileNotFoundException fnfe) {
                FirebaseCrash.log(fnfe.getMessage());
            }

            // TODO Change to Glide Library
            Picasso.with(this).load(Uri.parse(feed.getContent().getVideo()))
                    .fit()
                    .centerCrop()
                    .into(image_view);
            modifyMediaViews(true, IMAGE);
        } else if (feed.getContent().getVideo() != null) {
            Uri videoURI = Uri.parse(feed.getContent().getVideo());
            video_view.setVideoURI(videoURI);
            video_view.start();
            videoPath = Utility.getVideoPath(this, videoURI);
            modifyMediaViews(true, VIDEO);
        }

        // Check for premium details available
        if (feed.getPremiumDetails() != null)
            premium.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_active));

        // Check for tags
        tags_layout.setVisibility(View.VISIBLE);
        tags_view.setVisibility(View.VISIBLE);
        tagRecyclerAdapter.setItems(feed.getTags());

        // Check for a document
        if (feed.getContent().getDoc() != null) {
            doc_layout.setVisibility(View.VISIBLE);
            // Display file name of doc
            Document document = feed.getContent().getDoc();
            doc_name.setText(document.getName());
        }
    }

    private void setActionButtonListeners() {
        video.setOnClickListener(v -> launchVideo());
        camera.setOnClickListener(v -> launchCamera());
        tags.setOnClickListener(v -> {
            if (tags_layout.getVisibility() == View.VISIBLE) {
                // Clear tags already added
                add_tags.setText("");
                tags_layout.setVisibility(View.GONE);
                tags_view.setVisibility(View.GONE);

                // Clear tags already attached.
                tagRecyclerAdapter.clear();
            } else {
                tags_layout.setVisibility(View.VISIBLE);
                tags_view.setVisibility(View.VISIBLE);
                add_tags.requestFocus();
            }
        });
        tags_clear.setOnClickListener(v -> add_tags.setText(""));
        media_cancel.setOnClickListener(v -> {
            if (media_view.getVisibility() == View.VISIBLE) {
                // Delete image from image_view or video view and set visibility to gone
                image_view.setImageDrawable(null);
                video_view.setVideoPath("");
                media_view.setVisibility(View.GONE);
                image_view.setVisibility(View.GONE);
                videoViewLayout.setVisibility(View.GONE);
                modifyMediaViews(false, NONE); //  Make video and camera action button unblur
            }
        });

        // Set the post to contain a premium content and change the drawable of the icon
        premium.setOnClickListener(v -> {
            if (isPremium) {
                premium.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_outline));
                premiumDetails = null;
                isPremium = !isPremium;
            } else {
                // Open material dialog and ask user to put in an amount for the content and also
                // provide account information where the proceeds go to.
                if (documentFile == null) {
                    Utility.showToast(this, "A document must be uploaded to be premium", true);
                } else {
                    MaterialSpinner bankSpinner;
                    MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                            .negativeText(R.string.cancel)
                            .positiveText(R.string.ok)
                            .customView(R.layout.premium_content_view, false)
                            .onPositive(((dialog, which) -> {
                                // Validate all information provided to check for empty strings and nulls
                                String amount = amountEditText.getText().toString();
                                String bankAccountNumber = bankNumberEditText.getText().toString();

                                // TODO Check the account number details using NUBAN Account number checker.

                                if (amount.equals("") || bankAccountNumber.equals("")) {
                                    Utility.showToast(this, "All fields are mandatory for a premium content.", true);
                                } else {
                                    premiumDetails = new PremiumDetails(bankAccountNumber, bankName, Integer.valueOf(amount));
                                    premium.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_active));
                                    isPremium = !isPremium;
                                }
                            }))
                            .onNegative((dialog, which) -> {

                            })
                            .build();

                    View customView = materialDialog.getCustomView();
                    bankSpinner = customView.findViewById(R.id.bank_name);
                    bankSpinner.setItems(Arrays.asList(getResources().getStringArray(R.array.banks)));
                    bankSpinner.setOnItemSelectedListener((view, position, id, item) -> bankName = item.toString());
                    bankNumberEditText = customView.findViewById(R.id.bank_account_number);
                    amountEditText = customView.findViewById(R.id.amount);

                    materialDialog.show();
                }
            }
        });
        doc.setOnClickListener(v -> permissionCheck(Constants.OPEN_EXTERNAL_STORAGE, FeedContent.MediaType.DOC));
        doc_cancel.setOnClickListener(view -> {
            if (documentFile != null) {
                documentFile = null;
                doc_layout.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Opens an intent to select a document with the mime types listed.
     */
    void selectAttachement() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        String[] mimetypes = {
                "application/pdf",
                "application/powerpoint",
                "application/msword",
                "text/plain",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, Utility.DOC_REQUEST_CODE);
    }

    void modifyMediaViews(boolean visible, int choice) {
        if (visible) {
            video.setImageResource(R.drawable.ic_video_camera_blur);
            video.setClickable(false);
            camera.setImageResource(R.drawable.ic_photo_camera_blur);
            camera.setClickable(false);
            media_view.setVisibility(View.VISIBLE);
            if (choice == IMAGE) image_view.setVisibility(View.VISIBLE);
            else if (choice == VIDEO) videoViewLayout.setVisibility(View.VISIBLE);
        } else {
            video.setImageResource(R.drawable.ic_video_camera);
            video.setClickable(true);
            camera.setImageResource(R.drawable.ic_photo_camera);
            camera.setClickable(true);
        }
    }


    private void onTagContentChange() {
        add_tags.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    tags_clear.setVisibility(View.VISIBLE);
                } else {
                    tags_clear.setVisibility(View.GONE);
                }

                // Check for text wrapping and get the content of the text in previous line to change background color
                if (!charSequence.toString().equals("")) {
                    String lastChar = charSequence.toString().substring(charSequence.length() - 1);
                    if (lastChar.equals(" ")) {
                        // Remove trailing with white space character
                        // Get charSequence and add to adapter
                        tagRecyclerAdapter.addItem(charSequence.toString().substring(0, charSequence.length() - 1));
                        add_tags.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    /**
     * Requests a permission for the camera intent and opens the camera application
     * in order to take a photo.
     */
    private void launchCamera() {
        // Open material dialog to choose from gallery or take a photo
        new MaterialDialog.Builder(this).items(R.array.photo_items)
                .itemsCallback((dialog, view, which, text) -> permissionCheck(which, FeedContent.MediaType.IMAGE))
                .show();
    }

    /**
     * Requests a permission to access video and opens the gallery to select a video.
     */
    private void launchVideo() {
        // Open material dialog to choose from gallery or take a photo
        new MaterialDialog.Builder(this).items(R.array.video_items)
                .itemsCallback((dialog, view, which, text) -> permissionCheck(which, FeedContent.MediaType.VIDEO))
                .show();
    }

    private void permissionCheck(int which, int mediaType) {
        if (which == 0) {
            // Check camera permission and request permission if not granted.
            boolean check = Utility.checkPermission(this, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, getResources().getString(R.string.camera_rationale));
            if (check) {
                switch (mediaType) {
                    case FeedContent.MediaType.IMAGE:
                        // Launch camera
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
                        break;
                    case FeedContent.MediaType.VIDEO:
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(intent, Utility.VIDEO_CAPTURE_REQUEST_CODE);
                        break;
                }
            }
        } else {
            // Open gallery
            boolean check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + mediaType, getResources().getString(R.string.external_storage_rationale));
            if (check) {
                switch (mediaType) {
                    case FeedContent.MediaType.IMAGE:
                        // Open gallery
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
                        break;
                    case FeedContent.MediaType.VIDEO:
                        // Open Gallery to choose a video
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Utility.VIDEO_GALLERY_REQUEST_CODE);
                        break;
                    case FeedContent.MediaType.DOC:
                        selectAttachement();
                }
            }
        }
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
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.IMAGE) {
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
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.DOC) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "External Storage permission has now been granted");
                selectAttachement();
            } else {
                Log.i(Application.TAG, "External storage permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }
        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE + FeedContent.MediaType.VIDEO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Application.TAG, "External Storage permission has now been granted");
                // Open Gallery to choose a video
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Utility.VIDEO_GALLERY_REQUEST_CODE);
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
                    modifyMediaViews(true, IMAGE);
                    image_view.setImageBitmap(imageBitmap);
                }
                break;
            case Utility.GALLERY_REQUEST_CODE: // Photo picked from gallery
                if (resultCode == RESULT_OK) {
                    Bitmap imageBitmap;
                    try {
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        // Store a reference to original bitmap to send to Firebase
                        imageUploadBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    } catch (FileNotFoundException fnfe) {
                        FirebaseCrash.log(fnfe.getMessage());
                    }
                    modifyMediaViews(true, IMAGE);
                    // TODO Change to Glide library.
//                    RequestOptions options =
                    Picasso.with(this).load(data.getData())
                            .fit()
                            .centerCrop()
                            .into(image_view);
                }
                break;
            case Utility.VIDEO_GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (lengthVideo(data.getData()) > Constants.VIDEO_LIMIT_MILLIS)
                        Utility.showToast(this, R.string.video_limit, true);
                    else {
                        video_view.setVideoURI(data.getData());
                        video_view.start();
                        videoPath = Utility.getVideoPath(this, data.getData());
                        modifyMediaViews(true, VIDEO);
                    }
                }
                break;
            case Utility.VIDEO_CAPTURE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (lengthVideo(data.getData()) > Constants.VIDEO_LIMIT_MILLIS)
                        Utility.showToast(this, R.string.video_limit, true);
                    else {
                        video_view.setVideoURI(data.getData());
                        video_view.start();
                        videoPath = Utility.getVideoPath(this, data.getData());
                        modifyMediaViews(true, VIDEO);
                    }
                }
                break;
            case Utility.DOC_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Check size of file
                    String path = Utility.getPath(this, data.getData());
                    File file = new File(path);
                    if (file.length() > Constants.DOC_LIMIT_BYTES)
                        Utility.showToast(this, R.string.doc_limit, true);
                    else {
                        // Save document file to instance variable.
                        documentFile = file;
                        doc_layout.setVisibility(View.VISIBLE);
                        doc_name.setText(file.getName());
                    }
                }
                break;
            default: // DO NOTHING
        }
    }

    private long lengthVideo(Uri videoURI) {
        retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(new File(Utility.getVideoPath(this, videoURI))));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(time);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    /**
     * When feed content changes.
     */
    void onFeedContentChange() {
        postContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (count > 0) {
                    post_button.setTextColor(getResources().getColor(R.color.colorPrimary));
                    post_button.setClickable(true);
                } else {
                    post_button.setTextColor(getResources().getColor(R.color.material_grey_400_));
                    post_button.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (retriever != null)
            retriever.release();
    }

    /**
     * Checks if there is a content placed in the feed space by the user
     *
     * @return true if there is no feed content and false if there is.
     */
    boolean isFeedContentEmpty() {
        return postContentEditText.getText().toString().length() == 0;
    }

    @Override
    public void saveFeedAsDraft(Feed feed) {

    }

    @Override
    public void showPostError(String errorMessage) {

    }

    @Override
    public void sendFeedItem(Feed feed) {
//         Send feed item back to home activity into feed fragment to be added.
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PostContract.Presenter.ACTIVITY_RETURN_KEY, Parcels.wrap(feed));
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom);
        super.onPause();
    }

}
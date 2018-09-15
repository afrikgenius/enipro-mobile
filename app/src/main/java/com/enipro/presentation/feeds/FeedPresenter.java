package com.enipro.presentation.feeds;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.EniproRestService;
import com.enipro.data.remote.model.Document;
import com.enipro.data.remote.model.Feed;
import com.enipro.data.remote.model.FeedContent;
import com.enipro.data.remote.model.SavedFeed;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.AppExecutors;
import com.enipro.model.Constants;
import com.enipro.model.LocalCallback;
import com.enipro.model.Utility;
import com.enipro.presentation.base.BasePresenter;
import com.enipro.presentation.feeds.comments.FeedCommentActivity;
import com.enipro.presentation.post.PostActivity;
import com.enipro.presentation.profile.ProfileActivity;
import com.enipro.presentation.utility.ImageOverlayView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import co.paystack.android.ui.CardActivity;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import retrofit2.HttpException;

public class FeedPresenter extends BasePresenter<FeedContract.View> implements FeedContract.Presenter {

    private EniproDatabase dbInstance;

    private Context context;
    private boolean likeState = false;
    private boolean savedState = false;

    public FeedPresenter(EniproRestService restService, Scheduler ioScheduler, Scheduler mainScheduler, Context context) {
        super(restService, ioScheduler, mainScheduler);
        dbInstance = EniproDatabase.Companion.getInstance(context);
        this.context = context;
    }

    @Override
    public void deleteFeed(Feed feed) {
        addDisposable(restService.deleteFeedItem(feed.get_id().getOid(), Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(aVoid -> {
                }, throwable -> {
                }, () -> {
                }));
    }

    @Override
    public void processFeed(Feed feedData) {
        checkViewAttached();
        getView().showPostNotification();

        // Check if a media item exists(image or video) and persist in firebase.
        if (feedData.getContent().getMediaType() == FeedContent.MediaType.INSTANCE.getIMAGE()) {
            // Persist image in firebase.
            StorageReference storageReference = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_FEED_IMAGES_LOCATION + feedData.getContent().getMediaName() + ".jpg");
            Bitmap imageBitmap = null;
            if (Application.getFeedMediaIdentifier().equals(Application.BITMAP_IDENTIFIER_FEEDS))
                imageBitmap = Application.getTempBitmap();
            Utility.uploadImageFirebase(storageReference, imageBitmap, downloadURL -> {
                feedData.getContent().setImage(downloadURL);
                // Clear application temp bitmap
                Application.flushTempBitmap();

                // Checks if a document exists and upload the doc to firebase and send post.
                if (feedData.getContent().isDocExists())
                    uploadDocFirebase(feedData);
                else
                    sendPostToAPI(feedData);
            });
        } else if (feedData.getContent().getMediaType() == FeedContent.MediaType.INSTANCE.getVIDEO()) {
            // Persist video in firebase
            StorageReference storageReference = FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child(Constants.FIREBASE_FEED_VIDEO_LOCATION + feedData.getContent().getMediaName() + ".mp4");
            Uri imageURI = null;
            if (Application.getFeedMediaIdentifier().equals(Application.VIDEO_IDENTIFIER_FEEDS))
                imageURI = Uri.fromFile(new File(Application.getTempVideoPath()));
            Utility.uploadVideoFirebase(storageReference, imageURI, (downloadURL) -> {
                feedData.getContent().setVideo(downloadURL);
//                sendPostToAPI(feedData);
                // Clear application video URI
                Application.flushTempVideoPath();

                // Checks if a document exists and upload the doc to firebase and send post.
                if (feedData.getContent().isDocExists())
                    uploadDocFirebase(feedData);
                else
                    sendPostToAPI(feedData);

            });
        } else {
            // Checks if a document exists and upload the doc to firebase and send post.
            if (feedData.getContent().isDocExists()) {
                uploadDocFirebase(feedData);
            } else
                sendPostToAPI(feedData);
        }
    }


    /**
     * Creates a storage ref and uploads a doc to firebase storage and saves the download url of
     * the doc file in the feed content data of the feed and sends the information to the web api.
     *
     * @param feedData the feed data to modify with the download url.
     */
    private void uploadDocFirebase(Feed feedData) {
        File file = Application.getDocumentPostFile();
        Uri fileUri = Uri.fromFile(file);
        StorageReference storageReference = FirebaseStorage
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_FEED_DOCUMENT_LOCATION + fileUri.getLastPathSegment());
        try {
            Utility.uploadFileFirebase(storageReference, file, downloadURL -> {
                String filename = FilenameUtils.removeExtension(fileUri.getLastPathSegment());
                Document document = new Document(filename, downloadURL, (int) file.length(), FilenameUtils.getExtension(fileUri.getLastPathSegment()));
                feedData.getContent().setDoc(document);
                sendPostToAPI(feedData);
            });
        } catch (FileNotFoundException fnfe) {
            Crashlytics.log(fnfe.getMessage());
        }
    }

    @Override
    public void sendPostToAPI(Feed feedData) {
        checkViewAttached();
        addDisposable(restService.createFeedItem(feedData, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(feed -> {
                    getView().showCompleteNotification();
                    // new AppExecutors().diskIO().execute(() -> dbInstance.feedDao().insertFeed(response.body()));
                    getView().updateUI(feed);
                }, throwable -> getView().showErrorNotification()));
    }

    /**
     * Uses the user id to return the information of a user.
     *
     * @param _id the id of the user.
     * @return
     */
    @Override
    public void getUser(String _id, LocalCallback localCallback) {
        checkViewAttached();
        // Check if the id parameter is that of the active user and return active user object
        new AppExecutors().diskIO().execute(() -> {
            User appUser = dbInstance.user().getUser(_id);
            if (appUser != null)
                localCallback.respond(appUser);
            else {
                addDisposable(restService.getUser(_id, Application.getAuthToken())
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribe(user -> localCallback.respond(user)));
            }
        });
    }

    @Override
    public void loadSavedFeeds() {
        // Collect all saved feed info from application user profile and for each, request feed with feed id.
        List<SavedFeed> feeds = Application.getActiveUser().getSavedFeeds();
        addDisposable(restService.getSavedFeeds(Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(response -> {
//                    getView().onSavedFeedsRetrieved(response.getResult());
                }, throwable -> {
                }, () -> {
                }));
    }

    @Override
    public void removeSaved(Feed feed) {
        addDisposable(restService.deleteSavedFeed(feed.get_id().getOid(), Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                }, throwable -> {
                    String errorBody = ((HttpException) throwable).response().errorBody().string();
                }, () -> {
                }));
    }

    @Override
    public void addSaved(Feed feed) {
        addDisposable(restService.saveFeed(new SavedFeed(feed.get_id().getOid()), Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(user -> {
                    // A new user information is gotten, save in local storage and application user
                    new AppExecutors().diskIO().execute(() -> dbInstance.user().updateUser(user));
                    Application.setActiveUser(user);
                }, throwable -> {
                    String errorBody = ((HttpException) throwable).response().errorBody().string();
                }, () -> {
                }));
    }

    /**
     * Checks if the authenticated user has liked a feed item or not. This does not require a network
     * call since the feed information is also saved in Room.
     *
     * @param feed the feed item
     * @return true if liked or false otherwise
     */
    boolean getLikeState(Feed feed) {
        return false;
    }

    boolean getSaveSatate(Feed feed) {
        return false;
    }

    public void onLikeClicked(View view, Feed feed) {
        if (!likeState) {
            // First off increase the number of comments and make the animation happen then
            // revert if error occurred
            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite, null));
            Utility.applyBounceAnimation((ImageButton) view, context);
//            String likesStr =
            int currentNoLikes = Integer.valueOf("5");

//            likeFeed(feed.getId()).subscribe(feedObject -> {
//                int likes = feed.getLikesCount();
//            }, throwable -> {
//
//            });
            likeFeed(feed.getId());
        } else {
            unlikeFeed(feed.getId());
            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_favorite_border, null));
            Utility.applyBounceAnimation((ImageButton) view, context);
        }
        likeState = !likeState; // Invert like state.
    }

    Observable<Feed> likeFeed(String feed_id) {
        return restService.likeFeed(feed_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler);
    }

    Observable<Feed> unlikeFeed(String feed_id) {
        return restService.unlikeFeed(feed_id, Application.getAuthToken())
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler);
    }

    /***
     * Starts an activity that displays a list of users that liked a feed item.
     * @param feed the feed to get information from.
     */
    public void getFeedLikeUsers(Feed feed) {

    }

    public void onHeaderClicked(User user) {
        context.startActivity(ProfileActivity.Companion.newIntent(context, user));
    }

    /**
     * Activates the comment edit text when the comment button is clicked.
     */
    public void onCommentClicked() {
//        binding.postAComment.commentEditText.requestFocus();
//        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    /**
     * Advances the current activity to the feed comment activity containing information (comments)
     * on feed item clicked.
     *
     * @param feed the feed object attached to feed item clicked.
     */
    public void onCommentButtonClicked(Feed feed) {
        // Open the feed comment activity and activate the keyboard on the add comment edit text
        Intent intent = FeedCommentActivity.Companion.newIntent(context);
        intent.putExtra(FeedContract.Presenter.OPEN_KEYBOARD_COMMENT, FeedContract.Presenter.ADD_COMMENT);
        intent.putExtra(FeedContract.Presenter.FEED, feed);
        context.startActivity(intent);
    }

    public void onViewClicked(Feed feed) {
        Intent intent = FeedCommentActivity.Companion.newIntent(context);
        intent.putExtra(FeedContract.Presenter.FEED, feed);
        context.startActivity(intent);
    }


    public void onSaveClicked(View view, Feed feed) {
        // Change drawable to a full favorite button and apply a one bounce animation on it.
        if (!savedState) {
            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark, null));
            Utility.applyBounceAnimation((ImageButton) view, context);
            // Snack bar Saved
            Utility.showToast(context, R.string.saved, false);
//            presenter.addSaved(feed); // TODO Come back to this
        } else {
            ((ImageButton) view).setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_bookmark_outline, null));
            Utility.applyBounceAnimation((ImageButton) view, context);
//            presenter.removeSaved(feed); // TODO Come back to this
        }
        savedState = !savedState; // Invert saved state.
    }

    public void onShareClicked(Feed feed) {
        // Create an intent with a SEND Action
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain"); // Change Mime type to handle enipro posts.

        // Grab feed item information and put in intent.
        sharingIntent.putExtra(Intent.EXTRA_TEXT, feed.getContent().getText());
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_text)));
    }

    public void onDocClicked(Feed feedItem) {
        // Get document object
        Document doc = feedItem.getContent().getDoc();
        //  Download the item and open using default app for that item.
        // TODO Try on all devices to see if this feature works.
        // In a case the feed item has been tagged premium, show item pricing and option to pay
        if (feedItem.getPremiumDetails() == null) {
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent newIntent = new Intent(Intent.ACTION_VIEW);
            String mimeType = myMime.getMimeTypeFromExtension(doc.getExtension());
            newIntent.setDataAndType(Uri.parse(doc.getUrl()), mimeType);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(newIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        } else {
            // TODO Show option to pay for content. A dialog showing information like document name and price of content.
            // TODO Check if the user has paid for this item. Get current user then
            // TODO

//            Intent paymentIntent = PaymentsFormActivity.newIntent(context);
//            paymentIntent.putExtra(Constants.FEED_EXTRA, Parcels.wrap(feedItem));

            // TODO If this is better, make use of this instead
            Intent intent = new Intent(context, CardActivity.class);
            context.startActivity(intent);
        }
    }

    public void onMoreClicked(Feed feed) {
        if (feed.getUser().equals(Application.getActiveUser().get_id().getOid())) {
            new MaterialDialog.Builder(context)
                    .items(R.array.app_user_more_items)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        switch (position) {
                            case 0: // Open post activity with predefined views
                                Intent intent = PostActivity.Companion.newIntent(context);
                                intent.putExtra(Constants.FEED_EXTRA, feed);
                                context.startActivity(intent);
                                break;
                            case 1: // Show tags in a material dialog.
                                break;
                            case 2: // Delete post
//                                presenter.removeFeed(feed);
//                                removeItem(feed_position);
//                                // call presenter to delete post from API
//                                presenter.deleteFeed(feed);
                                break;
                        }
                    }).show();
        } else {
            // Not application users post. the user can hide
            new MaterialDialog.Builder(context)
                    .items(R.array.foreign_user_more_items)
                    .itemsCallback((dialog, itemView, position, text) -> {
                        // Action Listeners for hiding post and
                        switch (position) {
                            case 0: // Hide Post
//                                removeItem(feed_position);
                                break;
                            case 1: // Show tags in a material dialog
                                break;
                        }
                    }).show();
        }
    }

    // TODO Build support for upload of multiple images
    public void onImageClicked(Feed feed) {
        List<String> urls = new ArrayList<>();
        urls.add(feed.getContent().getImage());
        ImageOverlayView overlayView = new ImageOverlayView(context);
        overlayView.setCommentsCount(feed.getCommentsCount());
        overlayView.setLikesCount(feed.getLikesCount());

        // TODO  This process makes two network requests to load the image
        // TODO 1. Using Glide 2. Using FrescoImageViewer

        // Get background color from the image using Palette api
        Glide.with(context)
                .asBitmap()
                .load(feed.getContent().getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        // Get the color using the Palette api
                        Palette palette = Palette.from(resource).generate();
                        int color = palette.getVibrantColor(context.getResources().getColor(R.color.black));
                        ImageViewer viewer = new ImageViewer.Builder<>(context, urls)
                                .setStartPosition(0)
                                .setOverlayView(overlayView)
                                .setBackgroundColor(color)
                                .build();

                        overlayView.findViewById(R.id.photo_viewer_back).setOnClickListener(v -> viewer.onDismiss());
                        viewer.show();

                    }
                });
    }
}
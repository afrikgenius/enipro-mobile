package com.enipro.model;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.firebase.FirebaseNotificationBuilder;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Utility {

    public static final int CAMERA_REQUEST_CODE = 0x00; // Request code for accessing camera and returning images.
    public static final int GALLERY_REQUEST_CODE = 0x01; // Request code for accessing gallery on the device
    public static final int VIDEO_GALLERY_REQUEST_CODE = 0x02; // Request code for accessing video in gallery on the device
    public static final int VIDEO_CAPTURE_REQUEST_CODE = 0x03; // Request code for recording a video.

    public static final int DOC_REQUEST_CODE = 0x04;

    public static final int PHOTO_SCALE_SIZE = 200; // Scale size for bitmaps when selected into an edit text.

    private static Bitmap userImageBitmap;


    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getRandomIdentifier() {
        return Application.getActiveUser().get_id().get_$oid() + new java.util.Random().nextLong() + new java.util.Date();
    }

    public static void collapseKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(Context context, View view, @StringRes int text, boolean isLong) {
        showSnackBar(view, context.getString(text), isLong);
    }

    public static void showSnackBar(View view, String text, boolean isLong) {
        Snackbar snackbar = Snackbar.make(view, text, isLong ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);
        TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static String getVideoPath(Context context, Uri videoURI) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity) context).managedQuery(videoURI, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    /**
     * @param context
     * @param editText
     */
    public static void showDatePickerDialog(Context context, EditText editText) {
        YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(context, (year, month) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
            editText.setText(dateFormat.format(calendar.getTime()));
        });
        yearMonthPickerDialog.show();
    }

    /**
     * Checks for permission and if not granted, requests permission
     * for the intent called.
     *
     * @param context     the context from which the permission is requested
     * @param permission  the permission to request for
     * @param requestCode the request code for the permission.
     * @return
     */
    public static boolean checkPermission(final Context context, String permission, int requestCode, CharSequence rationaleText) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//            Log.i(context.getClass().getCanonicalName(), "Permission not granted. Requesting permission.");
            requestPermission(context, permission, requestCode, rationaleText); // Requests the permission specified since the permission has not been granted.
        } else {
//            Log.i(context.getClass().getCanonicalName(), permission + " has already been granted. There is no need to request for it.");
            return true;
        }
        return false;
    }

    /**
     * Applies a bounce animation to an image button.
     *
     * @param imageButton the image button to animate.
     * @param context     the context which the button exists.
     */
    public static void applyBounceAnimation(ImageButton imageButton, Context context) {
        Animation bounceAnimation = AnimationUtils.loadAnimation(context, R.anim.bounce);
        imageButton.startAnimation(bounceAnimation);
    }

    /**
     * Requests a permission for the camera intent and opens the camera application
     * in order to take a photo.
     *
     * @return the intent to start activity for camera.
     */
    public static Intent launchCamera(Context context) {
        // Check camera permission and request permission if not granted.
        boolean check = Utility.checkPermission(context, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, context.getResources().getString(R.string.camera_rationale));
        if (check) {
            // Launch camera
            return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return null;
    }

    /**
     * Requests a permission to access photos and opens the gallery to select a picture.
     *
     * @return the intent to open gallery.
     */
    public static Intent launchGallery(Context context) {
        boolean check = Utility.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, context.getResources().getString(R.string.external_storage_rationale));
        if (check) {
            // Open gallery
            return new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        return null;
    }

    /**
     * Compresses a bitmap and returns the bytes array of the bitmap
     *
     * @param bitmap
     * @return
     */
    public static byte[] compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * Returns the firebase token associated with the instance of this application stored
     * in a shared preference.
     *
     * @return firebase token
     */
    public static String getTokenFromSharedPref(final Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.ARG_FIREBASE_TOKEN, null);
    }

    public static void showNotification(final Context context, final Class<?> cs, String title, String message, String receiver, String receiverUid, String firebaseToken) {
        Intent intent = new Intent(context, cs);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
    
    public static void showNotification(final Context context, final Intent intent, String title, String message, String receiver, String receiverUid, String firebaseToken) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(receiverUid, 0, notificationBuilder.build());
    }

    /**
     * Shows a request notification (network or circle) with action buttons to either accept or decline
     * the request.
     *
     * @param context       context from which this utility function is called
     * @param user          the user that sent the request
     * @param requestIntent the intent to activate when the notification is clicked
     * @param title         the title or the header of the request.
     * @param message       the notification message
     * @param username      the username of the user (i.e. email)
     */
    public static void showRequestNotification(Context context, User user, Intent requestIntent, PendingIntent acceptIntent, PendingIntent declineIntent, String title, String message, String username, String channel_id) {
        // Add two action buttons, one for accepting and one for rejecting the circle request
        // TODO Make sure to change ic_launcher to an appropriate drawable.
        NotificationCompat.Action acceptAction = new NotificationCompat.Action.Builder(R.drawable.ic_launcher,
                context.getString(R.string.accept), acceptIntent)
                .build();

        // Action button to decline the request.
        NotificationCompat.Action declineAction = new NotificationCompat.Action.Builder(R.drawable.ic_launcher,
                context.getString(R.string.decline), declineIntent)
                .build();

        Glide.with(context).asBitmap().load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.profile_image))
                .into(new SimpleTarget<Bitmap>(50, 50) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        userImageBitmap = getRoundedCornerBitmap(resource, 50);
                    }
                });

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setLargeIcon(userImageBitmap)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, 0, requestIntent, PendingIntent.FLAG_ONE_SHOT))
                .addAction(declineAction)
                .addAction(acceptAction);

        NotificationManagerCompat nManager = NotificationManagerCompat.from(context);
        nManager.notify(username, 0, notificationBuilder.build());
    }

    /**
     * Send notification to recipient of the message.
     *
     * @param username
     * @param message
     * @param uid
     * @param firebaseToken
     * @param receiverFirebaseToken
     */
    public static void sendPushNotificationToReceiver(String title, String username, String message, String uid, String firebaseToken, String receiverFirebaseToken, String uniqueId) {
        FirebaseNotificationBuilder.initialize()
                .title(title)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .uniqueIdentifier(uniqueId)
                .send();
    }

    /**
     * Uploads an image to firebase and returns the download URl.
     *
     * @param storageReference the storage ref to use
     * @param bitmap           the bitmap image to upload
     * @param localCallback    the callback function to return URL.
     */
    public static void uploadImageFirebase(StorageReference storageReference, Bitmap bitmap, LocalCallback<String> localCallback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Storage metadata for the avatar file
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UploadTask uploadTask = storageReference.putBytes(data, storageMetadata);
        uploadTask.addOnFailureListener(exception -> {
            // TODO Handle unsuccessful uploads

        }).addOnSuccessListener(taskSnapshot -> {
            // Get the download URL and pass into callback function.
            localCallback.respond(taskSnapshot.getDownloadUrl().toString());
        });
    }

    /**
     * Uploads a file to firebase storage and returns the download url of the file.
     *
     * @param storageReference the storage ref to use
     * @param file             the file to upload to firebase
     * @param localCallback    the callback function that returns the download url of the file.
     */
    public static void uploadFileFirebase(StorageReference storageReference, File file, LocalCallback<String> localCallback) throws FileNotFoundException {
        UploadTask uploadTask = storageReference.putStream(new FileInputStream(file));
//        UploadTask uploadTask = storageReference.putFile(fileUri);
        uploadTask.addOnFailureListener(exception -> {

        }).addOnSuccessListener(taskSnapshot -> {
            localCallback.respond(taskSnapshot.getDownloadUrl().toString());
        });
    }

    /**
     * Upload a video to firebase and returns the download URL of the video
     *
     * @param storageReference the storage reference used
     * @param uri              the video to upload
     * @param localCallback    a callback function to return the download URL.
     */
    public static void uploadVideoFirebase(StorageReference storageReference, final Uri uri, LocalCallback<String> localCallback) {

        // Storage metadata for the video file
        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                .setContentType("video/mp4")
                .build();

        UploadTask uploadTask = storageReference.putFile(uri, storageMetadata);
        uploadTask.addOnFailureListener(exception -> {
        })
                .addOnSuccessListener(taskSnapshot -> localCallback.respond(taskSnapshot.getDownloadUrl().toString()));
    }


    /**
     * Creates a rounded bitmap from a bitmap using the specified number of pixels.
     *
     * @param bitmap the bitmap to make corners round.
     * @param pixels the pixels to use as a radius.
     * @return the new bitmap with rounded corners.
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static void onPhotoClickListener(Context context, List<Void> images) {
    }

    /**
     * Edits a bitmap, making it smaller and also giving it rounded corners for input into an edit text.
     *
     * @param bitmap the bitmap object to edit
     * @param pixels the number of pixels to use as a rounded corner.
     * @return the new transformed bitmap
     */
    public static BitmapDrawable editBitmapForEditText(Bitmap bitmap, Context context, int pixels, int bitmapSize) {
        if (bitmapSize == 0)
            bitmapSize = new BitmapDrawable(context.getResources(), bitmap).getIntrinsicWidth();
        Bitmap imageBitmap = getRoundedCornerBitmap(Bitmap.createScaledBitmap(bitmap, bitmapSize, bitmapSize, true), pixels);
        return new BitmapDrawable(context.getResources(), imageBitmap);
    }


    /**
     * Converts a drawable object into a bitmap object.
     *
     * @param drawable
     * @param widthPixels
     * @param heightPixels
     * @return
     */
    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return mutableBitmap;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * Requests for a permission from the system and grants permission unless permission request was denied then a snackbar with a
     * rationale text is displayed to give user an explanation of why the permission is needed.
     *
     * @param context       context where permission is requested.
     * @param permission    the permission to request for
     * @param requestCode   the request code for the permission
     * @param rationaleText the rationale text to be displayed when permission is denied.
     */
    private static void requestPermission(final Context context, String permission, int requestCode, CharSequence rationaleText) {
//        Log.i(context.getClass().getCanonicalName(), permission + " has not been granted. Requesting permission..");
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            // Show UI to explain why the permission is needed.
//            Log.i(context.getClass().getCanonicalName(), "Providing additional info as to why the permission is needed");
            View rootView = ((Activity) context).getWindow().getDecorView().getRootView();
            Snackbar.make(rootView, rationaleText, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, v -> ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode))
                    .show();
        } else {
            // Request permission
//            Log.i(context.getClass().getCanonicalName(), "Requesting Permission.");
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
        }
    }


    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;

        public DividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }

            outRect.top = mDivider.getIntrinsicHeight();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }
}
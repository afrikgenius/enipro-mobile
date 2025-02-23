package com.enipro.presentation.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.enipro.presentation.home.HomeActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddPhotoActivity extends AppCompatActivity implements SignupContract.View {

    public static final String TAG = ".AddPhotoActivity";
    StorageReference mStorageRef;

    CircleImageView profile_image;
    Button btnFinish;
    Button btnSkip;

    private MaterialDialog progressDialog;

    private SignupContract.Presenter presenter;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, AddPhotoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        profile_image = findViewById(R.id.profile_image);
        btnFinish = findViewById(R.id.finish);
        btnSkip = findViewById(R.id.skip);

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);

        // Get user object passed from previous activity
        User user = getIntent().getParcelableExtra(TAG);

        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.Companion.getInstance(this), this);
        presenter.attachView(this);

        // On click on image to upload a profile photo.
        profile_image.setOnClickListener(v -> {
            // Open material dialog to choose from gallery or take a photo
            new MaterialDialog.Builder(this)
                    .items(R.array.photo_items)
                    .itemsCallback((dialog, view, which, text) -> {
                        if (which == 0) {
                            // Open Camera
                            launchCamera();
                        } else {
                            // Open Gallery to choose a photo
                            launchGallery();
                        }
                    })
                    .show();
        });

        // Skip the image process and insert a default image in body of request.
        btnSkip.setOnClickListener(v -> {
            user.setAvatar(Constants.DEFAULT_PROFILE_URL);  // set avatar to default profile image.
            user.setAvatar_cover(Constants.DEFAULT_PROFILE_COVER_URL);
            presenter.persistUser(user);
        });

        btnFinish.setOnClickListener(v -> {
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mStorageRef = mStorageRef.child(Constants.FIREBASE_PROFILE_REF + user.getFirstName() + user.getLastName() + user.getEmail() + ".jpg");
            presenter.persistAvatarFirebase(user, profile_image, mStorageRef, (firebasePersistedUser) -> {
                firebasePersistedUser.setAvatar_cover(Constants.DEFAULT_PROFILE_COVER_URL);
                presenter.persistUser(firebasePersistedUser);
            });
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (mStorageRef != null)
            outState.putString(Constants.FIREBASE_UPLOAD_REF, mStorageRef.toString());
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        final String stringReference = savedInstanceState.getString(Constants.FIREBASE_UPLOAD_REF);
        if (stringReference == null) return;

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringReference);

        //TODO  Continue Handling lifecycle changes during upload to Firebase Storage
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(TAG, "Camera permission has now been granted.");
                // Launch Camera to take a picture
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
            } else {
//                Log.i(TAG, "Camera permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(TAG, "External Storage permission has now been granted");
                // Open gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
            } else {
//                Log.i(TAG, "External storage permission was not granted ");
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
                    profile_image.setImageBitmap(imageBitmap);
                }
                break;
            case Utility.GALLERY_REQUEST_CODE: // Photo picked from gallery
                if (resultCode == RESULT_OK)
                    profile_image.setImageURI(data.getData());
                break;
            default: // DO NOTHING
        }
    }

    @Override
    protected void onPause() {
        // When the activity is paused (i.e it is no longer visible), the activity leaves the screen by a slide
        // through the bottom of the screen.
        overridePendingTransition(R.anim.pull_hold, R.anim.slide_out_bottom);
        super.onPause();
    }

    @Override
    public void showProgress() {
        progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.wait)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void openApplication(User user) {
        Intent intent = HomeActivity.newIntent(this, user);
//        intent.putExtra(Constants.APPLICATION_USER, Parcels.wrap(user));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showMessage(int message) {
    }

    @Override
    public void showMessage(String type, String message) {
    }

    @Override
    public void showMessageDialog(int title, int message) {
    }

    @Override
    public void showMessageDialog(int title, String message) {
    }

    @Override
    public void showMessageDialog(String title, int message) {
    }

    @Override
    public void showMessageDialog(String title, String message) {
    }

    @Override
    public void setViewError(View view, String errorMessage) {
    }

    @Override
    public String getSpinnerData(String spinner_name) {
        return null;
    }

    @Override
    public void advanceProcess(User user) {
    }
}

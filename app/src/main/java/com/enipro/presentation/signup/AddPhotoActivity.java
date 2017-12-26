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
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.robototextview.widget.RobotoButton;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.User;
import com.enipro.db.EniproDatabase;
import com.enipro.injection.Injection;
import com.enipro.model.Utility;
import com.enipro.presentation.home.HomeActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddPhotoActivity extends AppCompatActivity implements SignupContract.View{

    public static final String TAG = ".AddPhotoActivity";
    private static final int CAMERA = 0x00; // Request code for opening camera application
    private static final int GALLERY = 0x01; // Request code for accessing gallery on the device

    /**
     * URL of the default profile image used in the application stored in Firebase Storage.
     */
    private static final String DEFAULT_PROFILE_URL = "https://firebasestorage.googleapis.com/v0/b/enipro-56ea8.appspot.com/o/profile_image.png?alt=media&token=26a1181a-bcf0-4f63-abda-8d78d65f12f4";

    /**
     * URL of the default profile cover image used in the application stored in Firebase Storage.
     */
    private static final String DEFAULT_PROFILE_COVER_URL = "https://firebasestorage.googleapis.com/v0/b/enipro-56ea8.appspot.com/o/Avatar_Cover.png?alt=media&token=4314280f-c04a-494b-97e6-5b60d060e803";

    private static final String FIREBASE_UPLOAD_REF = "reference";

    StorageReference mStorageRef;

    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.finish) RobotoButton btnFinish;
    @BindView(R.id.skip) RobotoButton btnSkip;

    private MaterialDialog progressDialog;

    private SignupContract.Presenter presenter;

    /**
     * Returns a new intent to open an instance of this activity.
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
        ButterKnife.bind(this);

        // Animate the activity into the screen from the bottom.
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.pull_hold);

        // Get user object passed from previous activity
        User user = getIntent().getParcelableExtra(TAG);

        presenter = new SignupPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), null,
                EniproDatabase.getInstance(this));
        presenter.attachView(this);

        // On click on image to upload a profile photo.
        profile_image.setOnClickListener(v -> {
            // Open material dialog to choose from gallery or take a photo
            new MaterialDialog.Builder(this)
                    .items(R.array.photo_items)
                    .itemsCallback((dialog, view, which, text) -> {
                        if(which == 0){
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
            user.setAvatar(DEFAULT_PROFILE_URL);  // set avatar to default profile image.
            user.setAvatar_cover(DEFAULT_PROFILE_COVER_URL);
            presenter.persistUser(user);
        });

        // Persist image in firebase and user data with a call to the API when the finish button is clicked.
        btnFinish.setOnClickListener(v -> {
            // Persist image saved in firebase before sending it to the API
            // Set up storage reference
            mStorageRef = FirebaseStorage.getInstance().getReference();
            // navigate to profile_avatar reference
            mStorageRef = mStorageRef.child("images/profile_avatar/" + user.getFirstName() + user.getLastName() + user.getEmail() + ".jpg");
            presenter.persistAvatarFirebase(user, profile_image, mStorageRef, (firebasePersistedUser) -> {
                // Sending the avatar to firebase storage is done on a separate thread and persisting user with
                // a call to the API should be done when the avatar is successfully sent to firebase storage.
                firebasePersistedUser.setAvatar_cover(DEFAULT_PROFILE_COVER_URL);
                presenter.persistUser(firebasePersistedUser);
            });
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        // If there is a firebase storage upload in progress, save the reference so it can be queried later.
        if(mStorageRef != null)
            outState.putString(FIREBASE_UPLOAD_REF, mStorageRef.toString());
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        // Get the reference of an upload if there is any in progress
        final String stringReference = savedInstanceState.getString(FIREBASE_UPLOAD_REF);
        if(stringReference == null) return;

        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringReference);

        //TODO  Continue Handling lifecycle changes during upload to Firebase Storage
    }

    /**
     * Requests a permission for the camera intent and opens the camera application
     * in order to take a photo.
     */
    private void launchCamera(){
        // Check camera permission and request permission if not granted.
        boolean check = Utility.checkPermission(this, Manifest.permission.CAMERA, Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA, getResources().getString(R.string.camera_rationale));
        if(check){
            // Launch camera
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, CAMERA);//zero can be replaced with any action codes
        }
    }

    /**
     * Requests a permission to access photos and opens the gallery to select a picture.
     */
    private void launchGallery(){
        boolean check = Utility.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE, getResources().getString(R.string.external_storage_rationale));
        if(check){
            // Open gallery
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , GALLERY);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_CAMERA) {
            // Check if the permission was accepted or denied.
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Camera permission has now been granted.");
                // Launch Camera to take a picture
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA);//zero can be replaced with any action codes
            } else {
                Log.i(TAG, "Camera permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }

        } else if(requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "External Storage permission has now been granted");
                // Open gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , GALLERY);
            } else {
                Log.i(TAG, "External storage permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CAMERA: // Camera selected
                if(resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profile_image.setImageBitmap(imageBitmap);
                }
                break;
            case GALLERY: // Photo picked from gallery
                if(resultCode == RESULT_OK)
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
                .progress(true, 0)
                .show();
    }

    @Override
    public void dismissProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void openApplication(User user) {
        Intent intent = HomeActivity.newIntent(this);
        intent.putExtra(HomeActivity.EXTRA_DATA, user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void showMessage(int message) {}

    @Override
    public void showMessage(String type, String message) {}

    @Override
    public void showMessageDialog(int title, int message) {}

    @Override
    public void showMessageDialog(int title, String message) {}

    @Override
    public void showMessageDialog(String title, int message) {}

    @Override
    public void showMessageDialog(String title, String message) {}

    @Override
    public void setViewError(View view, String errorMessage) {}

    @Override
    public String getSpinnerData(String spinner_name) {return null;}

    @Override
    public void advanceProcess(User user) {}

    @Override
    public SignupContract.Presenter getPresenter() {
        return presenter;
    }
}

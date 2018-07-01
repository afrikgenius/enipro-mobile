package com.enipro.presentation.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.enipro.Application;
import com.enipro.R;
import com.enipro.data.remote.model.Education;
import com.enipro.data.remote.model.Experience;
import com.enipro.data.remote.model.User;
import com.enipro.injection.Injection;
import com.enipro.model.Constants;
import com.enipro.model.Utility;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ProfileEditActivity extends AppCompatActivity implements ProfileContract.EditView {


    private User user;
    private boolean isProfileImage;
    ProfileContract.EditPresenter presenter;
    Bitmap profile_image_bitmap = null;
    Bitmap cover_image_bitmap = null;
    CustomAdapter edu_adapter;
    CustomAdapter exp_adapter;
    private EditText first_name, last_name, headline, bio, achievements;
    private List<Education> userEducation;
    private List<Experience> userExperience;

    @BindView(R.id.profile_image_edit)
    ImageButton profile_image_edit;
    @BindView(R.id.cover_photo_edit)
    ImageButton cover_photo_edit;
    @BindView(R.id.cover_edit_photoview)
    ImageView cover_photo;
    @BindView(R.id.profile_edit_imageview)
    ImageView profile_imageview;
    @BindView(R.id.edu_layout)
    View edu_layout;
    @BindView(R.id.exp_layout)
    View exp_layout;
    @BindView(R.id.first_name_layout)
    TextInputLayout first_name_layout;
    @BindView(R.id.last_name_layout)
    TextInputLayout last_name_layout;
    @BindView(R.id.headline_layout)
    TextInputLayout headline_layout;
    @BindView(R.id.bio_layout)
    TextInputLayout bio_layout;
    @BindView(R.id.edit_experience_recyclerview)
    RecyclerView exp_recycler;
    @BindView(R.id.edit_education_recyclerview)
    RecyclerView edu_recycler;
    @BindView(R.id.add_education)
    ImageButton add_education;
    @BindView(R.id.add_experience)
    ImageButton add_experience;
    @BindView(R.id.save)
    Button save;

    /**
     * Returns a new intent to open an instance of this activity.
     *
     * @param context the context to use
     * @return intent.
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, ProfileEditActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.profile_edit_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.edit_profile);
        user = Application.getActiveUser();

        presenter = new ProfileEditPresenter(Injection.eniproRestService(), Schedulers.io(), AndroidSchedulers.mainThread(), this);
        presenter.attachView(this);

        init();
        setUpClickListeners();
    }

    private void initEduRecycler() {
        // Recycler view and adapter for education
        edu_recycler.setHasFixedSize(true);
        edu_recycler.addItemDecoration(new Utility.DividerItemDecoration(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        edu_recycler.setLayoutManager(linearLayoutManager);
        // Get education school
        List<String> schools = new ArrayList<>();
        for (Education education : user.getEducation())
            schools.add(education.getSchool());
        edu_adapter = new CustomAdapter(schools);
        edu_recycler.setAdapter(edu_adapter);
    }

    private void initExpRecycler() {
        // Recycler view and adapter for experience
        exp_recycler.setHasFixedSize(true);
        exp_recycler.addItemDecoration(new Utility.DividerItemDecoration(this));
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        exp_recycler.setLayoutManager(linearLayoutManager1);
        // Get place of work.
        List<String> work = new ArrayList<>();
        for (Experience experience : user.getExperiences())
            work.add(experience.getOrganisation());
        exp_adapter = new CustomAdapter(work);
        exp_recycler.setAdapter(exp_adapter);
    }

    private void init() {
        first_name = first_name_layout.getEditText();
        last_name = last_name_layout.getEditText();
        headline = headline_layout.getEditText();
        bio = bio_layout.getEditText();

        Glide.with(this).load(user.getAvatar()).apply(new RequestOptions().placeholder(R.drawable.profile_image)).into(profile_imageview);
        Glide.with(this).load(user.getAvatar_cover()).apply(new RequestOptions().placeholder(R.drawable.bg_cover)).into(cover_photo);
        first_name.setText(user.getFirstName());
        last_name.setText(user.getLastName());
        headline.setText(user.getHeadline());
        switch (user.getUserType().toUpperCase()) {
            case Constants.STUDENT:
                exp_layout.setVisibility(View.GONE);
                bio_layout.setVisibility(View.GONE);
                initEduRecycler();
                userEducation = new ArrayList<>();
                for (Education education : user.getEducation())
                    userEducation.add(education);
                Collections.copy(userEducation, user.getEducation());
                break;
            case Constants.PROFESSIONAL:
                initEduRecycler();
                initExpRecycler();
                userEducation = new ArrayList<>();
                userExperience = new ArrayList<>();
                for (Education education : user.getEducation())
                    userEducation.add(education);
                for (Experience experience : user.getExperiences())
                    userExperience.add(experience);
                Collections.copy(userEducation, user.getEducation());
                Collections.copy(userExperience, user.getExperiences());
                break;
        }
    }


    private void setUpClickListeners() {
        profile_image_edit.setOnClickListener(view -> {
            isProfileImage = true;
            openMediaDialog();
        });
        cover_photo_edit.setOnClickListener(view -> {
            isProfileImage = false;
            openMediaDialog();
        });
        save.setOnClickListener(view -> saveProfileChange());
        add_education.setOnClickListener(view -> startActivityForResult(NewEducationActivity.newIntent(this), Constants.ADD_EDUCATION));
        add_experience.setOnClickListener(view -> startActivityForResult(NewExperienceActivity.newIntent(this), Constants.ADD_EXPERIENCE));
    }


    private void saveProfileChange() {
        if (!profileChanged())
            return;

        // This is where validation of all required data is done.
        // and a new user with prefilled fields is returned.
        User user = validateData();
        if (user != null) { // Null can be returned due to an error in validate data.
            showLoading();
            if (profile_image_bitmap != null) {
                // Upload profile image to firebase and get download URL if profile image
                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(Constants.FIREBASE_PROFILE_REF + user.getFirstName() + user.getLastName() + user.getEmail() + ".jpg");
                Utility.uploadImageFirebase(storageReference, profile_image_bitmap, downloadURL -> {
                    user.setAvatar(downloadURL);
                    if (cover_image_bitmap != null) {
                        // Upload cover image to firebase and get download URL
                        Utility.uploadImageFirebase(storageReference, cover_image_bitmap, url -> {
                            user.setAvatar_cover(url);
                            presenter.updateUser(user, Application.getActiveUser().get_id().get_$oid());
                        });
                    } else {
                        presenter.updateUser(user, Application.getActiveUser().get_id().get_$oid()); // Send user information to API endpoint
                    }
                });
            } else if (cover_image_bitmap != null) {
                StorageReference storageReference = FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child(Constants.FIREBASE_PROFILE_REF + user.getFirstName() + user.getLastName() + user.getEmail() + ".jpg");
                // Upload cover image to firebase and get download URL
                Utility.uploadImageFirebase(storageReference, cover_image_bitmap, url -> {
                    user.setAvatar_cover(url);
                    presenter.updateUser(user, Application.getActiveUser().get_id().get_$oid());
                });
            } else {
                presenter.updateUser(user, Application.getActiveUser().get_id().get_$oid()); // Send user information to API endpoint
            }
        }
    }


    /**
     * Checks all fields and validates the data in fields that
     *
     * @return
     */
    private User validateData() {
        String firstName, lastName, profileHeadline;
        firstName = first_name.getText().toString();
        lastName = last_name.getText().toString();
        profileHeadline = headline.getText().toString();
        if (firstName.equals("")) {
            first_name.setError("This field is required");
            return null;
        }
        if (lastName.equals("")) {
            last_name.setError("This field is required");
            return null;
        }
        if (profileHeadline.equals("")) {
            headline.setError("This field is required");
            return null;
        }
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setHeadline(profileHeadline);

        // Initialize education and experience with new values if available
        user.setEducation(userEducation);
        user.setExperiences(userExperience);
        return user;
    }

    /**
     * Checks if the profile information has changed from the information of the profile user
     * by checking all fields with data to see if their content was altered.
     *
     * @return true if profile info has been changed and false otherwise.
     */
    private boolean profileChanged() {
        // First off check profile image and cover image to see if there is a change there
        if (profile_image_bitmap != null || cover_image_bitmap != null)
            return true;
        if (!first_name.getText().toString().equals(user.getFirstName()) ||
                !last_name.getText().toString().equals(user.getLastName()) ||
                !headline.getText().toString().equals(user.getHeadline()))
            return true;
        if (userEducation.size() != user.getEducation().size() || userExperience.size() != user.getExperiences().size())
            return true;

        return false;
    }

    /**
     * Opens a media dialog to choose a media picture from gallery or camera.
     */
    private void openMediaDialog() {
        new MaterialDialog.Builder(this)
                .items(R.array.photo_items)
                .itemsCallback((dialog, v, which, text) -> {
                    if (which == 0) {
                        // Open Camera
                        launchCamera();
                    } else {
                        // Open Gallery to choose a photo
                        launchGallery();
                    }
                })
                .show();
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
//                Log.i(Application.TAG, "Camera permission has now been granted.");
                // Launch Camera to take a picture
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, Utility.CAMERA_REQUEST_CODE);//zero can be replaced with any action codes
            } else {
//                Log.i(Application.TAG, "Camera permission was not granted ");
                // Show snackbar.
                Snackbar.make(getWindow().getDecorView().getRootView(), R.string.permission_not_granted, Snackbar.LENGTH_SHORT).show();
            }

        } else if (requestCode == Application.PermissionRequests.MY_PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(Application.TAG, "External Storage permission has now been granted");
                // Open gallery
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, Utility.GALLERY_REQUEST_CODE);
            } else {
//                Log.i(Application.TAG, "External storage permission was not granted ");
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
                    if (isProfileImage) {
                        profile_imageview.setImageBitmap(imageBitmap);
                        profile_image_bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    } else {
                        cover_photo.setImageBitmap(imageBitmap);
                        cover_image_bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                    }
                }
                break;
            case Utility.GALLERY_REQUEST_CODE: // Photo picked from gallery
                if (resultCode == RESULT_OK) {
                    Bitmap imageBitmap;
                    try {
                        imageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        if (isProfileImage) {
                            Glide.with(this).load(data.getData()).apply(new RequestOptions().placeholder(R.drawable.profile_image)).into(profile_imageview);
                            profile_image_bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                        } else {
                            Glide.with(this).load(data.getData()).apply(new RequestOptions().placeholder(R.drawable.bg_cover)).into(cover_photo);
                            cover_image_bitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, false);
                        }
                    } catch (FileNotFoundException fnfe) {
                        FirebaseCrash.log(fnfe.getMessage());
                    }
                }
                break;
            case Constants.ADD_EDUCATION:
                // Check if there is an education object in the new intent and add the item to the recycler view
                Education education = data.getParcelableExtra(Constants.EDUCATION_EXTRA);
                if (education != null) {
                    edu_adapter.addItem(education.getSchool());
                    userEducation.add(education);
                }
                break;
            case Constants.ADD_EXPERIENCE:
                // Check if there is an experience object in the data intent and add item to experience recycler view.
                Experience experience = data.getParcelableExtra(Constants.EXPERIENCE_EXTRA);
                if (experience != null) {
                    exp_adapter.addItem(experience.getOrganisation());
                    userExperience.add(experience);
                }
                break;
            default: // DO NOTHING
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Before back button is pressed, check if there is a change in profile information.
                if (profileChanged()) {
                    new MaterialDialog.Builder(this)
                            .content(R.string.unsaved_changes)
                            .positiveText(R.string.cancel)
                            .negativeText(R.string.discard)
                            .onNegative((dialog, which) -> finish())
                            .show();
                } else finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void showLoading() {
        new MaterialDialog.Builder(this)
                .content(R.string.wait)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void onProfileUpdated(User user) {
        hideLoading();
        Application.profileEdited = true; // Tag application profile edit in order to update profile information in relevant places
        finish();
    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private List<String> itemList;

        CustomAdapter(List<String> items) {
            this.itemList = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exp_edu_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.editText.setText(itemList.get(position));
        }

        @Override
        public int getItemCount() {
            if (itemList == null) {
                return 0;
            }
            return itemList.size();
        }

        public void addItem(String string) {
            this.itemList.add(string);
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            this.itemList.remove(position);
            notifyItemRemoved(position);
        }

        public void setItems(List<String> eduList) {
            this.itemList = eduList;
            notifyDataSetChanged();
        }

        public void clear() {
            this.itemList = null;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            EditText editText;

            ViewHolder(View view) {
                super(view);
                editText = view.findViewById(R.id.text_edit);
            }
        }
    }
}
package com.enipro.data.remote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

@org.parceler.Parcel
@Entity(tableName = "users")
@IgnoreExtraProperties
public class User implements AbstractUser, IUser {

    @SerializedName("_id")
    @Expose
    @NonNull
    @PrimaryKey
    @Exclude
    public ObjectId _id;

    @SerializedName("first_name")
    @Expose
    public String firstName;

    @SerializedName("last_name")
    @Expose
    public String lastName;

    @SerializedName("password")
    @Expose
    @Exclude
    public String password;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("active")
    @Expose
    @Exclude
    public boolean active;

    @SerializedName("user_type")
    @Expose
    @Exclude
    public String userType;

    @SerializedName("headline")
    @Expose
    public String headline;

    @SerializedName("country")
    @Expose
    public String country;

    @SerializedName("mobile")
    @Expose
    public String mobile;

    @SerializedName("bio")
    @Expose
    public String bio;

    @SerializedName("avatar")
    @Expose
    @Exclude
    public String avatar;

    @SerializedName("avatar_cover")
    @Expose
    @Exclude
    public String avatar_cover;

    @SerializedName("firebaseToken")
    @Expose
    public String firebaseToken;

    @SerializedName("firebaseUID")
    @Expose
    public String firebaseUID;

    @SerializedName("created_at")
    @Expose
    @Exclude
    public Date created_at;

    @SerializedName("updated_at")
    @Expose
    @Exclude
    public Date updated_at;

    @SerializedName("achievements")
    @Expose
    public String achievements;

    @SerializedName("circle")
    @Expose
    public List<UserConnection> circle = new ArrayList<>();

    @SerializedName("network")
    @Expose
    public List<UserConnection> network = new ArrayList<>();

    @SerializedName("interests")
    @Expose
    public List<String> interests = new ArrayList<>();

    @SerializedName("chat")
    @Expose
    public List<UserConnection> chats = new ArrayList<>();

    @SerializedName("saved")
    @Expose
    public List<SavedFeed> savedFeeds = new ArrayList<>();

    @SerializedName("education")
    @Expose
    public List<Education> education = new ArrayList<>();

    @SerializedName("experience")
    @Expose
    public List<Experience> experiences = new ArrayList<>();

    public User() {
    }

    @Ignore
    public User(String firstName, String lastName, String email, boolean active, String password, String userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.password = password;
        this.userType = userType;
    }

    public User(AbstractUser user) {
        _id = user.get_id();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        password = user.getPassword();
        email = user.getEmail();
        active = user.getActive();
        userType = user.getUserType();
        headline = user.getHeadline();
        country = user.getCountry();
        mobile = user.getMobile();
        bio = user.getBio();
        avatar = user.getAvatar();
        avatar_cover = user.getAvatar_cover();
        firebaseToken = user.getFirebaseToken();
        firebaseUID = user.getFirebaseUID();
        created_at = user.getCreated_at();
        updated_at = user.getUpdated_at();
        achievements = user.getAchievements();
        circle = user.getCircle();
        network = user.getNetwork();
        interests = user.getInterests();
        chats = user.getChats();
        savedFeeds = user.getSavedFeeds();
        education = user.getEducation();
        experiences = user.getExperiences();
    }

    @Override
    public String getId() {
        return this._id.get_$oid();
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }


    @Override
    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    @Override
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getAvatar_cover() {
        return avatar_cover;
    }

    public void setAvatar_cover(String avatar_cover) {
        this.avatar_cover = avatar_cover;
    }

    @Override
    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    @Override
    public String getFirebaseUID() {
        return firebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
    }

    @Override
    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    @Override
    public List<UserConnection> getCircle() {
        return circle;
    }

    public void setCircle(List<UserConnection> circle) {
        this.circle = circle;
    }

    @Override
    public List<UserConnection> getNetwork() {
        return network;
    }

    public void setNetwork(List<UserConnection> network) {
        this.network = network;
    }

    @Override
    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    @Override
    public List<UserConnection> getChats() {
        return chats;
    }

    public void setChats(List<UserConnection> chats) {
        this.chats = chats;
    }

    @Override
    public List<SavedFeed> getSavedFeeds() {
        return savedFeeds;
    }

    public void setSavedFeeds(List<SavedFeed> savedFeeds) {
        this.savedFeeds = savedFeeds;
    }


    @Override
    public List<Education> getEducation() {
        return education;
    }

    public void setEducation(List<Education> education) {
        this.education = education;
    }

    @Override
    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }
}

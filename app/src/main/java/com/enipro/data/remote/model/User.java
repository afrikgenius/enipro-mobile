package com.enipro.data.remote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
@Parcel
@Entity(tableName = "users")
public class User implements IUser {

    @SerializedName("_id")
    @Expose
    @Exclude
    @PrimaryKey
    @NotNull
    private ObjectId _id;

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("active")
    @Expose
    @Exclude
    private boolean active;
    @SerializedName("password")
    @Expose
    @Exclude
    private String password;
    @SerializedName("user_type")
    @Expose
    @Exclude
    private String userType;
    @SerializedName("headline")
    @Expose
    private String headline;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("avatar")
    @Expose
    @Exclude
    private String user_avatar;
    @SerializedName("avatar_cover")
    @Expose
    @Exclude
    private String avatar_cover;
    @SerializedName("firebaseToken")
    @Expose
    private String firebaseToken;
    @SerializedName("firebaseUID")
    @Expose
    private String firebaseUID;

    @SerializedName("created_at")
    @Expose
    @Exclude
    private Date created_at;

    @SerializedName("updated_at")
    @Expose
    @Exclude
    private Date updated_at;
    @SerializedName("achievements")
    @Expose
    private String achievements;
    @SerializedName("circle")
    @Expose
    private List circle = new ArrayList();

    @SerializedName("network")
    @Expose
    private List network = new ArrayList();

    @SerializedName("interests")
    @Expose
    private List interests = new ArrayList();

    @SerializedName("chat")
    @Expose
    private List chats = new ArrayList();

    @SerializedName("saved")
    @Expose
    private List<SavedFeed> savedFeeds = new ArrayList<>();

    @SerializedName("education")
    @Expose
    private List<Education> education = new ArrayList<>();
    @SerializedName("experience")
    @Expose
    private List<Experience> experiences = new ArrayList<>();

    public ObjectId get_id() {
        return this._id;
    }

    public void set_id(ObjectId var1) {
        this._id = var1;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String var1) {
        this.firstName = var1;
    }


    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String var1) {
        this.lastName = var1;
    }


    public String getEmail() {
        return this.email;
    }

    public void setEmail(String var1) {
        this.email = var1;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean var1) {
        this.active = var1;
    }


    public String getPassword() {
        return this.password;
    }

    public final void setPassword(String var1) {
        this.password = var1;
    }


    public String getUserType() {
        return this.userType;
    }

    public void setUserType(String var1) {
        this.userType = var1;
    }


    public String getHeadline() {
        return this.headline;
    }

    public void setHeadline(String var1) {
        this.headline = var1;
    }


    public String getCountry() {
        return this.country;
    }

    public void setCountry(String var1) {
        this.country = var1;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String var1) {
        this.mobile = var1;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String var1) {
        this.bio = var1;
    }


    public String getUser_avatar() {
        return this.user_avatar;
    }

    public void setUser_avatar(String var1) {
        this.user_avatar = var1;
    }


    public String getAvatar_cover() {
        return this.avatar_cover;
    }

    public void setAvatar_cover(String var1) {
        this.avatar_cover = var1;
    }


    public String getFirebaseToken() {
        return this.firebaseToken;
    }

    public void setFirebaseToken(String var1) {
        this.firebaseToken = var1;
    }


    public String getFirebaseUID() {
        return this.firebaseUID;
    }

    public void setFirebaseUID(String var1) {
        this.firebaseUID = var1;
    }


    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date var1) {
        this.created_at = var1;
    }


    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Date var1) {
        this.updated_at = var1;
    }

    public String getAchievements() {
        return this.achievements;
    }

    public void setAchievements(String var1) {
        this.achievements = var1;
    }

    public List getCircle() {
        return this.circle;
    }

    public void setCircle(List var1) {
        this.circle = var1;
    }

    public List getNetwork() {
        return this.network;
    }

    public void setNetwork(List var1) {
        this.network = var1;
    }

    public List getInterests() {
        return this.interests;
    }

    public void setInterests(List var1) {
        this.interests = var1;
    }

    public List getChats() {
        return this.chats;
    }

    public void setChats(List var1) {
        this.chats = var1;
    }

    public List getSavedFeeds() {
        return this.savedFeeds;
    }

    public void setSavedFeeds(List var1) {
        this.savedFeeds = var1;
    }

    public List getEducation() {
        return this.education;
    }

    public void setEducation(List var1) {
        this.education = var1;
    }

    public List getExperiences() {
        return this.experiences;
    }

    public void setExperiences(List var1) {
        this.experiences = var1;
    }

    public String getId() {
        return this._id.get_$oid();
    }

    public String getName() {
        return this.firstName + ' ' + this.lastName;
    }

    public String getAvatar() {
        return this.user_avatar;
    }

    public void setAvatar(String avatar) {
        this.user_avatar = avatar;
    }

    public User() {
    }

    public User(String first_name, String last_name, String email, String password, String user_type) {
        this.firstName = first_name;
        this.lastName = last_name;
        this.email = email;
        this.password = password;
        this.userType = user_type;
    }
}

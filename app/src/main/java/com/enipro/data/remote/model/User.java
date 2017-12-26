package com.enipro.data.remote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

@Entity(tableName = "users")
public class User implements Parcelable, AbstractUser, IUser {

    @SerializedName("_id")
    @Expose
    @NonNull
    @PrimaryKey
    private ObjectId _id;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("active")
    @Expose
    private boolean active;

    @SerializedName("user_type")
    @Expose
    private String userType;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("avatar_cover")
    @Expose
    private String avatar_cover;

    @SerializedName("created_at")
    @Expose
    private Date created_at;

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    public User(){}

    @Ignore
    public User(String firstName, String lastName, String email, boolean active, String password, String userType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
        this.password = password;
        this.userType = userType;
//        this.avatar = avatar;
//        this.avatar_cover = avatar_cover;
    }

    public User(AbstractUser user){
        _id = user.get_id();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        password = user.getPassword();
        email = user.getEmail();
        active = user.getActive();
        userType = user.getUserType();
        avatar = user.getAvatar();
        avatar_cover = user.getAvatar_cover();
        created_at = user.getCreated_at();
        updated_at = user.getUpdated_at();
    }


    public User(Parcel in){
        this._id = in.readParcelable(getClass().getClassLoader());
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.password = in.readString();
        this.email = in.readString();
        this.active = in.readByte() != 0;
        this.userType = in.readString();
        this.avatar = in.readString();
        this.avatar_cover = in.readString();
        this.created_at = in.readParcelable(getClass().getClassLoader());
        this.updated_at = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(_id, flags);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(userType);
        dest.writeString(avatar);
        dest.writeString(avatar_cover);
        dest.writeParcelable(created_at, flags);
        dest.writeParcelable(updated_at, flags);
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
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar){ this.avatar = avatar; }

    @Override
    public String getAvatar_cover(){ return avatar_cover; }
    public void setAvatar_cover(String avatar_cover){ this.avatar_cover = avatar_cover; }

    @Override
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    @Override
    public Date getUpdated_at() { return updated_at; }
    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }


    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }


        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}

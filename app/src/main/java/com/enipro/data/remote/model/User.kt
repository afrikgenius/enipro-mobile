package com.enipro.data.remote.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IUser

@Entity(tableName = "users")
@IgnoreExtraProperties
class User : IUser, Parcelable {

    @SerializedName("_id")
    @Expose
    @PrimaryKey
    @Exclude
    @android.support.annotation.NonNull
    var _id: ObjectId? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("active")
    @Expose
    @Exclude
    var active: Boolean = false

    @SerializedName("password")
    @Expose
    @Exclude
    var password: String? = null

    @SerializedName("user_type")
    @Expose
    @Exclude
    var userType: String? = null

    @SerializedName("headline")
    @Expose
    var headline: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("bio")
    @Expose
    var bio: String? = null

    @SerializedName("avatar")
    @Expose
    @Exclude
    var user_avatar: String? = null

    @SerializedName("avatar_cover")
    @Expose
    @Exclude
    var avatar_cover: String? = null

    @SerializedName("firebaseToken")
    @Expose
    var firebaseToken: String? = null

    @SerializedName("firebaseUID")
    @Expose
    var firebaseUID: String? = null

    @SerializedName("created_at")
    @Expose
    @Exclude
    var created_at: Date? = null

    @SerializedName("updated_at")
    @Expose
    @Exclude
    var updated_at: Date? = null

    @SerializedName("achievements")
    @Expose
    var achievements: String? = null

    @SerializedName("circle")
    @Expose
    var circle: List<UserConnection>? = ArrayList()

    @SerializedName("network")
    @Expose
    var network: List<UserConnection>? = ArrayList()

    @SerializedName("interests")
    @Expose
    var interests: List<String>? = ArrayList()

    @SerializedName("chat")
    @Expose
    var chats: MutableList<UserConnection> = ArrayList()

    @SerializedName("saved")
    @Expose
    var savedFeeds: List<SavedFeed>? = ArrayList()

    @SerializedName("education")
    @Expose
    var education: List<Education>? = ArrayList()

    @SerializedName("experience")
    @Expose
    var experiences: List<Experience>? = ArrayList()

    constructor(parcel: Parcel) : this() {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
        firstName = parcel.readString()
        lastName = parcel.readString()
        email = parcel.readString()
        active = parcel.readByte() != 0.toByte()
        password = parcel.readString()
        userType = parcel.readString()
        headline = parcel.readString()
        country = parcel.readString()
        mobile = parcel.readString()
        bio = parcel.readString()
        user_avatar = parcel.readString()
        avatar_cover = parcel.readString()
        firebaseToken = parcel.readString()
        firebaseUID = parcel.readString()
        created_at = parcel.readParcelable(Date::class.java.classLoader)
        updated_at = parcel.readParcelable(Date::class.java.classLoader)
        achievements = parcel.readString()
        circle = parcel.createTypedArrayList(UserConnection)
        network = parcel.createTypedArrayList(UserConnection)
        interests = parcel.createStringArrayList()

        // Parceling a Mutable List is quite difficult, so the fix is to convert to an ArrayList
        // before passing it
        chats = parcel.createTypedArrayList(UserConnection)
        savedFeeds = parcel.createTypedArrayList(SavedFeed)
        education = parcel.createTypedArrayList(Education)
        experiences = parcel.createTypedArrayList(Experience)
    }

    constructor()

    constructor(first_name: String?, last_name: String?, email: String?, password: String?, user_type: String?) {
        this.firstName = first_name
        this.lastName = last_name
        this.email = email
        this.password = password
        this.userType = user_type
    }

    override fun getId(): String {
        return this._id!!.oid
    }

    override fun getName(): String {
        return "$firstName $lastName"
    }

    override fun getAvatar(): String? {
        return user_avatar
    }

    fun setAvatar(avatar: String) {
        this.user_avatar = avatar
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_id, flags)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(email)
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeString(password)
        parcel.writeString(userType)
        parcel.writeString(headline)
        parcel.writeString(country)
        parcel.writeString(mobile)
        parcel.writeString(bio)
        parcel.writeString(user_avatar)
        parcel.writeString(avatar_cover)
        parcel.writeString(firebaseToken)
        parcel.writeString(firebaseUID)
        parcel.writeParcelable(created_at, flags)
        parcel.writeParcelable(updated_at, flags)
        parcel.writeString(achievements)
        parcel.writeTypedList(circle)
        parcel.writeTypedList(network)
        parcel.writeStringList(interests)

        // TODO Parceling a Mutable List is quite difficult, so the fix is to convert to an ArrayList
        // TODO before passing it
        // TODO If possible something else should be done here.
        parcel.writeTypedList(ArrayList(chats))

        parcel.writeTypedList(savedFeeds)
        parcel.writeTypedList(education)
        parcel.writeTypedList(experiences)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}

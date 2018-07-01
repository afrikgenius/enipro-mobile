package com.enipro.data.remote.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IUser
import java.util.*

@org.parceler.Parcel
@Entity(tableName = "users")
@IgnoreExtraProperties
class User : IUser {

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
    private var avatar: String? = null

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
    var circle: MutableList<UserConnection> = ArrayList()

    @SerializedName("network")
    @Expose
    var network: MutableList<UserConnection> = ArrayList()

    @SerializedName("interests")
    @Expose
    var interests: MutableList<String> = ArrayList()

    @SerializedName("chat")
    @Expose
    var chats: MutableList<UserConnection> = ArrayList()

    @SerializedName("saved")
    @Expose
    var savedFeeds: MutableList<SavedFeed> = ArrayList()

    @SerializedName("education")
    @Expose
    var education: MutableList<Education> = ArrayList()

    @SerializedName("experience")
    @Expose
    var experiences: MutableList<Experience> = ArrayList()

    constructor() {}

    constructor(first_name: String?, last_name: String?, email: String?, password: String?, user_type: String?) {
        this.firstName = first_name
        this.lastName = last_name
        this.email = email
        this.password = password
        this.userType = user_type
    }

    override fun getId(): String {
        return this._id!!.`_$oid`
    }

    override fun getName(): String {
        return "$firstName $lastName"
    }

    override fun getAvatar(): String? {
        return avatar
    }

    fun setAvatar(avatar: String) {
        this.avatar = avatar
    }

}

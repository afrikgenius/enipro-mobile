package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Represents a news feed item in the application.
 */

@Entity(tableName = "feeds")
class Feed() : Parcelable {

    @SerializedName("_id")
    @Expose
    @PrimaryKey
    @android.support.annotation.NonNull
    var _id: ObjectId? = null

    @SerializedName("user_id")
    @Expose
    var user: String? = null

    @SerializedName("likes")
    @Expose
    var likes: List<UserConnection> = ArrayList()

    @SerializedName("moderated")
    @Expose
    var moderated: Boolean = false

    @SerializedName("content")
    @Expose
    var content: FeedContent? = null

    @SerializedName("comments")
    @Expose
    var comments: MutableList<FeedComment> = ArrayList()

    @SerializedName("tags")
    @Expose
    var tags: List<String>? = null

    @SerializedName("premium")
    @Expose
    var premiumDetails: PremiumDetails? = null

    @SerializedName("created_at")
    @Expose
    var created_at: Date? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at: Date? = null

    constructor(parcel: Parcel) : this() {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
        user = parcel.readString()
        moderated = parcel.readByte() != 0.toByte()
        tags = parcel.createStringArrayList()
        premiumDetails = parcel.readParcelable(PremiumDetails::class.java.classLoader)
        created_at = parcel.readParcelable(Date::class.java.classLoader)
        updated_at = parcel.readParcelable(Date::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_id, flags)
        parcel.writeString(user)
        parcel.writeByte(if (moderated) 1 else 0)
        parcel.writeStringList(tags)
        parcel.writeParcelable(premiumDetails, flags)
        parcel.writeParcelable(created_at, flags)
        parcel.writeParcelable(updated_at, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Feed> {
        override fun createFromParcel(parcel: Parcel): Feed {
            return Feed(parcel)
        }

        override fun newArray(size: Int): Array<Feed?> {
            return arrayOfNulls(size)
        }
    }

}
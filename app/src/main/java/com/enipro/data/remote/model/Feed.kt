package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Represents a news feed item in the application.
 */

@org.parceler.Parcel
@Entity(tableName = "feeds")
class Feed {

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

}
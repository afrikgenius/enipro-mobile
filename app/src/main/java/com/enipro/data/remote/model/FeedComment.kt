package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
@Entity(tableName = "feed_comments")
class FeedComment {

    @SerializedName("_id")
    @Expose
    var _id: ObjectId? = null

    @SerializedName("comment_user_id")
    @Expose
    var user: String? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("comment_image")
    @Expose
    var comment_image: String? = null

    @SerializedName("created_at")
    @Expose
    var created_at: Date? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at: Date? = null


    /**
     * Returns a name used to save the comment image file.
     *
     * @return
     */
    val imageName: String
        get() = user + java.util.Random().nextLong() + java.util.Date()
}

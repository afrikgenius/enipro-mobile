package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "feed_comments")
class FeedComment() : Parcelable {

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

    constructor(parcel: Parcel) : this() {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
        user = parcel.readString()
        comment = parcel.readString()
        comment_image = parcel.readString()
        created_at = parcel.readParcelable(Date::class.java.classLoader)
        updated_at = parcel.readParcelable(Date::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_id, flags)
        parcel.writeString(user)
        parcel.writeString(comment)
        parcel.writeString(comment_image)
        parcel.writeParcelable(created_at, flags)
        parcel.writeParcelable(updated_at, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedComment> {
        override fun createFromParcel(parcel: Parcel): FeedComment {
            return FeedComment(parcel)
        }

        override fun newArray(size: Int): Array<FeedComment?> {
            return arrayOfNulls(size)
        }
    }
}

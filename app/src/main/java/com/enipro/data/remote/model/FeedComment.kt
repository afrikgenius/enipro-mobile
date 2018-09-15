package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.util.DiffUtil
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "feed_comments")
class FeedComment() : Parcelable {

    @SerializedName("_id")
    @Expose
    var _id: ObjectId? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("likes")
    @Expose
    var likes: MutableList<String> = ArrayList()

    @SerializedName("likes_count")
    @Expose
    var likesCount: Int? = null

    @SerializedName("comment_image")
    @Expose
    var comment_image: String? = null

    @SerializedName("created_at")
    @Expose
    var created_at: Date? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at: Date? = null

    @SerializedName("user")
    @Expose
    var user: User? = null


    /**
     * Returns a name used to save the comment image file.
     *
     * @return
     */
    val imageName: String
        get() = user?.id + java.util.Random().nextLong() + java.util.Date()

    constructor(parcel: Parcel) : this() {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
        comment = parcel.readString()
        comment_image = parcel.readString()
        likes = parcel.createStringArrayList()
        likesCount = parcel.readInt()
        user = parcel.readParcelable(User::class.java.classLoader)
        created_at = parcel.readParcelable(Date::class.java.classLoader)
        updated_at = parcel.readParcelable(Date::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_id, flags)
        parcel.writeString(comment)
        parcel.writeString(comment_image)
        parcel.writeStringList(likes)
        parcel.writeInt(likesCount as Int)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(created_at, flags)
        parcel.writeParcelable(updated_at, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeedComment>() {
            override fun areContentsTheSame(oldItem: FeedComment?, newItem: FeedComment?): Boolean {
                return oldItem!!.equals(newItem)
            }

            override fun areItemsTheSame(oldItem: FeedComment?, newItem: FeedComment?): Boolean {
                return oldItem?._id == newItem?._id
            }
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<FeedComment> {

            override fun createFromParcel(parcel: Parcel): FeedComment {
                return FeedComment(parcel)
            }

            override fun newArray(size: Int): Array<FeedComment?> {
                return arrayOfNulls(size)
            }
        }
    }
}

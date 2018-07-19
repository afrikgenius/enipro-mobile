package com.enipro.data.remote.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.enipro.Application
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "feed_content")
class FeedContent() : Parcelable {

    @PrimaryKey
    var content_id = Random().nextInt()

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("video")
    @Expose
    var video: String? = null

    @SerializedName("doc")
    @Expose
    var doc: Document? = null

    var isDocExists = false
    var mediaType: Int = 0

    val mediaName: String
        get() = Application.getActiveUser().id + java.util.Random().nextLong() + java.util.Date()

    constructor(parcel: Parcel) : this() {
        content_id = parcel.readInt()
        text = parcel.readString()
        image = parcel.readString()
        video = parcel.readString()
        doc = parcel.readParcelable(Document::class.java.classLoader)
        isDocExists = parcel.readByte() != 0.toByte()
        mediaType = parcel.readInt()
    }

    object MediaType {
        val IMAGE = 0x005
        val VIDEO = 0x002
        val DOC = 0x003
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(content_id)
        parcel.writeString(text)
        parcel.writeString(image)
        parcel.writeString(video)
        parcel.writeParcelable(doc, flags)
        parcel.writeByte(if (isDocExists) 1 else 0)
        parcel.writeInt(mediaType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FeedContent> {
        override fun createFromParcel(parcel: Parcel): FeedContent {
            return FeedContent(parcel)
        }

        override fun newArray(size: Int): Array<FeedContent?> {
            return arrayOfNulls(size)
        }
    }
}

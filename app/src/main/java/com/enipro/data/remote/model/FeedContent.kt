package com.enipro.data.remote.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

import com.enipro.Application
import com.enipro.db.EniproDatabase
import com.google.firebase.database.Exclude
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.util.ArrayList
import java.util.Random

@org.parceler.Parcel
@Entity(tableName = "feed_content")
class FeedContent {

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

    object MediaType {
        val IMAGE = 0x005
        val VIDEO = 0x002
        val DOC = 0x003
    }
}

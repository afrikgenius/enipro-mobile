package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@org.parceler.Parcel
@Entity(tableName = "requests")
@IgnoreExtraProperties
class Request {

    @SerializedName("_id")
    @Expose
    @PrimaryKey
    @android.support.annotation.NonNull
    var _id: ObjectId? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("sender")
    @Expose
    var sender: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("category ")
    @Expose
    var category: String? = null

    @SerializedName("recipient")
    @Expose
    var recipient: String? = null

    @SerializedName("created_at")
    @Expose
    @Exclude
    var created_at: Date? = null

    @SerializedName("updated_at")
    @Expose
    @Exclude
    var updated_at: Date? = null

    @SerializedName("schedule")
    @Expose
    var schedule: SessionSchedule? = null

    constructor(sender: String?, type: String?, category: String?) {
        this.sender = sender
        this.type = type
        this.category = category
    }

    constructor() {}
}
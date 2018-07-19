package com.enipro.data.remote.model


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "requests")
@IgnoreExtraProperties
class Request : Parcelable {

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

    constructor(parcel: Parcel) : this() {
        _id = parcel.readParcelable(ObjectId::class.java.classLoader)
        status = parcel.readString()
        sender = parcel.readString()
        type = parcel.readString()
        category = parcel.readString()
        recipient = parcel.readString()
        created_at = parcel.readParcelable(Date::class.java.classLoader)
        updated_at = parcel.readParcelable(Date::class.java.classLoader)
    }

    constructor(sender: String?, type: String?, category: String?) {
        this.sender = sender
        this.type = type
        this.category = category
    }

    constructor()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(_id, flags)
        parcel.writeString(status)
        parcel.writeString(sender)
        parcel.writeString(type)
        parcel.writeString(category)
        parcel.writeString(recipient)
        parcel.writeParcelable(created_at, flags)
        parcel.writeParcelable(updated_at, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Request> {
        override fun createFromParcel(parcel: Parcel): Request {
            return Request(parcel)
        }

        override fun newArray(size: Int): Array<Request?> {
            return arrayOfNulls(size)
        }
    }
}
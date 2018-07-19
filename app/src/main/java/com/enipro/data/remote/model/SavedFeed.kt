package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SavedFeed(@field:SerializedName("id")
                     @field:Expose var feedId: String) : Parcelable {

    @SerializedName("created_at")
    @Expose
    var createdAt: Date? = null

    constructor(parcel: Parcel) : this(parcel.readString()) {
        createdAt = parcel.readParcelable(Date::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(feedId)
        parcel.writeParcelable(createdAt, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SavedFeed> {
        override fun createFromParcel(parcel: Parcel): SavedFeed {
            return SavedFeed(parcel)
        }

        override fun newArray(size: Int): Array<SavedFeed?> {
            return arrayOfNulls(size)
        }
    }

}

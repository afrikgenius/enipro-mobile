package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserConnection(
        @field:SerializedName("id")
        @field:Expose var userId: String) : Parcelable {

    @SerializedName("created_at")
    @Expose
    var createdAt: Date? = null

    constructor(parcel: Parcel) : this(parcel.readString()) {
        createdAt = parcel.readParcelable(Date::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeParcelable(createdAt, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserConnection> {
        override fun createFromParcel(parcel: Parcel): UserConnection {
            return UserConnection(parcel)
        }

        override fun newArray(size: Int): Array<UserConnection?> {
            return arrayOfNulls(size)
        }
    }
}

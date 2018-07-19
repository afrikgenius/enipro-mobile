package com.enipro.data.remote.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PremiumDetails
constructor(@field:SerializedName("accountNumber")
            @field:Expose
            var accountNumber: String,
            @field:SerializedName("bank")
            @field:Expose
            var bank: String,
            @field:SerializedName("payment_amount")
            @field:Expose
            var payment_amount: Int) : Parcelable {

    @SerializedName("payment_code")
    @Expose
    var payment_code: String? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
        payment_code = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accountNumber)
        parcel.writeString(bank)
        parcel.writeInt(payment_amount)
        parcel.writeString(payment_code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PremiumDetails> {
        override fun createFromParcel(parcel: Parcel): PremiumDetails {
            return PremiumDetails(parcel)
        }

        override fun newArray(size: Int): Array<PremiumDetails?> {
            return arrayOfNulls(size)
        }
    }

}

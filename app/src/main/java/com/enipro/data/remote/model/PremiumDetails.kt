package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.ParcelConstructor
import org.parceler.ParcelProperty

@org.parceler.Parcel
data class PremiumDetails @ParcelConstructor
constructor(@field:SerializedName("accountNumber")
            @field:Expose
            var accountNumber: String, @field:SerializedName("bank")
            @field:Expose
            var bank: String, @param:ParcelProperty("amount") @field:SerializedName("payment_amount")
            @field:Expose
            @get:ParcelProperty("amount")
            var payment_amount: Int) {

    @SerializedName("payment_code")
    @Expose
    var payment_code: String? = null

}

package com.enipro.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;
import org.parceler.ParcelProperty;

@org.parceler.Parcel
public class PremiumDetails {


    @SerializedName("accountNumber")
    @Expose
    public String accountNumber;

    @SerializedName("bank")
    @Expose
    public String bank;

    @SerializedName("payment_amount")
    @Expose
    public int payment_amount;

    @SerializedName("payment_code")
    @Expose
    public String payment_code;

    @ParcelConstructor
    public PremiumDetails(String accountNumber, String bank, @ParcelProperty("amount") int amount) {
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.payment_amount = amount;
    }

//    public PremiumDetails(Parcel in) {
//        this.payment_amount = in.readInt();
//        this.accountNumber = in.readString();
//        this.bank = in.readString();
//        this.payment_code = in.readString();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(payment_amount);
//        dest.writeString(accountNumber);
//        dest.writeString(bank);
//        dest.writeString(payment_code);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    @ParcelProperty("amount")
    public int getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(int payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(String payment_code) {
        this.payment_code = payment_code;
    }

//    public static final Parcelable.Creator<PremiumDetails> CREATOR = new Parcelable.Creator<PremiumDetails>() {
//
//        @Override
//        public PremiumDetails createFromParcel(Parcel source) {
//            return new PremiumDetails(source);
//        }
//
//        @Override
//        public PremiumDetails[] newArray(int size) {
//            return new PremiumDetails[size];
//        }
//    };
}

package com.enipro.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ObjectId implements Parcelable {

    @SerializedName("$oid")
    @Expose
    private String $oid;

    public ObjectId(Parcel in){
        this.$oid = in.readString();
    }

    public ObjectId(String $oid){this.$oid = $oid;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString($oid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String get_$oid() {
        return $oid;
    }

    public void set_$oid(String $oid) {
        this.$oid = $oid;
    }

    public static final Parcelable.Creator<ObjectId> CREATOR = new Parcelable.Creator<ObjectId>() {
        @Override
        public ObjectId createFromParcel(Parcel source) {
            return new ObjectId(source);
        }

        @Override
        public ObjectId[] newArray(int size) {
            return new ObjectId[size];
        }
    };
}

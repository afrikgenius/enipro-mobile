package com.enipro.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.ParcelConstructor;

@org.parceler.Parcel
public class ObjectId {

    @SerializedName("$oid")
    @Expose
    public String $oid;

//    public ObjectId(Parcel in){
//        this.$oid = in.readString();
//    }

    @ParcelConstructor
    public ObjectId(String $oid){this.$oid = $oid;}

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString($oid);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }

    public String get_$oid() {
        return $oid;
    }

    public void set_$oid(String $oid) {
        this.$oid = $oid;
    }

//    public static final Parcelable.Creator<ObjectId> CREATOR = new Parcelable.Creator<ObjectId>() {
//        @Override
//        public ObjectId createFromParcel(Parcel source) {
//            return new ObjectId(source);
//        }
//
//        @Override
//        public ObjectId[] newArray(int size) {
//            return new ObjectId[size];
//        }
//    };
}

package com.enipro.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.parceler.ParcelConstructor

@org.parceler.Parcel
data class ObjectId @ParcelConstructor
constructor(@field:SerializedName("\$oid")
            @field:Expose
            var `_$oid`: String)

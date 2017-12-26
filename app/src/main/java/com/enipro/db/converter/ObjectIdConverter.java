package com.enipro.db.converter;


import android.arch.persistence.room.TypeConverter;

import com.enipro.data.remote.model.ObjectId;

public class ObjectIdConverter {

    @TypeConverter
    public static String toString(ObjectId objectId){
        return objectId.get_$oid();
    }

    @TypeConverter
    public static ObjectId toObjectId(String $oid){
        return new ObjectId($oid);
    }
}

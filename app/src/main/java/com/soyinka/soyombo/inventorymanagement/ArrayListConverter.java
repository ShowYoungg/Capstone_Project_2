package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SHOW on 8/24/2018.
 */

public class ArrayListConverter {

    static Gson gson = new Gson();

    @TypeConverter
    public static ArrayList<ProductCategory> stringToObjectList(String data){
        if (data ==null) {
            return null;
        }

        Type listType = new TypeToken<ArrayList<ProductCategory>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String objectListToString(ArrayList<ProductCategory> arrayList){
        return gson.toJson(arrayList);
    }

    @TypeConverter
    public static Date toDate(Long timeStamp){
        return timeStamp == null ? null : new Date(timeStamp);
    }

    @TypeConverter
    public static Long toTimeStamp(Date date){
        return date == null ? null : date.getTime();
    }
}

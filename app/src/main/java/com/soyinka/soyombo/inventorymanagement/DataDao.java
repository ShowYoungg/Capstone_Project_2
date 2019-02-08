package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHOW on 8/24/2018.
 */

@Dao
public interface DataDao {

    @Query("SELECT * FROM Data ORDER BY id")
    LiveData<List<ProductCategory>> loadAllData();

    @Query("SELECT * FROM Data ORDER BY id")
    List<ProductCategory> loadData();

    @Insert
    void insertData(ProductCategory productCategory);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateData(ProductCategory productCategory);

    @Delete
    void deleteData(ProductCategory productCategory);

    @Query("SELECT * FROM Data WHERE id= :id")
    LiveData<ProductCategory> loadById(int id);

    @Query("SELECT * FROM Data WHERE productName= :productName")
    LiveData<ProductCategory> loadByName(String productName);
}

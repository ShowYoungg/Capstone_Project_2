package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by SHOW on 12/29/2018.
 */

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM TransactionData ORDER BY id")
    LiveData<List<Transaction>> loadAllData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(Transaction transaction);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateData(Transaction transaction);

    @Delete
    void deleteData(Transaction transaction);

    @Query("SELECT * FROM TransactionData WHERE productName = :productName")
    List<Transaction> loadByProductName(String productName);

    @Query("SELECT * FROM TransactionData WHERE id= :id")
    Transaction loadById(int id);

    @Query("SELECT * FROM TransactionData WHERE dat BETWEEN :from AND :to")
    List<Transaction> loadTransactionByDate(Date from, Date to);
}
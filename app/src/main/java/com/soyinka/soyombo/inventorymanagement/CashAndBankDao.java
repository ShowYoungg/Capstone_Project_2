package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by SHOW on 1/14/2019.
 */


@Dao
public interface CashAndBankDao {

    @Query("SELECT * FROM Cash_And_Bank ORDER BY id")
    LiveData<List<CashAndBank>> loadFundData();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFund(CashAndBank cashAndBank);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFund(CashAndBank cashAndBank);

    @Delete
    void deleteFund(CashAndBank cashAndBank);

    @Query("SELECT * FROM Cash_And_Bank WHERE id= :id")
    CashAndBank loadFundById(int id);

    @Query("SELECT * FROM Cash_And_Bank WHERE dat BETWEEN :from AND :to")
    CashAndBank loadFundByDate(Date from, Date to);
}
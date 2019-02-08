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
 * Created by SHOW on 1/15/2019.
 */


@Dao
public interface ExpensesDao {

    @Query("SELECT * FROM SalesCost_And_Expenses ORDER BY id")
    LiveData<List<SalesCostAndExpenses>> loadAllExpense();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExpenses(SalesCostAndExpenses salesCostAndExpenses);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExpenses(SalesCostAndExpenses salesCostAndExpenses);

    @Delete
    void deleteExpenses(SalesCostAndExpenses salesCostAndExpenses);

    @Query("SELECT * FROM SalesCost_And_Expenses WHERE id= :id")
    SalesCostAndExpenses loadExpensesById(int id);

    @Query("SELECT * FROM SalesCost_And_Expenses WHERE dat BETWEEN :from AND :to")
    List<SalesCostAndExpenses> loadExpensesByDate(Date from, Date to);
}
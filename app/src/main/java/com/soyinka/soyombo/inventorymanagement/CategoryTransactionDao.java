package com.soyinka.soyombo.inventorymanagement;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by SHOW on 12/30/2018.
 */

@Dao
public interface CategoryTransactionDao {

    @Query("SELECT * FROM TransactionData, Data"
            + " WHERE Data.id = TransactionData.userId  ")
    LiveData<List<Transaction>> loadCTData();
}

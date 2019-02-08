package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;


/**
 * Created by SHOW on 8/24/2018.
 */

@Database(entities = {ProductCategory.class, Transaction.class, CategoryTransaction.class,
        CashAndBank.class, SalesCostAndExpenses.class},
        version = 3, exportSchema = false)
@TypeConverters(ArrayListConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "Inventory Database";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
///////////////////////******************QUERIES SHOULD NEVER BE MADE ON MAIN THREAD************************************//////////////////
                        //.allowMainThreadQueries()
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract DataDao dataDao();
    public abstract TransactionDao transactionDao();
    public abstract CategoryTransactionDao categoryTransactionDao();
    public abstract CashAndBankDao cashAndBankDao();
    public abstract ExpensesDao expensesDao();
}


package com.soyinka.soyombo.inventorymanagement;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by SHOW on 12/21/2018.
 */

public class ProductCategoryViewModel extends AndroidViewModel {

    private LiveData<List<ProductCategory>> productTask;
    private LiveData<List<Transaction>> productTransaction;
    private LiveData<List<Transaction>> categoryTransaction;
    private LiveData<List<Transaction>> transactionByName;
    private LiveData<List<CashAndBank>> cashAndBank;
    private LiveData<List<SalesCostAndExpenses>> salesCostAndExpenses;
    private int id;

    public ProductCategoryViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        productTask = database.dataDao().loadAllData();
        productTransaction = database.transactionDao().loadAllData();
        categoryTransaction = database.categoryTransactionDao().loadCTData();
        cashAndBank = database.cashAndBankDao().loadFundData();
        salesCostAndExpenses = database.expensesDao().loadAllExpense();
    }

    public LiveData<List<ProductCategory>> getProductTask(){return productTask;}
    public LiveData<List<Transaction>> getProductTransaction(){return productTransaction;}
    public LiveData<List<Transaction>> getCategoryTransaction() {return categoryTransaction;}
    public LiveData<List<CashAndBank>> getCashAndBank() {return cashAndBank;}
    public LiveData<List<SalesCostAndExpenses>> getExpenses() {return salesCostAndExpenses;}
}

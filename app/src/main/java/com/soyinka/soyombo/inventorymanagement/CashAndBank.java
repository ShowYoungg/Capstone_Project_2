package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by SHOW on 1/9/2019.
 */


@Entity(tableName = "Cash_And_Bank")
public class CashAndBank {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String description;
    private String transactionFund;
    private String transactionType;
    private String amount;
    private double balance;
    private Date dat;

    @Ignore
    public CashAndBank(){
        this.date = date;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionFund = transactionFund;
        this.amount = amount;
        this.balance = balance;
        this.dat = dat;
    }

    public CashAndBank(int id, String date, String description, String transactionType,
                       String transactionFund, String amount, double balance, Date dat){
        this.id = id;
        this.date = date;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionFund = transactionFund;
        this.amount = amount;
        this.balance = balance;
        this.dat = dat;
    }


    public Date getDat(){return dat;}
    public void setDat(Date dat){this.dat = dat;}
    public double getBalance(){return balance;}
    public void setBalance(double balance){this.balance = balance;}
    public  int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public  String getDate() {
        return date;
    }
    public void   setDate(String date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }
    public void   setDescription(String description) {
        this.description = description;
    }
    public String getTransactionFund() {
        return transactionFund;
    }
    public void   setTransactionFund(String transactionFund) {
        this.transactionFund = transactionFund;
    }
    public String getTransactionType() {
        return transactionType;
    }

    public void   setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

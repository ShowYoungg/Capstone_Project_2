package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by SHOW on 1/15/2019.
 */


@Entity(tableName = "SalesCost_And_Expenses")
public class SalesCostAndExpenses {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String description;
    private String amount;
    private String nature;
    private Date dat;

    @Ignore
    public SalesCostAndExpenses(){
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.nature = nature;
        this.dat = dat;
    }

    public SalesCostAndExpenses(int id, String date, String description, String amount, String nature, Date dat){
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.nature = nature;
        this.dat = dat;
    }


    public Date getDat(){return dat;}
    public void setDat(Date dat){this.dat = dat;}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

}

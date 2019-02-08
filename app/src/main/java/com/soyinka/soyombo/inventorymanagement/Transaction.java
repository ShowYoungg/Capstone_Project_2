package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by SHOW on 12/29/2018.
 */


@Entity(foreignKeys = @ForeignKey(entity = ProductCategory.class, parentColumns = "id",
        childColumns = "userId", onDelete = CASCADE),
        tableName = "TransactionData", indices = {@Index("userId")})
public class Transaction implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int userId;
    private String date;
    private String supplier;
    private String quantity;
    private String salesPrice;
    private String costPrice;

    private String transactionType;
    private String transactionFund;

    private int totalPurchases;
    private int totalSales;

    private String productName;
    private String totalAmount;
    private Date dat;



    @Ignore
    public Transaction(){
        this.date = date;
        this.supplier = supplier;
        this.quantity = quantity;
        this.salesPrice = salesPrice;
        this.costPrice = costPrice;
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionFund = transactionFund;
        this.totalPurchases = totalPurchases;
        this.totalSales = totalSales;
        this.productName = productName;
        this.totalAmount = totalAmount;
        this.dat = dat;
    }

    public Transaction(int id, String date, String supplier,
                       String quantity, String salesPrice, String costPrice,
                       int userId, String transactionType, String transactionFund,
                       int totalPurchases, int totalSales, String productName,
                       String totalAmount, Date dat){
        this.id = id;
        this.date = date;
        this.supplier = supplier;
        this.quantity = quantity;
        this.salesPrice = salesPrice;
        this.costPrice = costPrice;
        this.userId = userId;
        this.transactionType = transactionType;
        this.transactionFund = transactionFund;
        this.totalPurchases = totalPurchases;
        this.totalSales = totalSales;
        this.productName = productName;
        this.totalAmount = totalAmount;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionFund() {
        return transactionFund;
    }

    public void setTransactionFund(String transactionFund) {
        this.transactionFund = transactionFund;
    }

    public int getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(int totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.userId);
        dest.writeString(this.date);
        dest.writeString(this.supplier);
        dest.writeString(this.quantity);
        dest.writeString(this.salesPrice);
        dest.writeString(this.costPrice);
        dest.writeString(this.transactionType);
        dest.writeString(this.transactionFund);
        dest.writeInt(this.totalPurchases);
        dest.writeInt(this.totalSales);
        dest.writeString(this.productName);
        dest.writeString(this.totalAmount);
    }

    protected Transaction(Parcel in) {
        this.id = in.readInt();
        this.userId = in.readInt();
        this.date = in.readString();
        this.supplier = in.readString();
        this.quantity = in.readString();
        this.salesPrice = in.readString();
        this.costPrice = in.readString();
        this.transactionType = in.readString();
        this.transactionFund = in.readString();
        this.totalPurchases = in.readInt();
        this.totalSales = in.readInt();
        this.productName = in.readString();
        this.totalAmount = in.readString();
    }

    public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}

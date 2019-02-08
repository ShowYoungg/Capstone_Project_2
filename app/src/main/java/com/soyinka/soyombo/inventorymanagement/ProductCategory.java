package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by SHOW on 12/21/2018.
 */

@Entity(tableName = "Data")
public class ProductCategory {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String productName;
    private String productDescription;
    private String productCode;
    private String imagePath;
    private int reorderLevel;

    @Ignore
    public ProductCategory(){
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCode = productCode;
        this.imagePath = imagePath;
        this.reorderLevel = reorderLevel;
    }

    public ProductCategory(int id, String productName, String productDescription,
                           String productCode, String imagePath, int reorderLevel){
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productCode = productCode;
        this.imagePath = imagePath;
        this.reorderLevel = reorderLevel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

}
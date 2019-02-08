package com.soyinka.soyombo.inventorymanagement;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by SHOW on 12/30/2018.
 */

@Entity
public class CategoryTransaction {

    @PrimaryKey
    int idx;
    @Embedded(prefix = "user_") ProductCategory productCategory;
    @Embedded Transaction transaction;
}

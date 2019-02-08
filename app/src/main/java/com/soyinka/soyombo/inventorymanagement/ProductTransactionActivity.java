package com.soyinka.soyombo.inventorymanagement;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class ProductTransactionActivity extends AppCompatActivity implements
        InventoryManagementActivity.onProductTransactionClickListener{
    private String imagePath, productName;
    private int anInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_transaction);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (savedInstanceState == null){
            Intent i = getIntent();
            if (i != null){
                imagePath = i.getStringExtra("ImagePath");
                productName = i.getStringExtra("ProductName");
                anInt = i.getIntExtra("switchcounter", 0);
                if (anInt == 45){
                    Transaction t = i.getParcelableExtra("Transaction");
                    Bundle b = new Bundle();
                    b.putParcelable("Transaction", t);
                    b.putInt("Position", anInt);
                    ProductTransactionFragment productTransactionFragment = new ProductTransactionFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    productTransactionFragment.setArguments(b);
                    fragmentManager.beginTransaction().replace(R.id.product_frag_container,
                            productTransactionFragment).commit();
                } else {
                    Bundle b = new Bundle();
                    b.putString("ImagePath", imagePath);
                    b.putString("ProductName", productName);
                    b.putInt("Position", -1);
                    ProductTransactionFragment productTransactionFragment = new ProductTransactionFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    productTransactionFragment.setArguments(b);
                    fragmentManager.beginTransaction().add(R.id.product_frag_container,
                            productTransactionFragment).commit();
                }
            }
        }
    }


    @Override
    public void onProductSelected(int clickedPosition, ProductCategory p) {

        imagePath = p.getImagePath();
        productName = p.getProductName();

        Bundle b = new Bundle();
        b.putString("ImagePath", imagePath);
        b.putString("ProductName", productName);
        b.putInt("Position", clickedPosition);
        ProductTransactionFragment productTransactionFragment = new ProductTransactionFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        productTransactionFragment.setArguments(b);
        fragmentManager.beginTransaction().replace(R.id.product_frag_container,
                productTransactionFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}

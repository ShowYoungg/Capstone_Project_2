package com.soyinka.soyombo.inventorymanagement;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by SHOW on 12/23/2018.
 */

public class ProductCategoryAdapter extends ArrayAdapter<ProductCategory> {
    private String s;
    private int totalPurchases, totalSales;
    private SharedPreferences mSharedPreferences;
    private boolean mTwoPanes;
    InventoryManagementActivity.onProductTransactionClickListener mCallback;


    public ProductCategoryAdapter(@NonNull Context context,
                                  List<ProductCategory> product, boolean mTwoPane,
                                  InventoryManagementActivity
                                          .onProductTransactionClickListener listener) {
        super(context, 0, product);
        mCallback = listener;
        mTwoPanes = mTwoPane;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ProductCategory p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        final ImageView productImage = convertView.findViewById(R.id.imageView2);
        TextView productName = convertView.findViewById(R.id.product_name1);
        TextView productCode = convertView.findViewById(R.id.product_code1);
        TextView productDescription = convertView.findViewById(R.id.product_description1);
        TextView totalInventory = convertView.findViewById(R.id.inv_total);
        TextView totalIncomingStock = convertView.findViewById(R.id.incoming_stock);
        TextView totalOutgoingStock = convertView.findViewById(R.id.outgoing_stock);
        LinearLayout linearLayout = convertView.findViewById(R.id.stock_replenishment_notice);

        String nameOfProduct = null;
        if (p != null) {
            productName.setText(p.getProductName());
            productCode.setText(p.getProductCode());
            productDescription.setText(p.getProductDescription());
            productImage.setImageURI(Uri.parse(p.getImagePath()));

            nameOfProduct = p.getProductName();
            Log.i("NAME OF PRODUCT", "" + nameOfProduct);
            InventoryManagementActivity.getSharedPreferencesData(getContext(), p.getProductName(),
                    totalInventory, totalIncomingStock, totalOutgoingStock, convertView, p.getReorderLevel(), linearLayout);
        }

        final String finalNameOfProduct = nameOfProduct;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (p != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation((Activity) getContext(),
                                productImage, productImage.getTransitionName()).toBundle();

                        if (!mTwoPanes) {
                            getContext().startActivity(new Intent(getContext(),
                                    ProductTransactionActivity.class)
                                    .putExtra("ImagePath", p.getImagePath())
                                    .putExtra("ProductName", finalNameOfProduct), bundle);

                            Log.i("NAME OF PRODUCT2", " " + finalNameOfProduct);
                        } else {
                            Toast.makeText(getContext(), "from mTwoPane", Toast.LENGTH_SHORT).show();

                            Bundle b = new Bundle();
                            b.putString("ImagePath", p.getImagePath());
                            b.putString("ProductName", finalNameOfProduct);
                            b.putInt("Position", 0);
                            ProductTransactionFragment productTransactionFragment = new ProductTransactionFragment();
                            FragmentManager fragmentManager =
                                    ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                            productTransactionFragment.setArguments(b);
                            fragmentManager.beginTransaction().replace(R.id.product_frag_container,
                                    productTransactionFragment).commit();
                        }

                    } else {
                        if (!mTwoPanes) {
                            getContext().startActivity(new Intent(getContext(),
                                    ProductTransactionActivity.class)
                                    .putExtra("ImagePath", p.getImagePath())
                                    .putExtra("ProductName", finalNameOfProduct));

                            Log.i("NAME OF PRODUCT2", " " + finalNameOfProduct);
                        } else {
                            Toast.makeText(getContext(), "from mTwoPane", Toast.LENGTH_SHORT).show();

                            Bundle b = new Bundle();
                            b.putString("ImagePath", p.getImagePath());
                            b.putString("ProductName", finalNameOfProduct);
                            b.putInt("Position", 0);
                            ProductTransactionFragment productTransactionFragment = new ProductTransactionFragment();
                            FragmentManager fragmentManager =
                                    ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                            productTransactionFragment.setArguments(b);
                            fragmentManager.beginTransaction().replace(R.id.product_frag_container,
                                    productTransactionFragment).commit();
                        }
                    }
                }
            }
        });

        return convertView;
    }
}

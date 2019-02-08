package com.soyinka.soyombo.inventorymanagement;

import android.appwidget.AppWidgetManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;


public class StackRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewFactory(this.getApplicationContext());
    }
}

class StackRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;
    private int totalPurchase, totalSales, totalRemaining;
    public static List<ProductCategory> categoryList;
    AppDatabase productDB;


    public StackRemoteViewFactory(Context context){
        mContext = context;
        //mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        categoryList = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        //getUpdate(categoryList);
        productDB = Room.databaseBuilder(mContext.getApplicationContext(), AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                categoryList = productDB.dataDao().loadData();
            }
        });

    }


    @Override
    public void onDestroy() {
        categoryList.clear();
    }

    @Override
    public int getCount() {
        if (categoryList != null){
        return categoryList.size();
        } else {
            return 1;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews( mContext.getPackageName(), R.layout.inventory_summary_widget);
        ArrayList<Integer> list = new ArrayList<>();

        String productName = null, productCode = null, description = null;
        if (position <= getCount()){
            if (categoryList != null && categoryList.size() >= 1){
                productName = categoryList.get(position).getProductName();

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(mContext.getApplicationContext());

                totalPurchase = sharedPreferences.getInt("Total Purchases"
                        + categoryList.get(position).getProductName(), 0);
                totalSales = sharedPreferences.getInt("Total Sales"
                        + categoryList.get(position).getProductName(), 0);
                totalRemaining = (totalPurchase - totalSales);

                rv.setTextViewText(R.id.product_name2, productName);
                rv.setTextViewText(R.id.instore, "Remaining: " + String.valueOf(totalRemaining));
                rv.setTextViewText(R.id.purchased_stock, "Purchases: " + String.valueOf(totalPurchase));
                rv.setTextViewText(R.id.sold_stock, "Sales: " + String.valueOf(totalSales));

                Bundle extras = new Bundle();
                Intent fillIntent = new Intent();
                fillIntent.putExtras(extras);
                rv.setOnClickFillInIntent(R.id.widget_list_item2, fillIntent);
            }
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}

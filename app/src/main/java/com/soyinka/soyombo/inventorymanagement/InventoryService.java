package com.soyinka.soyombo.inventorymanagement;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SHOW on 1/30/2019.
 */

public class InventoryService extends IntentService {

    public static List<ProductCategory> categoryList = new ArrayList<>();
    List<ProductCategory> productCategoryList = new ArrayList<>();
    public static final String ACTION_GET_STOCK_DATA = "com.soyinka.soyombo.inventorymanagement";
    public static final String ACTION_UPDATE_STOCK_WIDGETS = "com.soyinka.soyombo.inventorymanagement.action.update_stock_widgets";
    public static final String EXTRA_PLANT_ID = "com.example.android.mygarden.extra.PLANT_ID";



    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public InventoryService() {
        super("InventoryService");
    }

    /**
     * Starts this service to perform WaterPlant action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionForInventoryData(Context context) {
        Intent intent = new Intent(context, InventoryService.class);
        intent.setAction(ACTION_GET_STOCK_DATA);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_STOCK_DATA.equals(action)) {
                inventoryData();
            }
        }
    }

    private void inventoryData(){

        final AppDatabase productDB = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "Inventory Database")
                .fallbackToDestructiveMigration().build();

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                productCategoryList = productDB.dataDao().loadData();
            }
        });

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, InventorySummaryWidgetProvider.class));
        //Trigger data update to handle the GridView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_widget_view);
        //Now update all widgets
        InventorySummaryWidgetProvider.updatePlantWidgets(this, appWidgetManager, productCategoryList,appWidgetIds);
    }
}

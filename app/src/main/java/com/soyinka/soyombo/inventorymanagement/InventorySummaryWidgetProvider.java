package com.soyinka.soyombo.inventorymanagement;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class InventorySummaryWidgetProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                List<ProductCategory> productCategories, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.stack_view_widget);
            Intent intent = new Intent(context, StackRemoteViewsService.class);
//            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
//            serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.stack_widget_view, intent);
            PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.empty_stack_view_text, pendingIntent);

            Intent viewIntent = new Intent(context, InventoryManagementActivity.class);
            PendingIntent viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.stack_widget_view, viewPendingIntent);
            remoteViews.setEmptyView(R.id.stack_widget_view, R.id.empty_stack_view_text);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }


    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          List<ProductCategory> productCategories, int[] appWidgetIds) {
            updateAppWidget(context, appWidgetManager, productCategories, appWidgetIds);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        InventoryService.startActionForInventoryData(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        InventoryService.startActionForInventoryData(context);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}


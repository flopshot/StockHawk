package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.HistoryActivity;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Logic For the StockHawk AppWidget
 */

public class StockWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId:appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Timber.w(PrefUtils.getLastWidgetUpdate(context));
            views.setTextViewText(R.id.widget_refresh_text, PrefUtils.getLastWidgetUpdate(context)); //"Updated \n 3d ago");

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the ListView collection
            setRemoteAdapter(context, views);
            Intent clickIntentTemplate = new Intent(context, HistoryActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                  .addNextIntentWithParentStack(clickIntentTemplate)
                  .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Set up the Refresh Button
            Intent intentSync = new Intent(context, StockWidgetProvider.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent pendingSync = PendingIntent
                  .getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_refresh_text,pendingSync);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        String actionName = "android.appwidget.action.APPWIDGET_UPDATE";
        if (actionName.equals(intent.getAction())) {
            Timber.w("Starting on Receive Method from Intent Data Changed");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                  new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
            onUpdate(context, appWidgetManager,appWidgetIds);
        }
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
              new Intent(context, StockWidgetRemoteViewsService.class));
    }

}

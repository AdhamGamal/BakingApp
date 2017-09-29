package com.amg.bakingapptest.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.ui.MainActivity;
import com.amg.bakingapptest.ui.StepsListActivity;

public class AppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            views.setOnClickPendingIntent(R.id.title_label, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));

            views.setRemoteAdapter(R.id.listView, new Intent(context, WidgetService.class));

            views.setPendingIntentTemplate(R.id.listView, PendingIntent.getActivity(context, 0, new Intent(context, StepsListActivity.class), 0));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, WidgetRemoteFactory.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listView);
        }
        super.onReceive(context, intent);
    }
}


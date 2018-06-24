package com.example.anita.stageuren;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import static com.example.anita.stageuren.database.DayRepository.APP_STATE_KEY;

/**
 * Implementation of App Widget functionality.
 */
public class StartStopWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.start_stop_widget);
        Intent intent = new Intent(context, StartStopService.class);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String app_state = sharedPreferences.getString(APP_STATE_KEY, context.getString(R.string.app_state_default));
        if(app_state.equals(context.getString(R.string.stopped_state)))
        {
            intent.setAction(StartStopService.ACTION_START);
            views.setImageViewResource(R.id.widget_image, R.mipmap.widget_start_round);
        }
        else {
            intent.setAction(StartStopService.ACTION_STOP);
            views.setImageViewResource(R.id.widget_image, R.mipmap.widget_stop_round);
        }

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}


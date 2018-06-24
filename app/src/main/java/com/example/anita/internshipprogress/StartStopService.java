package com.example.anita.internshipprogress;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.anita.internshipprogress.database.AppDatabase;
import com.example.anita.internshipprogress.database.Day;
import com.example.anita.internshipprogress.database.DayRepository;
import com.example.anita.internshipprogress.database.DayDao;

public class StartStopService extends IntentService {
    public static final String ACTION_STOP = "com.example.anita.stageuren.action.STOP";
    public static final String ACTION_START = "com.example.anita.stageuren.action.START";
    private DayDao dayDao;

    public StartStopService() {
        super("StartStopService");
    }

//    public static void startActionStart(Context context) {
//        Intent intent = new Intent(context, StartStopService.class);
//        intent.setAction(ACTION_START);
//        context.startService(intent);
//    }
//
//    public static void startActionStop(Context context) {
//        Intent intent = new Intent(context, StartStopService.class);
//        intent.setAction(ACTION_STOP);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if(dayDao ==null)
                dayDao = AppDatabase.getDatabase(this).dayDao();
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart();
            } else if (ACTION_STOP.equals(action)) {
                handleActionStop();
            }
        }
    }

    private void handleActionStart() {
        dayDao.insert(new Day());
        updateState(getString(R.string.started_state));
    }

    private void handleActionStop() {
        Day day = dayDao.getLastDay();
        day.setEndTime(System.currentTimeMillis());
        dayDao.updateDay(day);
        updateState(getString(R.string.stopped_state));
    }

    private void updateState(String state) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(DayRepository.APP_STATE_KEY, state).apply();
        Intent intent = new Intent(this, StartStopWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, StartStopWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.sendBroadcast(intent);
    }
}

package com.example.anita.stageuren.database;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.anita.stageuren.R;
import com.example.anita.stageuren.StartStopWidget;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DayRepository implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final static String APP_STATE_KEY = "app_state";
    private final DayDao mDayDao;
    private final LiveData<List<Day>> mAllDays;
    private final LiveData<Long> mTotalTime;
    private final MutableLiveData<String> mAppState;
    private final SharedPreferences mSharedPreferences;
    private final Context mContext;

    public DayRepository(Context context) {
        mContext = context;
        AppDatabase db = AppDatabase.getDatabase(mContext);
        mDayDao = db.dayDao();
        mAllDays = mDayDao.getAll();
        mTotalTime = mDayDao.getTotalTime();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mAppState = new MutableLiveData<>();
        mAppState.setValue(mSharedPreferences.getString(APP_STATE_KEY, mContext.getString(R.string.app_state_default)));
    }

    public LiveData<List<Day>> getAllDays() {
        return mAllDays;
    }

    public void start() {
        new startAsyncTask(mDayDao).execute();
        String startedState = mContext.getString(R.string.started_state);
        updateState(startedState);
    }

    public void stop() {
        new stopAsyncTask(mDayDao).execute();
        String stoppedState = mContext.getString(R.string.stopped_state);
        updateState(stoppedState);
    }

    private void updateState(String state) {
        mSharedPreferences.edit().putString(APP_STATE_KEY, state).apply();
        Intent intent = new Intent(mContext, StartStopWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, StartStopWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        mContext.sendBroadcast(intent);
    }

    public LiveData<String> getAppState() {
        return mAppState;
    }

    public LiveData<Long> getTotalHours() {return mTotalTime;}

    public LiveData<Day> getDayWithId(int dayId) { return mDayDao.getDayWithId(dayId); }

    public int deleteDay(Day day){
        try {
            return new deleteAsyncTask(mDayDao).execute(day).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateDay(Day day){ new updateAsyncTask(mDayDao).execute(day); }

    public void insertNewDay(Day day) { new insertAsyncTask(mDayDao).execute(day); }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(APP_STATE_KEY))
            mAppState.setValue(sharedPreferences.getString(APP_STATE_KEY, mContext.getString(R.string.app_state_default)));
    }

    private static class insertAsyncTask extends AsyncTask<Day, Void, Void> {

        private final DayDao mAsyncTaskDao;

        insertAsyncTask(DayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Day... params) {
            if(params[0]!=null)
                mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Day, Void, Void> {

        private final DayDao mAsyncTaskDao;

        updateAsyncTask(DayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Day... params) {
            if(params[0]!=null)
                mAsyncTaskDao.updateDay(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Day, Void, Integer> {

        private final DayDao mAsyncTaskDao;

        deleteAsyncTask(DayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Integer doInBackground(final Day... params) {
            if(params[0]!=null)
                return mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class startAsyncTask extends android.os.AsyncTask<Void, Void, Void> {

        private final DayDao mAsyncTaskDao;

        startAsyncTask(DayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mAsyncTaskDao.insert(new Day());
            return null;
        }
    }

    private static class stopAsyncTask extends AsyncTask<Void, Void, Void> {

        private final DayDao mAsyncTaskDao;

        stopAsyncTask(DayDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Day day = mAsyncTaskDao.getLastDay();
            day.setEndTime(System.currentTimeMillis());
            mAsyncTaskDao.updateDay(day);
            return null;
        }
    }
}


package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.anita.stageuren.database.AppDatabase;
import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayDao;

import java.util.List;

public class DayRepository {
    private DayDao mDayDao;
    private LiveData<List<Day>> mAllDays;

    DayRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDayDao = db.dayDao();
        mAllDays = mDayDao.getAll();
    }

    LiveData<List<Day>> getAllDays() {
        return mAllDays;
    }

    public void start() {
        new startAsyncTask(mDayDao).execute();
    }

    public void stop() {
        new stopAsyncTask(mDayDao).execute();
    }

    private static class startAsyncTask extends AsyncTask<Void, Void, Void> {

        private DayDao mAsyncTaskDao;

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

        private DayDao mAsyncTaskDao;

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


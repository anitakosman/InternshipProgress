package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.os.AsyncTask;

import com.example.anita.stageuren.database.AppDatabase;
import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayDao;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class DayRepository {
    private DayDao mDayDao;
    private LiveData<List<Day>> mAllDays;
    private LiveData<Long> mTotalTime;

    DayRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDayDao = db.dayDao();
        mAllDays = mDayDao.getAll();
        mTotalTime = mDayDao.getTotalTime();
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

    private static class insertAsyncTask extends AsyncTask<Day, Void, Void> {

        private DayDao mAsyncTaskDao;

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

        private DayDao mAsyncTaskDao;

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

        private DayDao mAsyncTaskDao;

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


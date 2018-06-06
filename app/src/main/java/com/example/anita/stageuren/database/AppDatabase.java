package com.example.anita.stageuren.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

@Database(entities = {Day.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){ 
            super.onOpen(db);
            if(db.query("SELECT * FROM day").getCount() == 0)
                new PopulateDbAsync(INSTANCE).execute();
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        synchronized (AppDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "stage_uren_database").addCallback(sRoomDatabaseCallback).build();
            }
        }
        return INSTANCE;
    }

    public abstract DayDao dayDao();

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final DayDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.dayDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //TODO: insert all pre-app days
            return null;
        }
    }
}

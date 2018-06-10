package com.example.anita.stageuren.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

@Database(entities = {Day.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        synchronized (AppDatabase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "stage_uren_database").addMigrations(MIGRATION_1_2).build();
            }
        }
        return INSTANCE;
    }

    public abstract DayDao dayDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE day ADD COLUMN duration BIGINT");
            Cursor cursor = database.query("SELECT * FROM day");
            int idColumn = cursor.getColumnIndex("id");
            int startTimeColumn = cursor.getColumnIndex("startTime");
            int endTimeColumn = cursor.getColumnIndex("endTime");
            while(cursor.moveToNext()){
                if(cursor.isNull(endTimeColumn))
                    continue;
                ContentValues contentValues = new ContentValues();
                int id = cursor.getInt(idColumn);
                long startTime = cursor.getLong(startTimeColumn);
                long endTime = cursor.getLong(endTimeColumn);
                contentValues.put("id", id);
                contentValues.put("startTime", startTime);
                contentValues.put("endTime", endTime);
                contentValues.put("duration", Day.getDuration(startTime, endTime));
                database.update("day", SQLiteDatabase.CONFLICT_ABORT, contentValues, "id=?", new Object[]{id});
            }
        }
    };
}

package com.example.anita.internshipprogress.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DayDao {
    @Query("SELECT * FROM day ORDER BY startTime DESC")
    LiveData<List<Day>> getAll();

    @Query("SELECT * FROM day ORDER BY startTime DESC LIMIT 1")
    Day getLastDay();

    @Query("SELECT SUM(duration) FROM day")
    LiveData<Long> getTotalTime();

    @Insert
    void insert(Day day);

    @Update
    void updateDay(Day day);

    @Delete
    int delete(Day day);

    @Query("SELECT * FROM day WHERE ID = :dayId LIMIT 1")
    LiveData<Day> getDayWithId(int dayId);
}

package com.example.anita.stageuren.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DayDao {
    @Query("SELECT * FROM day ORDER BY startTime")
    LiveData<List<Day>> getAll();

    @Query("SELECT * FROM day ORDER BY startTime DESC LIMIT 1")
    Day getLastDay();

    @Insert
    void insert(Day day);

    @Insert
    void insertAll(Day... days);

    @Update
    void updateDay(Day day);

    @Delete
    void delete(Day day);

}

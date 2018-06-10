package com.example.anita.stageuren.database;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private Long startTime;
    private Long endTime;

    public Day(Long startTime, Long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    Day(long startTime)
    {
        this(startTime, null);
    }

    public Day(){
        this(System.currentTimeMillis());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) { this.startTime = startTime; }

    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) { this.endTime = endTime; }

    public String getDate(){
        Date date = new Date(startTime);
        return new SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(date);
    }
    public static String getTime(long milis){
        Date date = new Date(milis);
        return new SimpleDateFormat("H:mm", Locale.getDefault()).format(date);
    }
}

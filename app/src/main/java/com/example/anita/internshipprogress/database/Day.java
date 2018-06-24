package com.example.anita.internshipprogress.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private Long startTime;
    private Long endTime;
    private Long duration;

    public Day(Long startTime, Long endTime){
        this.startTime = startTime;
        this.endTime = endTime;
        setDuration();
    }

    private Day(long startTime)
    {
        this(startTime, null);
    }

    public Day(){
        this(System.currentTimeMillis());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Long getStartTime() { return startTime; }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
        setDuration();
    }

    public Long getEndTime() { return endTime; }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
        setDuration();
    }

    public Long getDuration() {
        return duration;
    }

    void setDuration(Long duration){this.duration = duration;}

    private void setDuration() {
        duration = calculateDuration();
    }

    private Long calculateDuration() {
        if(startTime==null || endTime == null)
            return null;
        Calendar startCalendar = Calendar.getInstance(Locale.getDefault());
        startCalendar.setTimeInMillis(startTime);
        Calendar endCalendar = Calendar.getInstance(Locale.getDefault());
        endCalendar.setTimeInMillis(endTime);
        // If started before 11.30 and ended after 13.00, subtract 30 minutes of duration because of lunch break
        if((startCalendar.get(Calendar.HOUR_OF_DAY)<11 ||
                (startCalendar.get(Calendar.HOUR_OF_DAY)==11 && startCalendar.get(Calendar.MINUTE)<30))
                && endCalendar.get(Calendar.HOUR_OF_DAY)>=13) {
            return endTime - startTime - (30 * 60 *1000);
        }
        return endTime-startTime;
    }

    public String getDate(){
        return Day.getDate(startTime);
    }

    public static String getDate(long milis){
        Date date = new Date(milis);
        return new SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(date);
    }

    public static String getTime(long milis){
        Date date = new Date(milis);
        return new SimpleDateFormat("H:mm", Locale.getDefault()).format(date);
    }
}

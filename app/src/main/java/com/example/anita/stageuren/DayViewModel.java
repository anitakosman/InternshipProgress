package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.anita.stageuren.database.Day;

import java.util.List;

public class DayViewModel extends AndroidViewModel {
    private DayRepository mRepository;
    private LiveData<List<Day>> mAllDays;
    private LiveData<Long> mTotalTime;

    public DayViewModel (Application application) {
        super(application);
        mRepository = new DayRepository(application);
        mAllDays = mRepository.getAllDays();
        mTotalTime = mRepository.getTotalHours();
    }

    LiveData<List<Day>> getAllDays() { return mAllDays; }

    public LiveData<Long> getTotalHours() {
        return mTotalTime;
    }

    public void start() { mRepository.start(); }

    public void stop() { mRepository.stop(); }
}
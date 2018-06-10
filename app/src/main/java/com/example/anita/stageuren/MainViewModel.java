package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private DayRepository mRepository;
    private LiveData<List<Day>> mAllDays;
    private LiveData<Long> mTotalTime;

    public MainViewModel(Application application) {
        super(application);
        System.out.println("New DayViewModel");
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
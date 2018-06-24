package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayRepository;

import java.util.List;

class MainViewModel extends AndroidViewModel {
    private final DayRepository mRepository;
    private final LiveData<List<Day>> mAllDays;
    private final LiveData<Long> mTotalTime;
    private final LiveData<String> mAppState;

    public MainViewModel(Application application) {
        super(application);
        System.out.println("New DayViewModel");
        mRepository = new DayRepository(application);
        mAllDays = mRepository.getAllDays();
        mTotalTime = mRepository.getTotalHours();
        mAppState = mRepository.getAppState();
}

    public LiveData<List<Day>> getAllDays() { return mAllDays; }

    public LiveData<Long> getTotalHours() {
        return mTotalTime;
    }

    public LiveData<String> getAppState() {
        return mAppState;
    }

    public void start() { mRepository.start(); }

    public void stop() { mRepository.stop(); }
}
package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.anita.stageuren.database.Day;

public class EditorViewModel extends AndroidViewModel {
    private DayRepository mRepository;
    private LiveData<Day> mCurDay;

    public EditorViewModel(Application application) {
        super(application);
        mRepository = new DayRepository(application);
    }

    public void setCurDay(int dayId) {
        mCurDay = mRepository.getDayWithId(dayId);
    }

    public int deleteCurDay(){
        return mRepository.deleteDay(mCurDay.getValue());
    }

    public void updateCurDay(){
        mRepository.updateDay(mCurDay.getValue());
    }

    public void insertNewDay(Day day){
        mRepository.insertNewDay(day);
    }

    LiveData<Day> getCurDay() { return mCurDay; }
}
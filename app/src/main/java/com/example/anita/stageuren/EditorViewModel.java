package com.example.anita.stageuren;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayRepository;

import java.util.Calendar;
import java.util.Locale;

public class EditorViewModel extends AndroidViewModel {
    private DayRepository mRepository;
    private LiveData<Day> mCurDay;
    private MutableLiveData<Calendar> mDate, mStartTime, mEndTime;

    private boolean mChanged = false;

    public EditorViewModel(Application application) {
        super(application);
        mRepository = new DayRepository(application);
        mDate = new MutableLiveData<>();
        mStartTime = new MutableLiveData<>();
        mEndTime = new MutableLiveData<>();
    }

    @SuppressWarnings("all") // to suppress warning boolean method always inverted
    public boolean isChanged() {
        return mChanged;
    }

    public void setChanged() {
        mChanged = true;
    }

    public LiveData<Calendar> getDate() {
        return mDate;
    }

    public LiveData<Calendar> getStartTime() {
        return mStartTime;
    }

    public LiveData<Calendar> getEndTime() {
        return mEndTime;
    }

    public void setDate(int year, int month, int day) {
        if(mDate.getValue() == null)
            mDate.setValue(Calendar.getInstance(Locale.getDefault()));
        assert mDate.getValue() != null;
        mDate.getValue().set(year, month, day);
        mDate.setValue(mDate.getValue());
    }

    public void setStartTime(int hourOfDay, int minute) {
        if(mStartTime.getValue() == null)
            mStartTime.setValue(Calendar.getInstance(Locale.getDefault()));
        assert mStartTime.getValue() != null;
        mStartTime.getValue().set(Calendar.HOUR_OF_DAY, hourOfDay);
        mStartTime.getValue().set(Calendar.MINUTE,minute);
        mStartTime.setValue(mStartTime.getValue());
    }

    public void setEndTime(int hourOfDay, int minute) {
        if(mEndTime.getValue() == null)
            mEndTime.setValue(Calendar.getInstance(Locale.getDefault()));
        assert mEndTime.getValue() != null;
        mEndTime.getValue().set(Calendar.HOUR_OF_DAY, hourOfDay);
        mEndTime.getValue().set(Calendar.MINUTE, minute);
        mEndTime.setValue(mEndTime.getValue());
    }

    public void setCalendars(Long startTime, Long endTime){
        if(startTime != null || endTime != null) {
            if (mDate.getValue() == null)
                mDate.setValue(Calendar.getInstance(Locale.getDefault()));
            assert mDate.getValue() != null;
            mDate.getValue().setTimeInMillis(startTime != null ? startTime : endTime);
            mDate.setValue(mDate.getValue());
        }

        if(startTime != null) {
            if (mStartTime.getValue() == null)
                mStartTime.setValue(Calendar.getInstance(Locale.getDefault()));
            assert mStartTime.getValue() != null;
            mStartTime.getValue().setTimeInMillis(startTime);
            mStartTime.setValue(mStartTime.getValue());
        }

        if(endTime != null) {
            if (mEndTime.getValue() == null)
                mEndTime.setValue(Calendar.getInstance(Locale.getDefault()));
            assert mEndTime.getValue() != null;
            mEndTime.getValue().setTimeInMillis(endTime);
            mEndTime.setValue(mEndTime.getValue());
        }
    }

    LiveData<Day> getCurDay() { return mCurDay; }

    public void setCurDay(int dayId) {
        mCurDay = mRepository.getDayWithId(dayId);
    }

    public int deleteCurDay(){
        return mRepository.deleteDay(mCurDay.getValue());
    }

    public void saveDay(){
        Day day = getDay();
        if(mCurDay.getValue() == null)
            mRepository.insertNewDay(day);
        else
            mRepository.updateDay(day);
    }

    private Day getDay() {
        Day day;
        Long startTime = mergeDateAndTime(mStartTime.getValue());
        Long endTime = mergeDateAndTime(mEndTime.getValue());
        if(mCurDay.getValue() == null)
            day = new Day(startTime, endTime);
        else {
            day = mCurDay.getValue();
            day.setStartTime(startTime);
            day.setEndTime(endTime);
        }
        return day;
    }

    private Long mergeDateAndTime(Calendar timeCalendar) {
        if(mDate.getValue() == null)
            return null;
        Calendar date = mDate.getValue();
        timeCalendar.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
        return timeCalendar.getTimeInMillis();
    }
}
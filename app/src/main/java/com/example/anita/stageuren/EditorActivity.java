package com.example.anita.stageuren;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.anita.stageuren.database.Day;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity {
    private TextView mEditDate;
    private TextView mEditStartTime;
    private TextView mEditEndTime;
    private Calendar mStartTimeCalendar = Calendar.getInstance(Locale.getDefault()),
            mEndTimeCalendar = Calendar.getInstance(Locale.getDefault());

    private EditorViewModel mEditorViewModel;
    private int mDayId;
    private Boolean mChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mDayId = intent.getIntExtra("dayId", -1);

        mEditorViewModel = ViewModelProviders.of(this).get(EditorViewModel.class);
        mEditorViewModel.setCurDay(mDayId);

        // Find all relevant views that we will need to read user input from
        mEditDate = findViewById(R.id.edit_date);
        mEditStartTime = findViewById(R.id.edit_start_time);
        mEditEndTime = findViewById(R.id.edit_end_time);

        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        mEditStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        mEditEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        if (mDayId == -1) {
            setTitle("New entry");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit entry");
            LiveData<Day> current = mEditorViewModel.getCurDay();
            current.observe(this, new Observer<Day>() {
                @Override
                public void onChanged(@Nullable Day day) {
                    if (day != null) {
                        mEditDate.setText(day.getDate());
                        mEditStartTime.setText(Day.getTime(day.getStartTime()));
                        mEditEndTime.setText(Day.getTime(day.getEndTime()));
                        mStartTimeCalendar.setTimeInMillis(day.getStartTime());
                        mEndTimeCalendar.setTimeInMillis(day.getEndTime());
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mDayId == -1) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(save())
                    finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean save() {
        if (mDayId != -1) {
            Day current = mEditorViewModel.getCurDay().getValue();
            assert current != null;
            current.setStartTime(mStartTimeCalendar.getTimeInMillis());
            current.setEndTime(mEndTimeCalendar.getTimeInMillis());
            mEditorViewModel.updateCurDay();
        } else {
            if (mEditDate.getText().equals("Pick a date") || mEditStartTime.getText().equals("Pick a time") || mEditEndTime.getText().equals("Pick a time")) {
                Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
                return false;
            }
            else
                mEditorViewModel.insertNewDay(new Day(mStartTimeCalendar.getTimeInMillis(), mEndTimeCalendar.getTimeInMillis()));
        }
        return true;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to delete this entry?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (mDayId != -1) {
                    if (mEditorViewModel.deleteCurDay() != 0)
                        Toast.makeText(EditorActivity.this, "Delete successful", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(EditorActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have some unsaved changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        String tag = v.getId() == R.id.edit_start_time ? "startTimePicker" : "endTimePicker";
        newFragment.show(getFragmentManager(), tag);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), R.style.AppDialogTheme, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditorActivity activity = (EditorActivity) getActivity();
            activity.mChanged = true;
            Calendar calendar;
            TextView textView;
            if (getTag().equals("startTimePicker")) {
                textView = activity.findViewById(R.id.edit_start_time);
                calendar = activity.mStartTimeCalendar;
            } else {
                textView = activity.findViewById(R.id.edit_end_time);
                calendar = activity.mEndTimeCalendar;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            textView.setText(Day.getTime(calendar.getTimeInMillis()));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), R.style.AppDialogTheme, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            EditorActivity activity = (EditorActivity) getActivity();
            Calendar startTimeCalender = activity.mStartTimeCalendar;
            activity.mChanged = true;
            startTimeCalender.set(year, month, day);
            activity.mEndTimeCalendar.set(year, month, day);
            activity.mEditDate.setText(new SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(startTimeCalender.getTime()));
        }
    }
}
package com.example.anita.internshipprogress;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anita.internshipprogress.database.Day;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    public static final long MILLISECONDS_PER_HOUR = 60L * 60L * 1000L;
    private MainViewModel mMainViewModel;
    private FloatingActionButton fab;
    private TextView fabTv, totalHoursTv, daysToGoTv, relativeHoursToGoTodayTv;
    private OnClickListener startOnClickListener, stopOnClickListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final DayListAdapter adapter = new DayListAdapter(this,new DayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int adapterPosition) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                int dayId = mMainViewModel.getAllDays().getValue()!=null?
                        mMainViewModel.getAllDays().getValue().get(adapterPosition).getId() : -1;
                i.putExtra("dayId", dayId);
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mMainViewModel.getAllDays().observe(this, new Observer<List<Day>>() {
            @Override
            public void onChanged(@Nullable final List<Day> days) {
                adapter.setDays(days);
            }
        });

        fab = findViewById(R.id.fab);
        fabTv = findViewById(R.id.fab_text);
        totalHoursTv = findViewById(R.id.valueTotalHoursTextView);
        relativeHoursToGoTodayTv = findViewById(R.id.relativeHoursToGoTodayTextView);
        daysToGoTv = findViewById(R.id.valueDaysToGoTextView);
        progressBar = findViewById(R.id.progressBar);

        mMainViewModel.getTotalHours().observe(this, new Observer<Long>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(@Nullable Long totalTime) {
                long time = totalTime != null ? totalTime : 0L;
                long totalHours = TimeUnit.MILLISECONDS.toHours(time);
                long remainingTime = 1120L * MILLISECONDS_PER_HOUR - time;
                long remainingDays = remainingTime / (8L * MILLISECONDS_PER_HOUR);
                long relativeTime = remainingTime % (8L * MILLISECONDS_PER_HOUR);
                String sign = "+";
                long relativeHours = TimeUnit.MILLISECONDS.toHours(relativeTime);
                long relativeMinutes = TimeUnit.MILLISECONDS.toMinutes(relativeTime - (relativeHours * MILLISECONDS_PER_HOUR));
                if(relativeHours > 4 || (relativeHours == 4 && relativeMinutes >0)){
                    sign = "-";
                    remainingDays++;
                    relativeHours = 7 - relativeHours;
                    relativeMinutes = 60 - relativeMinutes;
                }

                totalHoursTv.setText(String.format("%d", totalHours));
                daysToGoTv.setText(String.format("%d", remainingDays));
                relativeHoursToGoTodayTv.setText(String.format("%s%d:%d", sign, relativeHours, relativeMinutes));
                progressBar.setProgress((int) (totalHours*100/1120));
            }
        });

        // Setup onClickListeners
        startOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainViewModel.start();
            }
        };

        stopOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                    mMainViewModel.stop();
            }
        };

        mMainViewModel.getAppState().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String appState) {
                if(appState != null) {
                    if (appState.equals(getString(R.string.started_state))) {
                        fabTv.setText(R.string.stop);
                        fab.setOnClickListener(stopOnClickListener);
                    } else {
                        fabTv.setText(R.string.start);
                        fab.setOnClickListener(startOnClickListener);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent i = new Intent(this, EditorActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

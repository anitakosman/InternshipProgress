package com.example.anita.stageuren;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayListAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {
    private final static String FAB_STATE_KEY = "fab_state";
    private MainViewModel mMainViewModel;
    private FloatingActionButton fab;
    private TextView fabTv, totalHoursTv, hoursToGoTv;
    private OnClickListener startOnClickListener, stopOnClickListener;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getDefaultSharedPreferences(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final DayListAdapter adapter = new DayListAdapter(this,new DayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int adapterPosition) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                i.putExtra("dayId", mMainViewModel.getAllDays().getValue().get(adapterPosition).getId());
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
        hoursToGoTv = findViewById(R.id.valueHoursToGoTextView);
        progressBar = findViewById(R.id.progressBar);

        mMainViewModel.getTotalHours().observe(this, new Observer<Long>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(@Nullable Long totalTime) {
                long totalHours = TimeUnit.MILLISECONDS.toHours(totalTime != null ? totalTime : 0);
                totalHoursTv.setText(String.format("%d", totalHours));
                hoursToGoTv.setText(String.format("%d", 1120 - totalHours));
                progressBar.setProgress((int) (totalHours*100/1120));
            }
        });

        // Setup onClickListeners
        startOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainViewModel.start();
                fabTv.setText(R.string.fab_icon_stop);
                fab.setOnClickListener(stopOnClickListener);
            }
        };

        stopOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                    mMainViewModel.stop();
                    fabTv.setText(R.string.fab_icon_start);
                    fab.setOnClickListener(startOnClickListener);
            }
        };

        // Setup FAB
        String fab_state = sharedPreferences.getString(FAB_STATE_KEY, getString(R.string.fab_icon_default));
        fabTv.setText(fab_state);
        if(fab_state.equals(getString(R.string.fab_icon_stop)))
        {
            fab.setOnClickListener(stopOnClickListener);
        }
        else {
            fab.setOnClickListener(startOnClickListener);
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit().putString(FAB_STATE_KEY, (String) fabTv.getText()).apply();
    }
}

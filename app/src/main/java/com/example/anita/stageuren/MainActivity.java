package com.example.anita.stageuren;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.anita.stageuren.database.Day;
import com.example.anita.stageuren.database.DayListAdapter;

import java.util.List;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {
    private final static String FAB_STATE_KEY = "fab_state";
    private DayViewModel mDayViewModel;
    private FloatingActionButton fab;
    private TextView fab_tv;
    private OnClickListener startOnClickListener, stopOnClickListener;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getDefaultSharedPreferences(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final DayListAdapter adapter = new DayListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDayViewModel = ViewModelProviders.of(this).get(DayViewModel.class);
        mDayViewModel.getAllDays().observe(this, new Observer<List<Day>>() {
            @Override
            public void onChanged(@Nullable final List<Day> days) {
                adapter.setDays(days);
            }
        });

        fab = findViewById(R.id.fab);
        fab_tv = findViewById(R.id.fab_text);

        // Setup onClickListeners
        startOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDayViewModel.start();
                fab_tv.setText(R.string.fab_icon_stop);
                fab.setOnClickListener(stopOnClickListener);
            }
        };

        stopOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                    mDayViewModel.stop();
                    fab_tv.setText(R.string.fab_icon_start);
                    fab.setOnClickListener(startOnClickListener);
            }
        };

        // Setup FAB
        String fab_state = sharedPreferences.getString(FAB_STATE_KEY, getString(R.string.fab_icon_default));
        fab_tv.setText(fab_state);
        if(fab_state.equals(getString(R.string.fab_icon_stop)))
        {
            fab.setOnClickListener(stopOnClickListener);
        }
        else {
            fab.setOnClickListener(startOnClickListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit().putString(FAB_STATE_KEY, (String) fab_tv.getText()).apply();
    }
}

package com.example.anita.stageuren.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anita.stageuren.R;

import java.util.List;

public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.DayViewHolder> {

    class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView, startTimeTextView, endTimeTextView;

        private DayViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<Day> mDays; // Cached copy of days

    public DayListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public DayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new DayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DayViewHolder holder, int position) {
        if (mDays != null) {
            Day current = mDays.get(position);
            holder.dateTextView.setText(current.getDate());
            holder.startTimeTextView.setText(Day.getTime(current.getStartTime()));
            Long endTime = current.getEndTime();
            if(endTime!=null)
                holder.endTimeTextView.setText(Day.getTime(endTime));
            else
                holder.endTimeTextView.setText("");
        } else {
            // Covers the case of data not being ready yet.
            holder.dateTextView.setText(R.string.no_day);
            holder.startTimeTextView.setText(R.string.no_start_time);
            holder.endTimeTextView.setText("");
        }
    }

    public void setDays(List<Day> days){
        mDays = days;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mDays has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mDays != null)
            return mDays.size();
        else return 0;
    }
}

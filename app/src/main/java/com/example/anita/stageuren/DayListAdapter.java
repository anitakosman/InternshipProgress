package com.example.anita.stageuren;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anita.stageuren.database.Day;

import java.util.List;
import java.util.Locale;

public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.DayViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int adapterPosition);
    }

    private final OnItemClickListener mListener;
    private final LayoutInflater mInflater;
    private List<Day> mDays; // Cached copy of days

    DayListAdapter(Context context, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView, startTimeTextView, endTimeTextView, durationTextView;

        private DayViewHolder(final View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
        }
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new DayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DayViewHolder holder, int position) {
        if (mDays != null) {
            final Day current = mDays.get(position);
            Long startTime = current.getStartTime();
            holder.dateTextView.setText(current.getDate());
            holder.startTimeTextView.setText(Day.getTime(startTime));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    mListener.onItemClick(holder.getAdapterPosition());
                }
            });
            Long endTime = current.getEndTime();
            if(endTime!=null)
                holder.endTimeTextView.setText(Day.getTime(endTime));
            if(current.getDuration()!=null){
                Long durationInMinutes = current.getDuration()/60000;
                String format = durationInMinutes%60>=10? "%d.%d" : "%d.0%d";
                holder.durationTextView.setText(String.format(Locale.getDefault(), format, durationInMinutes/60, durationInMinutes%60));
            }
            else {
                holder.endTimeTextView.setText("");
                holder.durationTextView.setText("");
            }
        } else {
            // Covers the case of data not being ready yet.
            holder.dateTextView.setText(R.string.no_date);
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

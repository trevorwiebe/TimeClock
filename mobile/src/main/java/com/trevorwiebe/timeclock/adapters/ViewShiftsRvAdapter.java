package com.trevorwiebe.timeclock.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trevorwiebe.timeclock.R;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;

/**
 * Created by thisi on 10/31/2018.
 */

public class ViewShiftsRvAdapter extends RecyclerView.Adapter<ViewShiftsRvAdapter.ViewShiftsViewHolder> {

    // TODO: 10/31/2018 fill out this adapter

    private static final String TAG = "ViewShiftsRvAdapter";

    private Context mContext;
    private ArrayList<Long> mClockInList;
    private ArrayList<Long> mClockOutList;
    private ArrayList<Long> mDays;

    public ViewShiftsRvAdapter(Context context, ArrayList<Long> clockInEntries, ArrayList<Long> clockOutEntries, ArrayList<Long> days){
        this.mContext = context;
        this.mClockInList = clockInEntries;
        this.mClockOutList = clockOutEntries;
        this.mDays = days;
    }

    @Override
    public int getItemCount() {
        if(mDays == null)return 0;
        return mDays.size();
    }

    @NonNull
    @Override
    public ViewShiftsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_view_shifts, viewGroup, false);
        return new ViewShiftsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewShiftsViewHolder holder, int i) {
        long date = mDays.get(i);
        long msPerDay = 86400 * 1000;
        long tomorrow = date + msPerDay;
        long millisecondsAlready = 0;

        ArrayList<Long> clockInForToday = new ArrayList<>();
        ArrayList<Long> clockOutForToday = new ArrayList<>();

        for(int r = 0; mClockInList.size() > r; r++){
            long timeInQuestion = mClockInList.get(r);
            if(timeInQuestion > date && timeInQuestion < tomorrow){
                clockInForToday.add(timeInQuestion);
            }
        }

        for(int q = 0; mClockOutList.size() > q; q++){
            long timeInQuestion = mClockOutList.get(q);
            if(timeInQuestion > date && timeInQuestion < tomorrow){
                clockOutForToday.add(timeInQuestion);
            }
        }

        for(int z = 0; clockInForToday.size() > z; z++){
            long time1 = clockInForToday.get(z);
            long time2;
            if(clockOutForToday.size() > z){
                time2 = clockOutForToday.get(z);
            }else{
                time2 = System.currentTimeMillis();
            }

            long time3 = time2 - time1;

            millisecondsAlready = millisecondsAlready + time3;

        }

        holder.mHoursWorked.setText(Utility.convertMillisecondsToHours(millisecondsAlready));

        holder.mViewShiftDate.setText(Utility.getFormattedDate(date));
    }

    public void swapData(ArrayList<Long> clockInEntries, ArrayList<Long> clockOutEntries, ArrayList<Long> days){
        mClockInList = clockInEntries;
        mClockOutList = clockOutEntries;
        mDays = days;
        if(mClockInList != null && mClockOutList != null && mDays != null) notifyDataSetChanged();
    }

    public class ViewShiftsViewHolder extends RecyclerView.ViewHolder{

        private TextView mViewShiftDate;
        private TextView mHoursWorked;

        public ViewShiftsViewHolder(View view){
            super(view);

            mViewShiftDate = view.findViewById(R.id.view_shifts_date);
            mHoursWorked = view.findViewById(R.id.hours_worked);

        }
    }
}

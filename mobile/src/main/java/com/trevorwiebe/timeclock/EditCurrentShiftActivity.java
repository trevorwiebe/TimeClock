package com.trevorwiebe.timeclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;

public class EditCurrentShiftActivity extends AppCompatActivity {

    private static final String TAG = "EditCurrentShiftActivit";

    private LinearLayout mNoShiftsWorked;
    private LinearLayout mShiftsWorked;

    private ArrayList<Long> mSelectedClockInTimes;
    private ArrayList<Long> mSelectedClockOutTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_current_shift);

        mNoShiftsWorked = findViewById(R.id.no_shift_layout);
        mShiftsWorked = findViewById(R.id.shifts_worked_layout);

        Intent selectedIntent = getIntent();
        //set the title of the activity to the date you are editing
        long selectedTime = selectedIntent.getLongExtra("selectedDate", 0);
        setTitle(Utility.getFormattedDate(selectedTime));

        mSelectedClockInTimes = (ArrayList<Long>) selectedIntent.getSerializableExtra("selectedClockInTimes");
        mSelectedClockOutTimes = (ArrayList<Long>) selectedIntent.getSerializableExtra("selectedClockOutTimes");

        if(mSelectedClockInTimes == null || mSelectedClockInTimes.size() == 0 && mSelectedClockOutTimes == null || mSelectedClockOutTimes.size() == 0){
            mShiftsWorked.setVisibility(View.GONE);
            mNoShiftsWorked.setVisibility(View.VISIBLE);
        }else{
            mShiftsWorked.setVisibility(View.VISIBLE);
            mNoShiftsWorked.setVisibility(View.GONE);

            for(int t = 0; mSelectedClockInTimes.size() > t; t++){
                long time = mSelectedClockInTimes.get(t);
                addTextView(time);

                long time2 = mSelectedClockOutTimes.get(t);
                addTextView(time2);

                View view = new View(this);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        75
                );
                view.setLayoutParams(spacerParams);
                mShiftsWorked.addView(view);
            }
        }
    }

    private void addTextView(Long time){
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels1 = (int) (24*scale + 0.5f);
        int dpAsPixels2 = (int) (8*scale + 0.5f);
        params.setMargins(dpAsPixels1, dpAsPixels2, dpAsPixels1, dpAsPixels2);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setText(Utility.getFormattedTime(time));
        mShiftsWorked.addView(textView);
    }
}

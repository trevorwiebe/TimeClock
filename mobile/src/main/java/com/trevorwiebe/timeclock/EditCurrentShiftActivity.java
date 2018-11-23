package com.trevorwiebe.timeclock;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.object.ViewObject;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class EditCurrentShiftActivity extends AppCompatActivity {

    private static final String TAG = "EditCurrentShiftActivit";

    private LinearLayout mNoShiftsWorked;
    private LinearLayout mShiftsWorked;
    private ArrayList<ClockInEntry> mSelectedClockInTimes;
    private ArrayList<ClockOutEntry> mSelectedClockOutTimes;

    private DatabaseReference mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_current_shift);

        mNoShiftsWorked = findViewById(R.id.no_shift_layout);
        mShiftsWorked = findViewById(R.id.shifts_worked_layout);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Intent selectedIntent = getIntent();
        //set the title of the activity to the date you are editing
        long selectedTime = selectedIntent.getLongExtra("selectedDate", 0);
        setTitle(Utility.getFormattedDate(selectedTime));

        mSelectedClockInTimes = (ArrayList<ClockInEntry>) selectedIntent.getSerializableExtra("selectedClockInTimes");
        mSelectedClockOutTimes = (ArrayList<ClockOutEntry>) selectedIntent.getSerializableExtra("selectedClockOutTimes");

        if(mSelectedClockInTimes == null || mSelectedClockInTimes.size() == 0 && mSelectedClockOutTimes == null || mSelectedClockOutTimes.size() == 0){
            mShiftsWorked.setVisibility(View.GONE);
            mNoShiftsWorked.setVisibility(View.VISIBLE);
        }else{
            mShiftsWorked.setVisibility(View.VISIBLE);
            mNoShiftsWorked.setVisibility(View.GONE);

            for(int t = 0; mSelectedClockInTimes.size() > t; t++){
                long time = mSelectedClockInTimes.get(t).getClockInTime();
                String timeId = mSelectedClockInTimes.get(t).getEntryId();
                addTextInputEditText(time, timeId,true, t);

                if(t < mSelectedClockOutTimes.size()){
                    long time2 = mSelectedClockOutTimes.get(t).getClockOutTime();
                    String timeOutId = mSelectedClockOutTimes.get(t).getEntryId();
                    addTextInputEditText(time2, timeOutId, false, t);
                }

                View view = new View(this);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        75
                );
                view.setLayoutParams(spacerParams);
                mShiftsWorked.addView(view);
            }

            Button updateButton = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (24*scale + 0.5f);
            updateButton.setPadding(0, dpAsPixels, 0, dpAsPixels);
            buttonParams.setMargins(dpAsPixels, 0, dpAsPixels, dpAsPixels);
            updateButton.setLayoutParams(buttonParams);
            updateButton.setBackground(getResources().getDrawable(R.drawable.sign_in_sign_out_btn));
            updateButton.setText("Update");
            updateButton.setTextColor(getResources().getColor(android.R.color.white));
            mShiftsWorked.addView(updateButton);

        }
    }

    private void addTextInputEditText(Long time, String timeId, boolean isClockedIn, int viewId){

        // TextInputLayout
        LinearLayout.LayoutParams textInputParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(textInputParams);
        textInputLayout.setTag(viewId);


        // TextInputEditText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels1 = (int) (24*scale + 0.5f);
        int dpAsPixels2 = (int) (8*scale + 0.5f);
        params.setMargins(dpAsPixels1, dpAsPixels2, dpAsPixels1, dpAsPixels2);

        TextInputEditText textInputEditText = new TextInputEditText(this);
        ViewObject viewObject = new ViewObject(isClockedIn, timeId, viewId, time);
        textInputEditText.setTag(viewObject);
        if(isClockedIn){
            textInputEditText.setHint("Clocked In");
        }else{
            textInputEditText.setHint("Clocked Out");
        }
        textInputEditText.setEms(9);
        textInputEditText.setFocusableInTouchMode(false);
        textInputEditText.setLayoutParams(params);
        textInputEditText.setText(Utility.getFormattedTime(time));
        textInputLayout.addView(textInputEditText);

        mShiftsWorked.addView(textInputLayout);

        textInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewObject tag = (ViewObject) view.getTag();
                if(tag.isClockedIn()){
                    showHourPicker(tag);
                }else{
                    showHourPicker(tag);
                }
            }
        });
    }

    public void showHourPicker(final ViewObject object) {
        final Calendar myCalender = Calendar.getInstance();
        myCalender.setTimeInMillis(object.getTime());
        int hour = myCalender.get(Calendar.HOUR);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    long newTimeSelected = myCalender.getTimeInMillis();
                    String id = object.getTimeId();
                    DatabaseReference updateTimeRef;
                    if(object.isClockedIn()) {
                        updateTimeRef = mFirebaseRef.child(ClockInEntry.CLOCK_IN_CHILD_STRING).child(id);
                        ClockInEntry clockInEntry = new ClockInEntry(newTimeSelected, id);
                        updateTimeRef.setValue(clockInEntry);
                    }else{
                        updateTimeRef = mFirebaseRef.child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).child(id);
                        ClockOutEntry clockOutEntry = new ClockOutEntry(newTimeSelected, id);
                        updateTimeRef.setValue(clockOutEntry);
                    }

                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, true);
        timePickerDialog.setTitle("Update time");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }
}

package com.trevorwiebe.timeclock;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

            // iterate through the clockInTimeList and clockOutTimeList
            for(int t = 0; mSelectedClockInTimes.size() > t; t++){

                // whole container layout
                LinearLayout containerLayout = new LinearLayout(this);
                LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                containerLayout.setOrientation(LinearLayout.HORIZONTAL);
                containerLayout.setLayoutParams(containerLayoutParams);
                containerLayout.setWeightSum(2);

                // column layout
                LinearLayout columnLayout = new LinearLayout(this);
                LinearLayout.LayoutParams columnLayoutParams = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1.0f
                );
                columnLayout.setOrientation(LinearLayout.VERTICAL);
                columnLayout.setLayoutParams(columnLayoutParams);

                // delete button layout
                LinearLayout deleteLayout = new LinearLayout(this);
                LinearLayout.LayoutParams deleteLayoutParams = new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                deleteLayout.setGravity(Gravity.CENTER_VERTICAL);
                deleteLayout.setOrientation(LinearLayout.VERTICAL);
                deleteLayout.setLayoutParams(deleteLayoutParams);

                // clock in entries
                long time = mSelectedClockInTimes.get(t).getClockInTime();
                String timeId = mSelectedClockInTimes.get(t).getEntryId();
                ViewObject viewObject1 = new ViewObject(true, timeId, t, time);
                addTextInputEditText(columnLayout, viewObject1);

//                // clock out entries
                if(t < mSelectedClockOutTimes.size()){

                    long time2 = mSelectedClockOutTimes.get(t).getClockOutTime();
                    String timeOutId = mSelectedClockOutTimes.get(t).getEntryId();
                    ViewObject viewObject2 = new ViewObject(false, timeOutId, t, time2);
                    addTextInputEditText(columnLayout, viewObject2);

                    // actual delete button
                    ImageButton deleteImageButton = new ImageButton(this);
                    LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    String timeString = Long.toString(time);
                    String time2String = Long.toString(time2);
                    deleteImageButton.setTag("0" + " " + "1" + " " + timeId + " " + timeOutId + " " + t + " " + timeString + " " + time2String);
                    deleteImageButton.setLayoutParams(deleteButtonParams);
                    deleteImageButton.setImageResource(R.drawable.ic_delete_black_24dp);
                    deleteImageButton.setBackgroundColor(Color.TRANSPARENT);
                    deleteLayout.addView(deleteImageButton);
                    deleteImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            String tag = (String) view.getTag();
                            String[] splitTag = tag.split(" ");
                            final String clockInId = splitTag[2];
                            final String clockOutId = splitTag[3];
                            int viewId = Integer.parseInt(splitTag[4]);
                            long clockInTime = Long.parseLong(splitTag[5]);
                            long clockOutTime = Long.parseLong(splitTag[6]);

                            View deleteShiftView = LayoutInflater.from(EditCurrentShiftActivity.this).inflate(R.layout.dialog_delete_shift, null);
                            TextView deleteClockInTimeTextView = deleteShiftView.findViewById(R.id.delete_clock_in_time_text_view);
                            TextView deleteClockOutTimeTextView = deleteShiftView.findViewById(R.id.delete_clock_out_time_text_view);
                            deleteClockInTimeTextView.setText("Clocked in at: " + Utility.getFormattedTime(clockInTime));
                            deleteClockOutTimeTextView.setText("Clocked out at: " + Utility.getFormattedTime(clockOutTime));
                            AlertDialog.Builder deleteShiftDialog = new AlertDialog.Builder(EditCurrentShiftActivity.this)
                                    .setTitle("Are you sure you want to delete this shift?")
                                    .setView(deleteShiftView)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            mFirebaseRef.child(ClockInEntry.CLOCK_IN_CHILD_STRING).child(clockInId).setValue(null);
                                            mFirebaseRef.child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).child(clockOutId).setValue(null);

                                            Snackbar deleteShiftSnackBar = Snackbar.make(view, "Shift deleted successfully", Snackbar.LENGTH_LONG);
                                            deleteShiftSnackBar.setAction("Undo", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Toast.makeText(EditCurrentShiftActivity.this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            deleteShiftSnackBar.show();

                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                            deleteShiftDialog.show();
                        }
                    });

                }

                containerLayout.addView(columnLayout);
                containerLayout.addView(deleteLayout);
                mShiftsWorked.addView(containerLayout);

                // this is the spacer view
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

    private void addTextInputEditText(LinearLayout columnLayout, ViewObject viewObject){

        // TextInputLayout
        LinearLayout.LayoutParams textInputParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(textInputParams);
        textInputLayout.setTag(viewObject);


        // TextInputEditText
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels1 = (int) (24*scale + 0.5f);
        int dpAsPixels2 = (int) (8*scale + 0.5f);
        params.setMargins(dpAsPixels1, dpAsPixels2, dpAsPixels1, dpAsPixels2);

        TextInputEditText textInputEditText = new TextInputEditText(this);
        textInputEditText.setTag(viewObject);
        if(viewObject.isClockedIn()){
            textInputEditText.setHint("Clocked In");
        }else{
            textInputEditText.setHint("Clocked Out");
        }
        textInputEditText.setFocusableInTouchMode(false);
        textInputEditText.setLayoutParams(params);
        textInputEditText.setText(Utility.getFormattedTime(viewObject.getTime()));
        textInputLayout.addView(textInputEditText);

        columnLayout.addView(textInputLayout);

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

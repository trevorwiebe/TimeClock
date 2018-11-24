package com.trevorwiebe.timeclock.adapters;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.trevorwiebe.timeclock.R;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class EditCurrentShiftRvAdapter extends RecyclerView.Adapter<EditCurrentShiftRvAdapter.EditShiftViewHolder> {

    private static final String TAG = "EditCurrentShiftRvAdapt";

    private ArrayList<ClockInEntry> mClockInList;
    private ArrayList<ClockOutEntry> mClockOutList;
    private Context mContext;
    private DatabaseReference mFirebaseRef;

    public EditCurrentShiftRvAdapter(Context context, ArrayList<ClockInEntry> clockInEntries, ArrayList<ClockOutEntry> clockOutEntries, DatabaseReference firebaseRef){
        this.mContext = context;
        this.mClockInList = clockInEntries;
        this.mClockOutList = clockOutEntries;
        this.mFirebaseRef = firebaseRef;
    }

    @Override
    public int getItemCount() {
        if(mClockInList == null)return 0;
        return mClockInList.size();
    }

    @NonNull
    @Override
    public EditShiftViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.list_edit_current_shift, viewGroup, false);
            return new EditShiftViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull EditShiftViewHolder editShiftViewHolder, int i) {

            long clockInTime = mClockInList.get(i).getClockInTime();
            long clockOutTime = mClockOutList.get(i).getClockOutTime();

            editShiftViewHolder.mClockInTime.setText(Utility.getFormattedTime(clockInTime));
            editShiftViewHolder.mClockOutTime.setText(Utility.getFormattedTime(clockOutTime));

            editShiftViewHolder.mClockInTime.setTag(mClockInList.get(i));
            editShiftViewHolder.mClockOutTime.setTag(mClockOutList.get(i));

            String clockInTimeString = Long.toString(clockInTime);
            String clockOutTimeString = Long.toString(clockOutTime);

            editShiftViewHolder.mDeleteEntryButton.setTag(mClockInList.get(i).getEntryId() + " " + mClockOutList.get(i).getEntryId() + " " + clockInTimeString + " " + clockOutTimeString);

            editShiftViewHolder.mClockInTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClockInEntry clockInEntry = (ClockInEntry) view.getTag();
                    long clockInTime = clockInEntry.getClockInTime();
                    String timeId = clockInEntry.getEntryId();
                    showHourPicker(clockInTime, timeId, true);
                }
            });
            editShiftViewHolder.mClockOutTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClockOutEntry clockOutEntry = (ClockOutEntry) view.getTag();
                    long clockOutTime = clockOutEntry.getClockOutTime();
                    String timeId = clockOutEntry.getEntryId();
                    showHourPicker(clockOutTime, timeId, false);
                }
            });
            editShiftViewHolder.mDeleteEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String string = (String) view.getTag();
                    String[] splitString = string.split(" ");

                    final String clockInId = splitString[0];
                    final String clockOutId = splitString[1];

                    long clockInTime = Long.parseLong(splitString[2]);
                    long clockOutTime = Long.parseLong(splitString[3]);;

                    View deleteShiftView = LayoutInflater.from(mContext).inflate(R.layout.dialog_delete_shift, null);
                    TextView deleteClockInTimeTextView = deleteShiftView.findViewById(R.id.delete_clock_in_time_text_view);
                    TextView deleteClockOutTimeTextView = deleteShiftView.findViewById(R.id.delete_clock_out_time_text_view);
                    deleteClockInTimeTextView.setText("Clocked in at: " + Utility.getFormattedTime(clockInTime));
                    deleteClockOutTimeTextView.setText("Clocked out at: " + Utility.getFormattedTime(clockOutTime));
                    AlertDialog.Builder deleteShiftDialog = new AlertDialog.Builder(mContext)
                            .setTitle("Are you sure you want to delete this shift?")
                            .setView(deleteShiftView)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    deleteItemFromClockInList(mClockInList, clockInId);
                                    deleteItemFromClockOutList(mClockOutList, clockOutId);

                                    mFirebaseRef.child(ClockInEntry.CLOCK_IN_CHILD_STRING).child(clockInId).setValue(null);
                                    mFirebaseRef.child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).child(clockOutId).setValue(null);

                                    notifyDataSetChanged();

                                    Snackbar deleteShiftSnackBar = Snackbar.make(view, "Shift deleted successfully", Snackbar.LENGTH_LONG);
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

    public class EditShiftViewHolder extends RecyclerView.ViewHolder{

        private TextInputEditText mClockInTime;
        private TextInputEditText mClockOutTime;
        private Button mDeleteEntryButton;

        public EditShiftViewHolder(View view){
            super(view);
            mClockInTime = view.findViewById(R.id.edit_clock_in_time);
            mClockOutTime = view.findViewById(R.id.edit_clock_out_time);
            mDeleteEntryButton = view.findViewById(R.id.delete_shift_entry);
        }
    }

    private void showHourPicker(final long oldTime, final String timeId, final boolean isClockedIn) {
        final Calendar myCalender = Calendar.getInstance();
        myCalender.setTimeInMillis(oldTime);
        int hour = myCalender.get(Calendar.HOUR);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    myCalender.set(Calendar.HOUR, hourOfDay);
                    myCalender.set(Calendar.MINUTE, minute);
                    long newTimeSelected = myCalender.getTimeInMillis();
                    DatabaseReference updateTimeRef;
                    if(isClockedIn) {
                        updateClockInListList(mClockInList, oldTime, newTimeSelected);
                        updateTimeRef = mFirebaseRef.child(ClockInEntry.CLOCK_IN_CHILD_STRING).child(timeId);
                        ClockInEntry clockInEntry = new ClockInEntry(newTimeSelected, timeId);
                        updateTimeRef.setValue(clockInEntry);
                    }else{
                        updateClockOutListList(mClockOutList, oldTime, newTimeSelected);
                        updateTimeRef = mFirebaseRef.child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).child(timeId);
                        ClockOutEntry clockOutEntry = new ClockOutEntry(newTimeSelected, timeId);
                        updateTimeRef.setValue(clockOutEntry);
                    }
                    notifyDataSetChanged();
                }
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Update time");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void updateClockInListList(ArrayList<ClockInEntry> list, long oldTime, long newTime){
        for(int r = 0; list.size() > r; r++){
            ClockInEntry clockInEntry = list.get(r);
            long selection = clockInEntry.getClockInTime();
            if(selection == oldTime){
                clockInEntry.setClockInTime(newTime);
                break;
            }
        }
    }

    private void updateClockOutListList(ArrayList<ClockOutEntry> list, long oldTime, long newTime){
        for(int r = 0; list.size() > r; r++){
            ClockOutEntry clockOutEntry = list.get(r);
            long selection = clockOutEntry.getClockOutTime();
            if(selection == oldTime){
                clockOutEntry.setClockOutTime(newTime);
                break;
            }
        }
    }

    private void deleteItemFromClockInList(ArrayList<ClockInEntry> list, String id){
        for(int g = 0; list.size() > g; g++){
            ClockInEntry clockInEntry = list.get(g);
            String selection = clockInEntry.getEntryId();
            if(selection.equals(id)){
                list.remove(clockInEntry);
                break;
            }
        }
    }

    private void deleteItemFromClockOutList(ArrayList<ClockOutEntry> list, String id){
        for(int g = 0; list.size() > g; g++){
            ClockOutEntry clockOutEntry = list.get(g);
            String selection = clockOutEntry.getEntryId();
            if(selection.equals(id)){
                list.remove(clockOutEntry);
                break;
            }
        }
    }

}

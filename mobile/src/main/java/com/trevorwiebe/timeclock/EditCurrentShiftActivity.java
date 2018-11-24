package com.trevorwiebe.timeclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.trevorwiebe.timeclock.adapters.EditCurrentShiftRvAdapter;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;

public class EditCurrentShiftActivity extends AppCompatActivity {

    private static final String TAG = "TestEditCurrentShiftAct";

    private ArrayList<ClockInEntry> mSelectedClockInTimes;
    private ArrayList<ClockOutEntry> mSelectedClockOutTimes;
    private DatabaseReference mFirebaseRef;

    // widgets
    private RecyclerView mRecyclerView;
    private TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_current_shift);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Intent selectedIntent = getIntent();
        //set the title of the activity to the date you are editing
        long selectedTime = selectedIntent.getLongExtra("selectedDate", 0);
        setTitle(Utility.getFormattedDate(selectedTime));

        mSelectedClockInTimes = (ArrayList<ClockInEntry>) selectedIntent.getSerializableExtra("selectedClockInTimes");
        mSelectedClockOutTimes = (ArrayList<ClockOutEntry>) selectedIntent.getSerializableExtra("selectedClockOutTimes");

        mRecyclerView = findViewById(R.id.edit_current_shift_rv);
        mEmptyText = findViewById(R.id.no_current_shift);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        EditCurrentShiftRvAdapter editCurrentShiftRvAdapter = new EditCurrentShiftRvAdapter(this, mSelectedClockInTimes, mSelectedClockOutTimes, mFirebaseRef);
        mRecyclerView.setAdapter(editCurrentShiftRvAdapter);

        if(mSelectedClockInTimes.size() == 0){
            mEmptyText.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mEmptyText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}

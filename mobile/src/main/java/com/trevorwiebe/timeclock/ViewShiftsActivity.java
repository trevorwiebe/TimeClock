package com.trevorwiebe.timeclock;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.timeclock.adapters.ViewShiftsRvAdapter;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewShiftsActivity extends AppCompatActivity {

    private FirebaseDatabase mBaseRef = FirebaseDatabase.getInstance();
    private RecyclerView mViewShiftsRv;
    private ViewShiftsRvAdapter mViewShiftsRvAdapter;
//    private DatabaseReference mClockInRef;
//    private DatabaseReference mClockOutRef;
//    private ValueEventListener mClockInListener;
//    private ValueEventListener mClockOutListener;

    private ArrayList<Long> mClockInList = new ArrayList<>();
    private ArrayList<Long> mClockOutList = new ArrayList<>();
    private ArrayList<Long> mDays = new ArrayList<>();

    private static final String TAG = "ViewShiftsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shifts);

        mViewShiftsRv = findViewById(R.id.view_shifts_rv);
        mViewShiftsRv.setLayoutManager(new LinearLayoutManager(this));
        mViewShiftsRvAdapter = new ViewShiftsRvAdapter(this, mClockInList, mClockOutList, mDays);
        mViewShiftsRv.setAdapter(mViewShiftsRvAdapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){

//            mClockInRef = mBaseRef.getReference("users").child(user.getUid()).child(ClockInEntry.CLOCK_IN_CHILD_STRING);
//            mClockOutRef = mBaseRef.getReference("users").child(user.getUid()).child(ClockOutEntry.CLOCK_OUT_CHILD_STRING);

            final String userId = user.getUid();
            DatabaseReference clockInRef = mBaseRef.getReference("users/" + userId + "/clockIn");
            clockInRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ClockInEntry clockInEntry = snapshot.getValue(ClockInEntry.class);
                        if(clockInEntry != null) {
                            mClockInList.add(clockInEntry.getClockInTime());
                        }
                    }
                    DatabaseReference clockOutRef = mBaseRef.getReference("users/" + userId + "/clockOut");
                    clockOutRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                ClockOutEntry clockOutEntry = snapshot.getValue(ClockOutEntry.class);
                                if(clockOutEntry != null) {
                                    mClockOutList.add(clockOutEntry.getClockOutTime());
                                }
                            }
                            mViewShiftsRvAdapter.swapData(mClockInList, mClockOutList, mDays);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if(user.getMetadata() != null){

                // get when the user was created and calculate the beginning of the day
                long beginningOfTheDayWhenUserWasCreated = Utility.getBeginningOfDay(user.getMetadata().getCreationTimestamp());

                // get the beginning of today
                long beginningOfToday = Utility.getBeginningOfDay(System.currentTimeMillis());

                // add the beginning of the day when the user was created
                mDays.add(beginningOfTheDayWhenUserWasCreated);

                // calculate how many milliseconds are in a day
                long msPerDay = 86400 * 1000;

                /*
                This loop is used to populate an array with the millisecond
                time of the beginning of every day from the time the user
                was created until today.
                 */
                while (mDays.get(mDays.size() - 1) < (beginningOfToday - msPerDay)){

                    // get the last day that was added to the array
                    long lastDayAdded = mDays.get(mDays.size() -1);

                    // calculate how many milliseconds the starting of the next day will be at
                    long nextDay = msPerDay + lastDayAdded;

                    // add it to the array
                    mDays.add(nextDay);
                }
            }
        }
//
//        mClockInListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mClockOutRef.addListenerForSingleValueEvent(mClockOutListener);
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    ClockInEntry clockInEntry = snapshot.getValue(ClockInEntry.class);
//                    mClockInList.add(clockInEntry);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//
//        mClockInRef.addListenerForSingleValueEvent(mClockInListener);
//
//        mClockOutListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    ClockOutEntry clockOutEntry = snapshot.getValue(ClockOutEntry.class);
//                    mClockOutList.add(clockOutEntry);
//                }
//                mViewShiftsRvAdapter.swapData(mClockInList, mClockOutList, mDays);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
    }

}

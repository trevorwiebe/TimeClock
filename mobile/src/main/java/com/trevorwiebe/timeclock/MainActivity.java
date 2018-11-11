package com.trevorwiebe.timeclock;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;
import com.trevorwiebe.timeclock.utils.Utility;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private long mLastClockInTime;
    private long mLastClockOutTime;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mClockInRef;
    private DatabaseReference mClockOutRef;
    private Query mClockOutQuery;
    private Query mClockInQuery;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private ValueEventListener mClockInListener;
    private ValueEventListener mClockOutListener;

    private TextView mWorkingStatus;
    private Button mClockInBtn;
    private Button mClockOutBtn;
    private ProgressBar mConnectingToDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser == null) {
                    // not signed in
                    Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signInIntent);
                } else {
                    // signed in
                    mWorkingStatus = findViewById(R.id.working_status);
                    mClockInBtn = findViewById(R.id.clock_in_btn);
                    mClockOutBtn = findViewById(R.id.clock_out_btn);
                    mConnectingToDb = findViewById(R.id.connecting_to_db);

                    if (mClockInQuery == null || mClockOutQuery == null) {
                        mClockInQuery = database.getReference("users").child(mUser.getUid()).child(ClockInEntry.CLOCK_IN_CHILD_STRING).limitToLast(1);
                        mClockOutQuery = database.getReference("users").child(mUser.getUid()).child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).limitToLast(1);
                    }

                    // once the user is confirmed signed in, begin loading the clock in times from the database
                    mClockInQuery.addValueEventListener(mClockInListener);

                }
            }
        };

        mClockInListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClockInEntry clockInEntry = snapshot.getValue(ClockInEntry.class);
                    if (clockInEntry != null) {
                        mLastClockInTime = clockInEntry.getClockInTime();
                    }
                }

                // once the clock in times are loaded, begin loading the clock out times
                mClockOutQuery.addValueEventListener(mClockOutListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mClockOutListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClockOutEntry clockOutEntry = snapshot.getValue(ClockOutEntry.class);
                    if (clockOutEntry != null) {
                        mLastClockOutTime = clockOutEntry.getClockOutTime();
                    }
                }

                mConnectingToDb.setVisibility(View.INVISIBLE);

                if (mLastClockInTime > mLastClockOutTime) {
                    // user is clocked in
                    mWorkingStatus.setText(getResources().getString(R.string.clocked_in));

                    mClockOutBtn.setVisibility(View.VISIBLE);
                    mClockInBtn.setVisibility(View.GONE);
                } else {
                    // user is clocked out
                    mWorkingStatus.setText(getResources().getString(R.string.clocked_out));

                    mClockOutBtn.setVisibility(View.GONE);
                    mClockInBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClockInQuery == null && mClockOutQuery == null && mUser != null) {
            mClockInQuery = database.getReference("users").child(mUser.getUid()).child(ClockInEntry.CLOCK_IN_CHILD_STRING).limitToLast(1);
            mClockOutQuery = database.getReference("users").child(mUser.getUid()).child(ClockOutEntry.CLOCK_OUT_CHILD_STRING).limitToLast(1);
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        mClockInQuery.removeEventListener(mClockInListener);
        mClockOutQuery.removeEventListener(mClockOutListener);
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view_shift) {
            Intent view_shifts_intent = new Intent(MainActivity.this, ViewShiftsActivity.class);
            startActivity(view_shifts_intent);
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clockIn(View view) {
        // Push clock in time to database
        if (mUser != null) {
            DatabaseReference pushRef = mClockInRef.push();
            String entryId = pushRef.getKey();
            ClockInEntry clockInEntry = new ClockInEntry(System.currentTimeMillis(), entryId);
            pushRef.setValue(clockInEntry);
            Snackbar.make(view, "Clocked in at " + Utility.getFormattedTime(), Snackbar.LENGTH_SHORT).show();
        }
    }

    public void clockOut(View view) {
        // Push clock out time to database
        if (mUser != null) {
            DatabaseReference pushRef = mClockOutRef.push();
            String entryId = pushRef.getKey();
            ClockOutEntry clockOutEntry = new ClockOutEntry(System.currentTimeMillis(), entryId);
            pushRef.setValue(clockOutEntry);
            Snackbar.make(view, "Clocked out at " + Utility.getFormattedTime(), Snackbar.LENGTH_SHORT).show();
        }
    }
}
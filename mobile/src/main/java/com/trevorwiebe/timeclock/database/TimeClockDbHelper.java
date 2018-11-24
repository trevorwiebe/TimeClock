package com.trevorwiebe.timeclock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TimeClockDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "time_clock.db";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_TIME_CLOCK_ENTRY_TABLE = "CREATE TABLE "
            + TimeClockContract.TimeClockInOrOutEntry.TABLE_NAME + " ("
            + TimeClockContract.TimeClockInOrOutEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + TimeClockContract.TimeClockInOrOutEntry.CLOCK_IN_CLOCK_OUT_TIME_COLUMN + " LONG NOT NULL, "
            + TimeClockContract.TimeClockInOrOutEntry.IS_CLOCKED_IN + " BOOLEAN NOT NULL);";

    public TimeClockDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TIME_CLOCK_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TimeClockContract.TimeClockInOrOutEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

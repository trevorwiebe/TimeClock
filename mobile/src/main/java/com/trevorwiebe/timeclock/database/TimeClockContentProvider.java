package com.trevorwiebe.timeclock.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TimeClockContentProvider extends ContentProvider {

    public static final int TIME_ENTRIES = 100;

    private static final UriMatcher sUriMatcher = buildUirMatcher();

    public static UriMatcher buildUirMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(TimeClockContract.AUTHORITY, TimeClockContract.PATH_TIME, TIME_ENTRIES);

        return uriMatcher;
    }

    TimeClockDbHelper mTimeClockDbHelper;

    @Override
    public boolean onCreate() {
        mTimeClockDbHelper = new TimeClockDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mTimeClockDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor informationCursor;

        switch (match){
            case TIME_ENTRIES:

                informationCursor = db.query(TimeClockContract.TimeClockInOrOutEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        informationCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return informationCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mTimeClockDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri locationUri;

        switch (match){

            case TIME_ENTRIES:
                long id = db.insert(TimeClockContract.TimeClockInOrOutEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    locationUri = ContentUris.withAppendedId(TimeClockContract.TimeClockInOrOutEntry.CONTENT_URI_CLOCK_IN_OR_OUT_ENTRY, id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return locationUri;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,@Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}

package com.trevorwiebe.timeclock.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class TimeClockContract {

    public static final String AUTHORITY = "com.trevorwiebe.timeclock";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TIME = "time";

    public static final class TimeClockInOrOutEntry implements BaseColumns {

        public static final Uri CONTENT_URI_CLOCK_IN_OR_OUT_ENTRY = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIME).build();

        public static final String TABLE_NAME = "clock_in_clock_out";

        public static final String CLOCK_IN_CLOCK_OUT_TIME_COLUMN = "clock_in_clock_out_time";
        public static final String IS_CLOCKED_IN = "is_clocked_in";

    }
}

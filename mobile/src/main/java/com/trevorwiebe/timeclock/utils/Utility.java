package com.trevorwiebe.timeclock.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by thisi on 10/30/2018.
 */

public class Utility {

    private static final String TAG = "Utility";

    public static String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm", Locale.getDefault());
        Date date = new Date();
        return formatter.format(date);
    }

    public static String getFormattedDate(Long msToFormat) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM/d/yy", Locale.getDefault());
        return format.format(msToFormat);
    }

    public static long getBeginningOfDay(long middleOfDay) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(middleOfDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String convertMillisecondsToHours(long millisToConvert) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millisToConvert),
                TimeUnit.MILLISECONDS.toMinutes(millisToConvert) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisToConvert)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millisToConvert) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisToConvert)));
    }
}

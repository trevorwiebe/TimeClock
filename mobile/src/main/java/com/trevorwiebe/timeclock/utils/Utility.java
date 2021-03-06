package com.trevorwiebe.timeclock.utils;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.trevorwiebe.timeclock.object.ClockInEntry;
import com.trevorwiebe.timeclock.object.ClockOutEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by thisi on 10/30/2018.
 */

public class Utility {

    private static final String TAG = "Utility";

    public static String getFormattedTime(Long date) {
        if(date == 0)return null;
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return formatter.format(date);
    }

    public static String getFormattedDate(Long msToFormat) {
        long msPerDay = 86400 * 1000;
        long currentTime = System.currentTimeMillis();
        if(currentTime - msToFormat < msPerDay){
            // it's today
            return "Today";
        }else if(currentTime - msToFormat< (msPerDay * 2)){
            // it's yesterday
            return "Yesterday";
        }else {
            SimpleDateFormat format = new SimpleDateFormat("EEE, MMM/d/yy", Locale.getDefault());
            return format.format(msToFormat);
        }
    }

    public static ArrayList<ClockInEntry> getClockInTimesForDateFromList(ArrayList<ClockInEntry> wholeList, long date){
        long msPerDay = 86400 * 1000;
        long tomorrow = date + msPerDay;
        ArrayList<ClockInEntry> selectedList = new ArrayList<>();
        for(int r = 0; wholeList.size() > r; r++){
            long timeInQuestion = wholeList.get(r).getClockInTime();
            if(timeInQuestion > date && timeInQuestion < tomorrow){
                String entryId = wholeList.get(r).getEntryId();
                ClockInEntry clockInEntry = new ClockInEntry(timeInQuestion, entryId);
                selectedList.add(clockInEntry);
            }
        }
        return selectedList;
    }

    public static ArrayList<ClockOutEntry> getClockOutTimesForDateFromList(ArrayList<ClockOutEntry> wholeList, long date){
        long msPerDay = 86400 * 1000;
        long tomorrow = date + msPerDay;
        ArrayList<ClockOutEntry> selectedList = new ArrayList<>();
        for(int r = 0; wholeList.size() > r; r++){
            long timeInQuestion = wholeList.get(r).getClockOutTime();
            if(timeInQuestion > date && timeInQuestion < tomorrow){
                String entryId = wholeList.get(r).getEntryId();
                ClockOutEntry clockOutEntry = new ClockOutEntry(timeInQuestion, entryId);
                selectedList.add(clockOutEntry);
            }
        }
        return selectedList;
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
        return String.format(Locale.getDefault(), "%2d hr, %2d min", TimeUnit.MILLISECONDS.toHours(millisToConvert), TimeUnit.MILLISECONDS.toMinutes(millisToConvert) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisToConvert)));
    }

    public static long getTimeFromWhenToString(String whenToString){
        long currentTime = System.currentTimeMillis();
        long msPerDay = 86400 * 1000;
        long msPerYear = 31536000000L;
        String[] splitString = whenToString.split(",");
        switch (splitString[0]){
            case "d":
                int perDays = Integer.parseInt(splitString[1]);
                long msToAddDays = perDays * msPerDay;
                return getBeginningOfDay(msToAddDays + currentTime);
            case "w":
                Log.d(TAG, "getTimeFromWhenToString: ");
                break;
            case "m":
                break;
            case "y":
                int perYears = Integer.parseInt(splitString[1]);
                long msToAddYears = perYears * msPerYear;
                return getBeginningOfDay(msToAddYears + currentTime);
            // TODO: 11/13/2018 read this article https://stackoverflow.com/questions/9141871/java-milliseconds-in-year to make sure we are doing this right here considering the leap years
            default:
                Log.e(TAG, "getTimeFromWhenToString: error parsing the first char of a whenToString");
        }
        return 2838758;
    }
}

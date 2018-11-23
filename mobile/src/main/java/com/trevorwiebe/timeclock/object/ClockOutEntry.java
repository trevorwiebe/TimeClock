package com.trevorwiebe.timeclock.object;

import android.os.Parcel;
import android.os.Parcelable;

public class ClockOutEntry implements Parcelable {

    public static final String CLOCK_OUT_CHILD_STRING = "clockOut";

    private long ClockOutTime;
    private String entryId;

    public ClockOutEntry(long clockOutTime, String entryId) {
        ClockOutTime = clockOutTime;
        this.entryId = entryId;
    }

    public ClockOutEntry(){}

    public long getClockOutTime() {
        return ClockOutTime;
    }

    public void setClockOutTime(long clockOutTime) {
        ClockOutTime = clockOutTime;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.ClockOutTime);
        dest.writeString(this.entryId);
    }

    protected ClockOutEntry(Parcel in) {
        this.ClockOutTime = in.readLong();
        this.entryId = in.readString();
    }

    public static final Creator<ClockOutEntry> CREATOR = new Creator<ClockOutEntry>() {
        @Override
        public ClockOutEntry createFromParcel(Parcel source) {
            return new ClockOutEntry(source);
        }

        @Override
        public ClockOutEntry[] newArray(int size) {
            return new ClockOutEntry[size];
        }
    };
}

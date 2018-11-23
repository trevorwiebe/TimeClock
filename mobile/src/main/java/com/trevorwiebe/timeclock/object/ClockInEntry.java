package com.trevorwiebe.timeclock.object;

import android.os.Parcel;
import android.os.Parcelable;

public class ClockInEntry implements Parcelable {

    public static final String CLOCK_IN_CHILD_STRING = "clockIn";

    private long clockInTime;
    private String entryId;

    public ClockInEntry(long clockInTime, String entryId) {
        this.clockInTime = clockInTime;
        this.entryId = entryId;
    }

    public ClockInEntry(){}

    public long getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(long clockInTime) {
        this.clockInTime = clockInTime;
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
        dest.writeLong(this.clockInTime);
        dest.writeString(this.entryId);
    }

    protected ClockInEntry(Parcel in) {
        this.clockInTime = in.readLong();
        this.entryId = in.readString();
    }

    public static final Creator<ClockInEntry> CREATOR = new Creator<ClockInEntry>() {
        @Override
        public ClockInEntry createFromParcel(Parcel source) {
            return new ClockInEntry(source);
        }

        @Override
        public ClockInEntry[] newArray(int size) {
            return new ClockInEntry[size];
        }
    };
}

package com.trevorwiebe.timeclock.object;

public class ClockInEntry {

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
}

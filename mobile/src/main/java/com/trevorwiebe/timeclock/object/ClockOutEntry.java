package com.trevorwiebe.timeclock.object;

public class ClockOutEntry {

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
}

package com.trevorwiebe.timeclock.object;

public class ViewObject {

    private boolean isClockedIn;
    private String timeId;
    private int viewId;
    private long time;

    public ViewObject(boolean isClockedIn, String timeId, int viewId, long time) {
        this.isClockedIn = isClockedIn;
        this.timeId = timeId;
        this.viewId = viewId;
        this.time = time;
    }

    public boolean isClockedIn() {
        return isClockedIn;
    }

    public void setClockedIn(boolean clockedIn) {
        isClockedIn = clockedIn;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }

    public String getTimeId() {
        return timeId;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

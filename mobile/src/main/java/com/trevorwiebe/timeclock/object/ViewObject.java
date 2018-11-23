package com.trevorwiebe.timeclock.object;

public class ViewObject {

    private boolean isClockedIn;
    private int viewId;

    public ViewObject(boolean isClockedIn, int viewId) {
        this.isClockedIn = isClockedIn;
        this.viewId = viewId;
    }

    public boolean isClockedIn() {
        return isClockedIn;
    }

    public void setClockedIn(boolean clockedIn) {
        isClockedIn = clockedIn;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}

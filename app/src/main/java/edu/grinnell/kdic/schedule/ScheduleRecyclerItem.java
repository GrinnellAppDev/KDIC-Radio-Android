package edu.grinnell.kdic.schedule;

public class ScheduleRecyclerItem {
    private int mViewType;
    private String mS1;
    private String mS2;


    public ScheduleRecyclerItem(int viewType, String s1, String s2) {
        this.mViewType = viewType;
        this.mS1 = s1;
        this.mS2 = s2;
    }

    public int getViewType() {
        return mViewType;
    }

    public String getS1() {
        return mS1;
    }

    public String getS2() {
        return mS2;
    }
}

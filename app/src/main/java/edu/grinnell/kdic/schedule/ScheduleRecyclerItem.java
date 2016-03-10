package edu.grinnell.kdic.schedule;

public class ScheduleRecyclerItem {
    private int viewType;
    private String s1;
    private String s2;


    public ScheduleRecyclerItem(int viewType, String s1, String s2) {
        this.viewType = viewType;
        this.s1 = s1;
        this.s2 = s2;
    }

    public int getViewType() {
        return viewType;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }
}

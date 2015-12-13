package edu.grinnell.kdic;


import edu.grinnell.kdic.schedule.ScheduleRecyclerItem;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

public class Show extends ScheduleRecyclerItem {

    private String day;

    public Show(String title, String date) {
        super(ScheduleRecyclerViewAdapter.CARD, title, date);
    }

    public String getTitle() {
        return super.getS1();
    }

    public String getTime() {
        return super.getS2();
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

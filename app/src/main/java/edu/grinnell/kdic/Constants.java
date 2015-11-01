package edu.grinnell.kdic;

public abstract class Constants {
    public static final String STREAM_URL = "http://";
    public static final String SCHEDULE_URL = "http://kdic.grinnell.edu/scheduleScript.php";

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String FIRST_RUN = "first_run";


    // for parsing JSON
    public static final String JSON_DAYS = "days";
    public static final String JSON_TIMES = "times";
    public static final String JSON_DATA = "data";

    public static final String DAY = "day";
    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};


}

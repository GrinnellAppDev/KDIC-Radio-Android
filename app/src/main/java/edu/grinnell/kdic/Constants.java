package edu.grinnell.kdic;

public abstract class Constants {
    public static final String STREAM_URL = "http://kdic.grinnell.edu/stream";
    public static final String SCHEDULE_URL = "http://prabirmsp.com/kdic.json";

    public static final String ACTION_STREAM_PLAY_PAUSE = "edu.grinnell.kdic.action.PLAY_PAUSE";
    public static final String ACTION_STOP_RADIO_SERVICE = "edu.grinnell.kdic.action.STOP_RADIO";

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String FIRST_RUN = "first_run";

    public static final String BLOG_TAG = "BlogFragment";
    public static final String BLOG_URL = "http://kdic.grinnell.edu/";


    // for parsing JSON
    public static final String JSON_DAYS = "days";
    public static final String JSON_TIMES = "times";
    public static final String JSON_DATA = "data";

    public static final String DAY = "day";
    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};


}

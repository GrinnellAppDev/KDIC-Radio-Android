package edu.grinnell.kdic;

public abstract class Constants {
    public static final String STREAM_URL = "http://kdic.grinnell.edu/stream";
    public static final String SCHEDULE_URL = "http://prabirmsp.com/kdic.json";

    public static final String ACTION_STREAM_PLAY_PAUSE = "edu.grinnell.kdic.action.PLAY_PAUSE";
    public static final String ACTION_STOP_RADIO_SERVICE = "edu.grinnell.kdic.action.STOP_RADIO";

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String FIRST_RUN = "first_run";

    public static final long STOP_STREAM_DELAY = 30 * 1000;
    public static final int NOTIFICATION_ID = 1;
    public static final String WIFI_TAG = "myWifiLock";


    public static final float MEDIA_PLAYER_LEFT_VOLUME = 1.0f;
    public static final float MEDIA_PLAYER_RIGHT_VOLUME = 1.0f;


    public static final int REQUEST_CODE = 0;

    public static final float MEDIA_PLAYER_LEFT_VOLUME_LOW = 0.2f;
    public static final float MEDIA_PLAYER_RIGHT_VOLUME_LOW = 0.2f;


    // for parsing JSON
    public static final String JSON_DAYS = "days";
    public static final String JSON_TIMES = "times";
    public static final String JSON_DATA = "data";

    public static final String DAY = "day";
    public static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday",
            "Friday", "Saturday", "Sunday"};


}

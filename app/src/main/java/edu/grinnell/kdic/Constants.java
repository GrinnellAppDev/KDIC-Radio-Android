package edu.grinnell.kdic;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

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


    //for splash activity
    public static final int SPLASH_DELAY = 1000;

    //for BlogWebViewFragment
    public static final String BLOG_URL = "http://kdic.grinnell.edu/";

    // for MainActivity.java
    public static final float INITIAL_ANGLE_DEGREES = 0;
    public static final float FINAL_ANGLE_DEGREES = 360;
    public static final int PIVOT_X_TYPE = RELATIVE_TO_SELF;
    public static final int PIVOT_Y_TYPE = RELATIVE_TO_SELF;
    public static final float PIVOT_X_COORDINATE_MIDDLE = 0.5f;
    public static final float PIVOT_Y_COORDINATE_MIDDLE = 0.5f;
    public static final int MS_ANIMATION_DURATION_LOADING_SPIN = 1000;
    public static final float INITIAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT= 0f;
    public static final float FINAL_ALPHA_LEVEL_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT= 1f;
    public static final int MS_DURATION_FADE_IN_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT= 200;
    public static final int MS_DURATION_FADE_IN_DELAY_SHOW_INFO_HIDE_VISUALIZE_FRAGMENT= 100;
    public static final float INITIAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT= 0;
    public static final float INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT = 0;
    public static final float FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT = 0;
    public static final int MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_HIDE_VISUALIZE_FRAGMENT = 200;
    public static final float INITIAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT=  1f;
    public static final float FINAL_ALPHA_LEVEL_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT = 0f;
    public static final int MS_DURATION_FADE_IN_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT = 300;
    public static final int MS_DURATION_FADE_IN_DELAY_SHOW_INFO_SHOW_VISUALIZE_FRAGMENT = 100;
    public static final float FINAL_CHANGE_IN_X_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT= 0;
    public static final float INITIAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT= 0;
    public static final float FINAL_CHANGE_IN_Y_COORDINATE_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT= 0;
    public static final int MS_ANIMATION_DURATION_PLAY_PAUSE_BUTTON_SHOW_VISUALIZE_FRAGMENT= 200;
}

package edu.grinnell.kdic.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.Show;

public class Schedule {
    private ScheduleDbHelper dbHelper;
    private SQLiteDatabase db;

    public static final String TEXT_TYPE = " TEXT";
    public static final String TINYINT_TYPE = " TINYINT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Entry.COLUMN_DAY + TEXT_TYPE + COMMA_SEP +
                    Entry.COLUMN_TIME + TEXT_TYPE + COMMA_SEP +
                    Entry.COLUMN_SHOW_TITLE + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    public Schedule(Context context) {
        dbHelper = new ScheduleDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /* Inner class that defines the table contents */
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_SHOW_TITLE = "show_title";
    }

    // CRUD Operations

    /**
     * Gets show name given a day and time if exists.
     *
     * @param day
     * @param time
     * @return String showName or null if it is not found.
     */
    public Show getShow(String day, String time) {
        // which columns to see for query
        String[] projection = {Entry.COLUMN_SHOW_TITLE};
        String selection = Entry.COLUMN_DAY + "='" + day + "' AND " +
                Entry.COLUMN_TIME + "='" + time + "'";

        Cursor c = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        Show show = null;
        if (!c.isAfterLast()) { // check if anything was found
            c.moveToFirst();
            String title = c.getString(c.getColumnIndex(Entry.COLUMN_SHOW_TITLE));
            show = new Show(title, time);
        }
        c.close();
        return show;
    }

    /**
     * Gets show by the show name.
     *
     * @param name
     * @return
     */
    public Show getShow(String name) {
        // which columns to see for query
        String[] projection = {Entry.COLUMN_SHOW_TITLE, Entry.COLUMN_DAY, Entry.COLUMN_TIME};
        String selection = Entry.COLUMN_SHOW_TITLE + "='" + name + "'";

        Cursor c = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        Show show = null;
        if (!c.isAfterLast()) { // check if anything was found
            c.moveToFirst();
            String day = c.getString(c.getColumnIndex(Entry.COLUMN_DAY));
            String time = c.getString(c.getColumnIndex(Entry.COLUMN_TIME));
            show = new Show(name, time);
            show.setDay(day);
        }
        c.close();
        return show;
    }

    /**
     * gets the current show
     *
     * @param context context
     * @return the current show playing or @null if nothing is playing
     */
    public static Show getCurrentShow(Context context) {
        Schedule schedule = new Schedule(context);
        Date today = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String todayDayOfWeek = dayFormat.format(today);
        Show show = schedule.getShow(todayDayOfWeek, new SimpleDateFormat("h:00 a").format(today));
        schedule.close();
        return show;
    }

    /**
     * Gets all show names for a day.
     *
     * @param day
     * @return
     */
    public ArrayList<Show> getShowByDay(String day) {
        ArrayList<Show> ret = new ArrayList<>();

        // which columns to see for query
        String[] projection = {Entry._ID, Entry.COLUMN_TIME, Entry.COLUMN_SHOW_TITLE};
        String selection = Entry.COLUMN_DAY + "='" + day + "'";

        Cursor c = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                Entry._ID                                 // The sort order
        );

        c.moveToFirst();

        while (!c.isAfterLast()) {
            String title = c.getString(c.getColumnIndex(Entry.COLUMN_SHOW_TITLE));
            String time = c.getString(c.getColumnIndex(Entry.COLUMN_TIME));
            ret.add(new Show(title, time));
            c.moveToNext();
        }

        c.close();
        return ret;
    }

    public void updateSchedule(String json) throws JSONException {
        db.execSQL(SQL_DELETE_ENTRIES); // delete old schedule if exists
        dbHelper.onCreate(db); // create new schedule table
        JSONObject jsonObject = new JSONObject(json);
        JSONObject data = jsonObject.getJSONObject(Constants.JSON_DATA);
        JSONArray times = jsonObject.getJSONArray(Constants.JSON_TIMES);
        JSONArray days = jsonObject.getJSONArray(Constants.JSON_DAYS);

        for (int i = 0; i < days.length(); i++) { // iterate through days
            String day = days.getString(i);
            JSONObject dayObject = data.getJSONObject(day);

            for (int j = 0; j < times.length(); j++) { // iterate through times
                String time = times.getString(j);
                String showName = dayObject.getString(time).trim();

                if (!showName.equals("")) {

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(Entry.COLUMN_DAY, day);
                    values.put(Entry.COLUMN_TIME, time);
                    values.put(Entry.COLUMN_SHOW_TITLE, showName);

                    // insert values into the db
                    db.insert(Entry.TABLE_NAME, null, values);
                }
            }
        }
    }
}

package edu.grinnell.kdic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Schedule {
    private Context context;
    private SchedueDbHelper dbHelper;
    private SQLiteDatabase db;

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY," +
                    Entry.COLUMN_DAY + TEXT_TYPE + COMMA_SEP +
                    Entry.COLUMN_TIME + TEXT_TYPE + COMMA_SEP +
                    Entry.COLUMN_SHOW_TITLE + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    public Schedule(Context context) {
        this.context = context;
        dbHelper = new SchedueDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    /* Inner class that defines the table contents */
    public static abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "schedule";
        public static final String COLUMN_ENTRY_ID = "id";
        public static final String COLUMN_DAY = "day";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_SHOW_TITLE = "show_title";
    }

    // CRUD Operations

    public String getShowName(String day, String time) {
        // which columns to see for query
        String[] projection = {Entry.COLUMN_SHOW_TITLE};
        String selection = Entry.COLUMN_DAY + "='?' AND " + Entry.COLUMN_TIME + "='?'";
        String[] selectionArgs = {day, time};

        Cursor c = db.query(
                Entry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        c.moveToFirst();
        String title = c.getString(c.getColumnIndex(Entry.COLUMN_SHOW_TITLE));
        c.close();
        return title;
    }

    public void updateSchedule(String json) throws JSONException {
        db.execSQL(SQL_DELETE_ENTRIES); // delete old schedule if exists
        dbHelper.onCreate(db); // create new schedule table
        JSONObject jsonObject = new JSONObject(json);
        JSONObject data = jsonObject.getJSONObject(Constants.DATA);
        JSONArray times = jsonObject.getJSONArray(Constants.TIMES);
        JSONArray days = jsonObject.getJSONArray(Constants.DAYS);

        for (int i = 0; i < days.length(); i++) { // iterate through days
            String day = days.getString(i);
            JSONObject dayObject = data.getJSONObject(day);

            for (int j = 0; j < times.length(); j++) { // iterate through times
                String time = times.getString(j);
                String showName = dayObject.getString(time);

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

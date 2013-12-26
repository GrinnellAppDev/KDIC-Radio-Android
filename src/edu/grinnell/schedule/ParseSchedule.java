package edu.grinnell.schedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class ParseSchedule extends
		AsyncTask<String, ArrayList<Show>, ArrayList<Show>> {

	public static String url = "http://tcdb.grinnell.edu/apps/glicious/KDIC/schedule.json";

	// JSON Node names
	private static final String TAG_MONDAY = "Monday";
	private static final String TAG_TUESDAY = "Tuesday";
	private static final String TAG_WEDNESDAY = "Wednesday";
	private static final String TAG_THURSDAY = "Thursday";
	private static final String TAG_FRIDAY = "Friday";
	private static final String TAG_SATURDAY = "Saturday";
	private static final String TAG_SUNDAY = "Sunday";

	private static final String TAG_NAME = "name";
	private static final String TAG_START = "start_time";
	private static final String TAG_END = "end_time";

	public ArrayList<Show> Schedule = new ArrayList<Show>();
	public Boolean parsed = false;
	
	// contacts JSONArray
	JSONArray monday = null;
	JSONArray tuesday = null;
	JSONArray wednesday = null;
	JSONArray thursday = null;
	JSONArray friday = null;
	JSONArray saturday = null;
	JSONArray sunday = null;

	// Creating JSON Parser instance
	JSONParser jParser = new JSONParser();

	@Override
	protected ArrayList<Show> doInBackground(String... urls) {
		url = urls[0];
		// getting JSON string from URL
		JSONObject json = jParser.getJSONFromUrl(url);

		try {
			// Getting Array of Contacts
			monday = json.getJSONArray(TAG_MONDAY);
			tuesday = json.getJSONArray(TAG_TUESDAY);
			wednesday = json.getJSONArray(TAG_WEDNESDAY);
			thursday = json.getJSONArray(TAG_THURSDAY);
			friday = json.getJSONArray(TAG_FRIDAY);
			saturday = json.getJSONArray(TAG_SATURDAY);
			sunday = json.getJSONArray(TAG_SUNDAY);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		addShowInfo(monday, "Monday");
		addShowInfo(tuesday, "Tuesday");
		addShowInfo(wednesday, "Wednesday");
		addShowInfo(thursday, "Thursday");
		addShowInfo(friday, "Friday");
		addShowInfo(saturday, "Saturday");
		addShowInfo(sunday, "Sunday");

		return Schedule;
	}

	@Override
	protected void onPostExecute(ArrayList<Show> schedule) {
		parsed = true;
	}

	public void addShowInfo(JSONArray shows, String day) {

		// looping through All Contacts
		for (int i = 0; i < shows.length(); i++) {
			JSONObject c;
			try {
				c = shows.getJSONObject(i);

				// Storing each json item in variable
				String name = c.getString(TAG_NAME);
				String start = c.getString(TAG_START);
				String end = c.getString(TAG_END);
				Show newShow = new Show(name, start, end, day);
				
				Log.i("day", name);
				
				Schedule.add(newShow);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

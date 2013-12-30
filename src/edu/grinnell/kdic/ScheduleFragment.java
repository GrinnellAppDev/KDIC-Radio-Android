package edu.grinnell.kdic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import edu.grinnell.schedule.Show;

public class ScheduleFragment extends ListFragment {
	ScheduleListAdapter mAdapter;

	// These variables will be used to sort the list by the current date
	Calendar cal;
	// day stored as int, in accordance wirh android Calendar class
	protected int today;
	protected int current_time;
	double current_time_val;

	private static final String SCHEDULE_KEY = "schedule_key";
	public static ArrayList<Show> mSchedule;

	public static ScheduleFragment newInstance(ArrayList<Show> mSchedule) {
		ScheduleFragment fragment = new ScheduleFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(SCHEDULE_KEY, mSchedule);
		fragment.setArguments(bundle);

		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new ScheduleListAdapter((MainActivity) getActivity(),
				R.layout.show_row, mSchedule);
		
		setListAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mSchedule = (ArrayList<Show>) getArguments().getSerializable(
				SCHEDULE_KEY);
		
		mSchedule = sortShows(mSchedule);
		
		return inflater.inflate(R.layout.fragment_schedule, container, false);
	}

	/*
	 * Sort the shows such that they are ordered by how soon they will air
	 */
	public ArrayList<Show> sortShows(ArrayList<Show> shows) {
		// set the calendar variables to the current time
		cal = Calendar.getInstance();
		today = cal.get(Calendar.DAY_OF_WEEK);
		current_time = cal.get(Calendar.HOUR_OF_DAY);

		/*
		 * For purposes of comparision, the time will be converted to a simple
		 * numerical format. The built-in Date and Calendar class formats are
		 * not used because these are weekly shows, and have no static date/year
		 * values.
		 */
		current_time_val = today + (.01 * current_time);

		Collections.sort(shows, new Comparator<Show>() {
			public int compare(Show show1, Show show2) {

				double show1_time_val = show1.getDay()
						+ (.01 * show1.getStartTime());

				double show2_time_val = show2.getDay()
						+ (.01 * show2.getStartTime());

				return timeCompare(show1_time_val) > timeCompare(show2_time_val) ? -1
						: 1;
			}
		});

		return shows;
	}

	/*
	 * Will return 7 - how many days until the show airs, with a fraction for
	 * the time of day
	 */
	public double timeCompare(double show_time_val) {
		if (current_time_val <= show_time_val)
			return 7 - show_time_val;
		else
			return show_time_val - 7;
	}
}
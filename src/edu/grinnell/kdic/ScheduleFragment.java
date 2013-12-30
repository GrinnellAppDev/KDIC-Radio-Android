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

	private static final String SCHEDULE_KEY = "schedule_key";
	public static ArrayList<Show> mSchedule;

	// ArrayList<ScheduleDay> schedule = new ArrayList<ScheduleDay>();

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
		
		return inflater.inflate(R.layout.fragment_schedule, container, false);
	}
/*	
	public ArrayList<Show> sortShows(ArrayList<Show> shows){
		Calendar c = Calendar.getInstance();
		//day stored as int, starting with SUN = 1, end with SAT = 7
		int day = c.get(Calendar.DAY_OF_WEEK);
		int time = c.get(Calendar.HOUR_OF_DAY);
		
		Collections.sort(shows, new Comparator<Show>() {
			public int compare(Show show1, Show show2){
				
			}
		});
		
		return shows;
	}
*/
}
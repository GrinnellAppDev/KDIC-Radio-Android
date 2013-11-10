package edu.grinnell.kdic;

import java.util.ArrayList;

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
		// String[] values = new String[] { "show1" , "Show2" , "Show3" ,
		// "Show4" , "Show5" , "Show6" , "Show7"};
		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1, values);
		// setListAdapter(adapter);

		/*
		 * Show show1 = new Show("tha joice boxx", "11", "12", "monday"); Show
		 * show2 = new Show("bubble butts", "8", "9", "tuesday"); Show show3 =
		 * new Show("youve probably never heard of it", "7", "6", "wednesday");
		 * 
		 * ArrayList<Show> mData = new ArrayList<Show>(); mData.add(show1);
		 * mData.add(show2); mData.add(show3); ScheduleDay monday = new
		 * ScheduleDay("Monday", mData);
		 * 
		 * ArrayList<ScheduleDay> schedule = new ArrayList<ScheduleDay>();
		 * schedule.add(monday);
		 */

		// MainActivity activity = (MainActivity) getActivity();
		// schedule = activity.mSchedule;

		// ArrayList<Show> schedule = getActivity().mSchedule;

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
}
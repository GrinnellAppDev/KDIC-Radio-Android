package edu.grinnell.kdic;

import java.util.ArrayList;
import java.util.Calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.grinnell.kdic.schedule.Show;

public class ScheduleListAdapter extends ArrayAdapter<Show> {
	MainActivity mActivity;
	private ArrayList<Show> mData;

	public ScheduleListAdapter(MainActivity a, int layoutId,
			ArrayList<Show> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
	}

	private class ViewHolder {
		TextView name;
		TextView time;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater li = mActivity.getLayoutInflater();
			convertView = li.inflate(R.layout.show_row, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.titleText);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Show a = mData.get(position);
		holder.name.setText(a.getTitle());

		int showday = a.getDay();

		/*
		 * In the JSON file a show that is from 12am-1am on Tuesday is stored as
		 * 24-25 on Monday, so this must be corrected when presenting the
		 * schedule to the user
		 */
		if (a.getStartTime() >= 24)
			showday++;
		if (showday == 8)
			showday = 1;

		String day;
		switch (showday) {
		case Calendar.MONDAY:
			day = "Monday";
			break;
		case Calendar.TUESDAY:
			day = "Tuesday";
			break;
		case Calendar.WEDNESDAY:
			day = "Wednesday";
			break;
		case Calendar.THURSDAY:
			day = "Thursday";
			break;
		case Calendar.FRIDAY:
			day = "Friday";
			break;
		case Calendar.SATURDAY:
			day = "Saturday";
			break;
		case Calendar.SUNDAY:
			day = "Sunday";
			break;
		default:
			day = "Partyday";
		}

		holder.time.setText(day + "  " + convertTime(a.getStartTime()) + " - "
				+ convertTime(a.getEndTime()) + "  CST");

		return convertView;
	}

	// convert 24 hour time to a 12 hour value
	public String convertTime(int hour) {
		if (hour > 12) {
			if (hour == 24)
				return "12am";
			if (hour > 24)
				return (hour - 24) + "am";
			else
				return (hour - 12) + "pm";
		} else
			return hour + "am";
	}

}
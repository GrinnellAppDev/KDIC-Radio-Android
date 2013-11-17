package edu.grinnell.kdic;

import java.util.ArrayList;
import java.util.ListIterator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.grinnell.schedule.Show;

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

		holder.time.setText(a.getDay() + "  " + a.getStartTime() + " - "
				+ a.getEndTime());

		return convertView;
	}

	protected ArrayList<Show> getDay(String day) {
		ArrayList<Show> dayshows = new ArrayList<Show>();
		ListIterator<Show> showitr = mData.listIterator();
		while (showitr.hasNext()) {
			Show a_show = showitr.next();
			if (a_show.getDay() == day)
				dayshows.add(a_show);
		}
		return dayshows;
	}

}
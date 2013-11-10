package edu.grinnell.kdic;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.grinnell.schedule.Show;

public class ScheduleListAdapter extends ArrayAdapter<Show> {
		MainActivity mActivity;
        private ArrayList<Show> mData;

        public ScheduleListAdapter(MainActivity a, int layoutId, ArrayList<Show> data) {
                super(a, layoutId, data);
                mActivity = a;
                mData = data;
        }

        private class ViewHolder {
                TextView day;
                TextView show;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder;

                if (convertView == null) {
                        LayoutInflater li = mActivity.getLayoutInflater();
                        convertView = li.inflate(R.layout.show_row, parent, false);
                        holder = new ViewHolder();
                        holder.day = (TextView) convertView.findViewById(R.id.titleText);
                        holder.show = (TextView) convertView
                                        .findViewById(R.id.djText);
                        convertView.setTag(holder);
                } else {
                        holder = (ViewHolder) convertView.getTag();
                }
                
                
                final Show a = mData.get(position);
                holder.day.setText(a.getDay());
                holder.show.setText(a.getTitle());

                return convertView;
        }
}
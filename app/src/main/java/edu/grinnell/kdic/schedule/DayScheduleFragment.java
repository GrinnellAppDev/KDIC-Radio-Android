package edu.grinnell.kdic.schedule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.R;
import edu.grinnell.kdic.Show;

public class DayScheduleFragment extends Fragment {

    private ScheduleRecyclerViewAdapter mAdapter;
    private ArrayList<ScheduleRecyclerItem> mContent;
    private String mDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the fragment's view
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // initialize the RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_schedule);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ScheduleRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        mDay = getArguments().getString(Constants.DAY);

        getContent();
        mAdapter.addContent(mContent);

        return view;
    }

    private void getContent() {
        mContent = new ArrayList<>();
        Schedule schedule = new Schedule(getActivity());

        // add day header
        mContent.add(new ScheduleRecyclerItem(ScheduleRecyclerViewAdapter.SECTION_HEADER, mDay, "All Shows for the Day"));

        // get today's shows

        ArrayList<Show> showsToday = schedule.getShow(mDay);
        for (int i = 0; i < showsToday.size(); i++) {
            Show show = showsToday.get(i);
            mContent.add(show);
        }

        schedule.close();
    }
}

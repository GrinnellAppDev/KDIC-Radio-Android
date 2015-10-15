package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static edu.grinnell.kdic.ScheduleRecyclerViewAdapter.CARD;
import static edu.grinnell.kdic.ScheduleRecyclerViewAdapter.SECTION_HEADER;

public class ScheduleFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ScheduleRecyclerViewAdapter mAdapter;
    private ArrayList<ScheduleRecyclerItem> mContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the fragment's view
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // initialize the RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_schedule);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ScheduleRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getContent();
        mAdapter.addContent(mContent);
    }

    private void getContent() {
        mContent = new ArrayList<>();
        Schedule schedule = new Schedule(getActivity());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:m a");
        // E - day name in week
        // a - am/pm marker
        // h - hour in am/pm (1-12)
        // m - minute in hour

        // get today's date
        Date today = new Date();
        String todayDayOfWeek = dayFormat.format(today);

        // add on air header
        mContent.add(new ScheduleRecyclerItem(SECTION_HEADER, "On Air", "Listen Now"));

        String onAir = schedule.getShowName(todayDayOfWeek, new SimpleDateFormat("h:00 a").format(today));

        if (onAir != null)
            mContent.add(new ScheduleRecyclerItem(CARD, onAir, "Listen now!"));
        else
            mContent.add(new ScheduleRecyclerItem(CARD, "Auto-Play", "There's no show playing."));

        // add later today header
        mContent.add(new ScheduleRecyclerItem(SECTION_HEADER, "Later Today", "Shows On Air Tonight"));

        // get today's shows

        HashMap<String, String> showsToday = schedule.getShowName(todayDayOfWeek);

        try {
            Date todayTime = timeFormat.parse(timeFormat.format(today));
            for (String s : showsToday.keySet()) {
                Date d = timeFormat.parse(s);
                if (d.after(todayTime)) {
                    mContent.add(new ScheduleRecyclerItem(CARD, showsToday.get(s), "Today at " + s));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "There was an error parsing the date.", Toast.LENGTH_SHORT).show();
        }

        schedule.close();
    }

}

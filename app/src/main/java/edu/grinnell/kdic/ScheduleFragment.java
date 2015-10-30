package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static edu.grinnell.kdic.ScheduleRecyclerViewAdapter.CARD;
import static edu.grinnell.kdic.ScheduleRecyclerViewAdapter.DAY_SCHEDULE;
import static edu.grinnell.kdic.ScheduleRecyclerViewAdapter.SECTION_HEADER;

public class ScheduleFragment extends Fragment {

    private ScheduleRecyclerViewAdapter mAdapter;
    private ArrayList<ScheduleRecyclerItem> mContent;

    private Show showOnAir;

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


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();

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

        showOnAir = schedule.getShow(todayDayOfWeek, new SimpleDateFormat("h:00 a").format(today));

        if (showOnAir != null)
            mContent.add(showOnAir);
        else
            mContent.add(new ScheduleRecyclerItem(CARD, "Auto-Play", "There's no show playing."));

        // add later today header
        mContent.add(new ScheduleRecyclerItem(SECTION_HEADER, "Later Today", "Shows On Air Tonight"));

        // get today's shows

        ArrayList<Show> showsToday = schedule.getShow(todayDayOfWeek);

        try {
            Date todayTime = timeFormat.parse(timeFormat.format(today));
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 2);
            Date lastShowEndTime = timeFormat.parse(timeFormat.format(cal.getTime()));
            for (int i = 0; i < showsToday.size(); i++) {
                Show show = showsToday.get(i);
                Date d = timeFormat.parse(show.getTime());
                if (d.after(todayTime) || d.before(lastShowEndTime)) {
                    mContent.add(show);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "There was an error parsing the date.", Toast.LENGTH_SHORT).show();
        }

        // add days header
        mContent.add(new ScheduleRecyclerItem(SECTION_HEADER, "Full Schedule", "All Shows for Days of the Week"));

        for (int i = 0; i < 7; i++) {
            mContent.add(new ScheduleRecyclerItem(DAY_SCHEDULE, Constants.DAYS_OF_WEEK[i], ""));
        }

        schedule.close();
    }
}

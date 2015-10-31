package edu.grinnell.kdic.schedule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.grinnell.kdic.R;

public class DayScheduleFragment extends Fragment {

    private ScheduleRecyclerViewAdapter mAdapter;
    private ArrayList<ScheduleRecyclerItem> mContent;

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
}

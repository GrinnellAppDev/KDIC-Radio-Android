package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Set;

import edu.grinnell.kdic.schedule.Schedule;
import edu.grinnell.kdic.schedule.ScheduleRecyclerItem;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;

public class FavoritesFragment extends Fragment {

    public static final String TAG = FavoritesFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private ScheduleRecyclerViewAdapter adapter;
    private ArrayList<ScheduleRecyclerItem> mContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_schedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ScheduleRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {

        getFavoritesList();


        super.onResume();
    }

    private void getFavoritesList() {
        Schedule schedule = new Schedule(getContext());
        Favorites favorites = new Favorites(getContext());
        Set<String> showNames = favorites.getFavorites();
        mContent = new ArrayList<>(showNames.size());

        if (!showNames.isEmpty())
            mContent.add(new ScheduleRecyclerItem(ScheduleRecyclerViewAdapter.SECTION_HEADER,
                    "Favorite Shows",
                    "All your jams in one place"));
        else
            mContent.add(new ScheduleRecyclerItem(ScheduleRecyclerViewAdapter.SECTION_HEADER,
                    "Nothing Here...",
                    "Your favorite shows will be right here."));

        for (String s : showNames) {
            Show show = schedule.getShow(s);
            mContent.add(new ScheduleRecyclerItem(ScheduleRecyclerViewAdapter.CARD,
                    show.getTitle(),
                    show.getDay() + "s at " + show.getTime()));
        }

        adapter.addContent(mContent);

        schedule.close();
    }
}

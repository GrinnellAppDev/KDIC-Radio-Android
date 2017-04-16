package edu.grinnell.kdic;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.grinnell.kdic.schedule.Schedule;
import edu.grinnell.kdic.schedule.ScheduleRecyclerItem;
import edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter;
import java.util.ArrayList;
import java.util.Set;

import static edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter.CARD;
import static edu.grinnell.kdic.schedule.ScheduleRecyclerViewAdapter.SECTION_HEADER;

/**
 * A {@link Fragment} to display favorited radio stations
 */
public class FavoritesFragment extends Fragment {

  public static final String TAG = "ScheduleFragment";

  private RecyclerView mRecyclerView;
  private ScheduleRecyclerViewAdapter mAdapter;
  private ArrayList<ScheduleRecyclerItem> mContent;
  private Resources mResources;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_schedule, null);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_schedule);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mAdapter = new ScheduleRecyclerViewAdapter(getActivity());
    mRecyclerView.setAdapter(mAdapter);

    return view;
  }

  @Override public void onResume() {
    getFavoritesList();
    super.onResume();
  }

  private void getFavoritesList() {
    Schedule schedule = new Schedule(getContext());
    Favorites favorites = new Favorites(getContext());
    Set<String> showNames = favorites.getFavorites();

    mContent = new ArrayList<>(showNames.size());
    mResources = getContext().getResources();

    if (!showNames.isEmpty()) {
      mContent.add(
          new ScheduleRecyclerItem(SECTION_HEADER, mResources.getString(R.string.fav_shows),
              mResources.getString(R.string.fav_shows_message)));
    } else {
      mContent.add(
          new ScheduleRecyclerItem(SECTION_HEADER, mResources.getString(R.string.no_fav_shows),
              mResources.getString(R.string.no_fav_shows_message)));
    }

    for (String s : showNames) {
      Show show = schedule.getShow(s);
      mContent.add(new ScheduleRecyclerItem(CARD, show.getTitle(),
          show.getDay() + mResources.getString(R.string.social_time) + show.getTime()));
    }

    mAdapter.addContent(mContent);
    schedule.close();
  }

  @Override public void onDestroyView() {
    mRecyclerView = null;
    super.onDestroyView();
  }

  @Override public void onDestroy() {
    mAdapter = null;
    mContent = null;
    super.onDestroy();
  }
}

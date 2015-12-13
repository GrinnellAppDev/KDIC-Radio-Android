package edu.grinnell.kdic.visualizer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.grinnell.kdic.Favorites;
import edu.grinnell.kdic.R;
import edu.grinnell.kdic.Show;
import edu.grinnell.kdic.schedule.Schedule;

/**
 * A placeholder fragment containing a simple view.
 */
public class VisualizeFragment extends Fragment {

    public static final String TAG = "VisualizeFragment";

    private TextView showName;
    private TextView showTime;
    private FloatingActionButton fabFavorite;
    private Show currentShow;
    private Favorites favorites;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        showName = (TextView) view.findViewById(R.id.tv_show_name);
        showTime = (TextView) view.findViewById(R.id.tv_show_time);
        fabFavorite = (FloatingActionButton) view.findViewById(R.id.fab_favorite);

        favorites = new Favorites(getContext());

        return view;
    }

    @Override
    public void onResume() {
        currentShow = Schedule.getCurrentShow(getActivity());

        try {
            if (currentShow == null) {
                showName.setText("No show\nScheduled");
                showTime.setText("The station is on Auto Play.");
                fabFavorite.setVisibility(View.GONE);
            } else {
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
                // E - day name in week
                // a - am/pm marker
                // h - hour in am/pm (1-12)
                // m - minute in hour

                // get today's date
                Date today = new Date();
                Date time = timeFormat.parse(currentShow.getTime());
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                calendar.add(Calendar.HOUR, 1);
                String endTime = timeFormat.format(calendar.getTime());

                String title = currentShow.getTitle();
                String[] words = currentShow.getTitle().trim().split(" ");
                if (words.length > 1) {
                    title = "";
                    for (int i = 0; i < words.length; i++) {
                        title += words[i] + " ";
                        if (i == words.length / 2 - 1) title += "\n";
                    }

                }
                showName.setText(title);
                showTime.setText(dayFormat.format(today) + "s " + currentShow.getTime() +
                        " to " + endTime);
                fabFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (favorites.isFavorite(currentShow.getTitle())) {
                            favorites.removeFavorite(currentShow.getTitle());
                            fabFavorite.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                        } else {
                            favorites.addFavorites(currentShow.getTitle());
                            fabFavorite.setImageResource(R.drawable.ic_favorite_white_24dp);
                        }
                    }
                });
                fabFavorite.setImageResource(favorites.isFavorite(currentShow.getTitle()) ?
                        R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
                fabFavorite.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        super.onResume();
    }
}

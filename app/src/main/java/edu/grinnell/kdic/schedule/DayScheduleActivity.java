package edu.grinnell.kdic.schedule;


import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.R;
import edu.grinnell.kdic.Show;

public class DayScheduleActivity extends AppCompatActivity {

    private ScheduleRecyclerViewAdapter mAdapter;
    private ArrayList<ScheduleRecyclerItem> mContent;
    private String mDay;



    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_day_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("Daily Schedule");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        // initialize the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_schedule);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 3;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ScheduleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        mDay = getIntent().getStringExtra(Constants.DAY);

        getContent();
        mAdapter.addContent(mContent);

    }


    private void getContent() {
        mContent = new ArrayList<>();
        Schedule schedule = new Schedule(this);

        // add day header
        mContent.add(new ScheduleRecyclerItem(ScheduleRecyclerViewAdapter.SECTION_HEADER, mDay, "All Shows for the Day"));

        // get today's shows

        ArrayList<Show> showsToday = schedule.getShowByDay(mDay);
        for (int i = 0; i < showsToday.size(); i++) {
            Show show = showsToday.get(i);
            mContent.add(show);
        }

        schedule.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
                finish();

            return true;
        }
        return false;
    }
}

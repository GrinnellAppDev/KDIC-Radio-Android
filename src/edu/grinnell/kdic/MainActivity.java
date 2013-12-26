package edu.grinnell.kdic;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import edu.grinnell.schedule.ParseSchedule;
import edu.grinnell.schedule.Show;

public class MainActivity extends FragmentActivity {

	public ArrayList<Show> mSchedule = new ArrayList<Show>();
	// InputStream scheduleJSON;
	ParseSchedule parser = new ParseSchedule();
	Boolean scheduleShowing = false;
	View schedule;
	public static String[] url = new String[] { "http://tcdb.grinnell.edu/apps/glicious/KDIC/schedule.json" };
	public boolean scheduleInitialized = false;
	public int diskImage = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO)
			getActionBar().hide();

		ConnectivityManager cm = (ConnectivityManager)
				this.getSystemService(Context.CONNECTIVITY_SERVICE);
		//check connections before downloading..

		if (!networkEnabled(cm)) {
			Toast.makeText(this, "No Network Connection",
					Toast.LENGTH_LONG).show();
		}
		else {
		/* Parse the shows from the KDIC website */
		parser.execute(url);
		mSchedule = parser.Schedule;
		}

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.radio_banner_container,
						new StreamBannerFragment()).commit();

	}

	@Override
	public void onBackPressed() {
		/* If the schedule is showing, hide it */
		if (scheduleShowing) {
			// schedule.isEnabled();
			schedule.setVisibility(View.INVISIBLE);
			findViewById(R.id.schedule).setBackgroundResource(
					R.drawable.list_black);
			scheduleShowing = false;
		} else
			/* If not, hide the whole app */
			super.onBackPressed();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	}

	/* The show schedule is a fragment that is displayed over the main interface */
	public void showSchedule(View view) {
		// Initialize the schedule the first time
		if (!scheduleInitialized) {
			schedule = findViewById(R.id.schedule_container);
			schedule.setVisibility(View.INVISIBLE);
			ListFragment schedule_frag = ScheduleFragment
					.newInstance(mSchedule);

			getFragmentManager().beginTransaction()
					.replace(R.id.schedule_container, schedule_frag)
					.addToBackStack("schedule").commit();

			scheduleInitialized = true;
			scheduleShowing = true;
			schedule.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.list_white);
			// Just change the visibility of the schedule on future taps
		} else if (scheduleShowing == false) {
			scheduleShowing = true;
			schedule.setVisibility(View.VISIBLE);
			view.setBackgroundResource(R.drawable.list_white);
		} else {
			scheduleShowing = false;
			schedule.setVisibility(View.GONE);
			view.setBackgroundResource(R.drawable.list_black);
		}
	}

	public void swapDisk(View view) {
		// switch the disk image
		final ImageView diskView = (ImageView) findViewById(R.id.diskImage);

		// easter egg shhhh
		diskView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				if (diskImage == 0)
					diskView.setImageResource(R.drawable.medium_kington);
				return true;
			}
		});

		// rotate through disk images
		if (diskImage == 0) {
			// swap to disk 1
			diskView.setImageResource(R.drawable.medium_kdicdisk);
			diskImage = 1;
		} else if (diskImage == 1) {
			// swap to disk 2
			diskView.setImageResource(R.drawable.medium_kdictext);
			diskImage = 2;
		} else if (diskImage == 2) {
			diskView.setImageResource(R.drawable.medium_tribe);
			diskImage = 3;
		} else if (diskImage == 3) {
			diskView.setImageResource(R.drawable.medium_chronic);
			diskImage = 0;
		}

	}

	/*
	 * Return true if the device has a network adapter that is capable of
	 * accessing the network.
	 */
	protected static boolean networkEnabled(ConnectivityManager connec) {
		// ARE WE CONNECTED TO THE NET

		if (connec == null) {
			return false;
		}

		if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
			// MESSAGE TO SCREEN FOR TESTING (IF REQ)
			// Toast.makeText(this, connectionType + ” connected”,
			// Toast.LENGTH_SHORT).show();
			return true;
		} else if (connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				|| connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
			return false;
		}

		return false;
	}
}

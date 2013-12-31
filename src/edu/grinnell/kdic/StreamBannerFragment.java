package edu.grinnell.kdic;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import edu.grinnell.kdic.RadioStreamService.StreamBinder;

public class StreamBannerFragment extends Fragment implements OnClickListener {

	private ImageView diskImage; // playPause button
	boolean mBound = false;

	RadioStreamService mService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		// Bind to RadioStreamService
		Intent intent = new Intent(getActivity(), RadioStreamService.class)
				.setAction(RadioStreamService.ACTION_PLAY);
		getActivity().getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_stream_banner, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle ofJoy) {

		// Initializing widget variables.
		diskImage = (ImageView) view.findViewById(R.id.diskImage);
		diskImage.setOnClickListener(this);

	}

	// If the stream is not paused, pause. Else, start.
	public void playPause(View V) {
		//Show a toast if the stream is still loading
		if (!mService.isLoaded()) {
			Toast.makeText(getActivity(), "Loading Stream ...",
					Toast.LENGTH_LONG).show();
		//pause the stream and stop the animation if it is playing
		} else if ((mService.isPlaying())) {
			mService.pauseStream();
			diskImage.clearAnimation();
		//otherwise playe the stream
		} else {
			mService.playStream();
			diskImage.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.spin));
		}
	}

	public void stopPlaying(View v) {
		mService.stopStream();
		diskImage.clearAnimation();
	}

	// toggle play/pause when disk is tapped
	@Override
	public void onClick(View arg0) {
		if (arg0 == diskImage)
			playPause(diskImage);

	}

	// Make sure the stream stops when the fragment is destroyed
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBound) {
			getActivity().getApplicationContext().unbindService(mConnection);
			mBound = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {

			StreamBinder binder = (StreamBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}

	};
}

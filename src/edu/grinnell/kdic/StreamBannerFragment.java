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

	boolean isLoading = false; // true if stream is loading but not playing
	private Boolean mLoaded = false;
	boolean mBound = false;

	RadioStreamService mService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		// Bind to RadioStreamService
		Intent intent = new Intent(getActivity(), RadioStreamService.class);
		boolean troubleshot = getActivity().getApplicationContext().bindService(intent, mConnection,
				Context.BIND_AUTO_CREATE);

		if (troubleshot = true)
			Log.e("fhrbgw", "bound");
		else
			Log.e("fhrbgw", "not bound!");
		
		if (mService == null){
			Log.e("fhrbgw", "but not really");

		}
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

		// onPrepared listener. Starts stream and changes diskImage image when
		// the stream has finished setting up.
		/*
		 * mService.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
		 * 
		 * @Override public void onPrepared(MediaPlayer mp) {
		 * kdicStream.start(); mLoaded = true; isLoading = false; //
		 * playButton.setBackgroundResource(R.drawable.button_blue_play);
		 * diskImage.startAnimation(AnimationUtils.loadAnimation( getActivity(),
		 * R.anim.spin)); } });
		 */

		diskImage.startAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.spin));

		if (mBound = true && mService != null) {
			mService.prepareStream();
			mService.startStream();
		}

		/*
		 * if (!(mService.isPlaying())) { mService.startStream(); }
		 */

	}

	// If the stream is not paused, pause. Else, start.
	public void playPause(View V) {
		if (isLoading) {
			Toast.makeText(getActivity(), "Loading Stream ...",
					Toast.LENGTH_LONG).show();
		} else if (!mLoaded) {
			mService.startStream();
		} else if ((mService.isPlaying())) {
			mService.pauseStream();
			diskImage.clearAnimation();
		} else {
			mService.startStream();
			diskImage.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.spin));
		}
	}

	// Stops stream, changes playPause to 'stopped' state.
	public void stopPlaying(View v) {
		if (mLoaded = true && isLoading == false) {
			mLoaded = false;

			mService.stopStream();
			diskImage.clearAnimation();
		}
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
		mService.releaseStream();
		if (mBound) {
			getActivity().unbindService(mConnection);
			mBound = false;
		}
	}

	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {

			Log.e("service conn", "HERE!!");
			
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

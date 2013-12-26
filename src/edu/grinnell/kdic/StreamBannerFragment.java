package edu.grinnell.kdic;

import java.io.IOException;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StreamBannerFragment extends Fragment implements OnClickListener {

	private String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128";

	private MediaPlayer kdicStream = new MediaPlayer(); // KDIC stream
	private WifiLock wifiLock; //keep the wifi from turning off

	private ImageView diskImage; // playPause button

	boolean isLoading = false; // true if stream is loading but not playing
	private Boolean mLoaded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Starts Stream
		setupPlayer();

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
		kdicStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				kdicStream.start();
				mLoaded = true;
				isLoading = false;
				// playButton.setBackgroundResource(R.drawable.button_blue_play);
				diskImage.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.spin));
			}
		});

		if (!(kdicStream.isPlaying())) {
			startPlaying();
		}
	}

	// If the stream is not stopped, stop. Else, start.
	public void playPause(View V) {
		if (isLoading) {
			// do nothing
			// give toast message

		} else if (!mLoaded) {
			startPlaying();
		} else if ((kdicStream.isPlaying())) {
			kdicStream.pause();
			diskImage.clearAnimation();
		} else {
			kdicStream.start();
			diskImage.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.spin));
		}
	}

	// Changes playPause to 'loading' state, prepares stream, starts stream
	public void startPlaying() {
		if (!kdicStream.isPlaying()) {
			isLoading = true;

			try {
				kdicStream.prepareAsync();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	// Stops stream, changes playPause to 'stopped' state.
	public void stopPlaying(View v) {
		if (mLoaded = true && isLoading == false) {
			mLoaded = false;

			kdicStream.stop();
			diskImage.clearAnimation();
		}
	}

	// Sets stream's type and URL
	public void setupPlayer() {
		kdicStream.setAudioStreamType(AudioManager.STREAM_MUSIC);
		kdicStream.setWakeMode(getActivity().getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		wifiLock = ((WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE))
			    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
		wifiLock.acquire();

		try {
			kdicStream.setDataSource(STREAMURL);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//toggle play/pause when disk is tapped
	@Override
	public void onClick(View arg0) {
		if (arg0 == diskImage)
			playPause(diskImage);

	}

	// Make sure the stream stops when the fragment is destroyed
	@Override
	public void onDestroy() {
		super.onDestroy();
		kdicStream.stop();
		kdicStream.release();
		wifiLock.release();
		kdicStream = null;
	}
}

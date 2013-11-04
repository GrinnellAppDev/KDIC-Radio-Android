package edu.grinnell.kdic;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	private String IMAGEURL = "http://kdic.grinnell.edu/wp-content/uploads/EDM-150x150.gif";

	private MediaPlayer kdicStream = new MediaPlayer(); // KDIC stream
	private ImageView diskImage; // playPause button
	private Button playButton;
	private Button pauseButton;
	private Button stopButton;

	private ImageView metadataImage; // Metadata image. Duhh.
	private TextView metadataText; // Double duhh.
	private final ImageDownloader mDownload = new ImageDownloader();

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

		// Find buttons
		playButton = (Button) view.findViewById(R.id.playButton);
		pauseButton = (Button) view.findViewById(R.id.pauseButton);
		stopButton = (Button) view.findViewById(R.id.stopButton);

		// Initialize Buttons
		playButton.setOnClickListener(this);
		pauseButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);

		playButton.setBackgroundResource(R.drawable.button_black_play);
		pauseButton.setBackgroundResource(R.drawable.button_black_pause);
		stopButton.setBackgroundResource(R.drawable.button_black_stop);

		// onPrepared listener. Starts stream and changes diskImage image when
		// the stream has finished setting up.
		kdicStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				kdicStream.start();
				mLoaded = true;
				isLoading = false;
				playButton.setBackgroundResource(R.drawable.button_blue_play);
				diskImage.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), R.anim.spin));
			}
		});

		// Set metadata image to hardcoded URL
		// mDownload.download(IMAGEURL, metadataImage);

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
			playButton.setBackgroundResource(R.drawable.button_black_play);
			pauseButton.setBackgroundResource(R.drawable.button_blue_pause);
		} else {
			kdicStream.start();
			diskImage.startAnimation(AnimationUtils.loadAnimation(
					getActivity(), R.anim.spin));
			stopButton.setBackgroundResource(R.drawable.button_black_stop);
			playButton.setBackgroundResource(R.drawable.button_blue_play);
			pauseButton.setBackgroundResource(R.drawable.button_black_pause);
		}
	}

	// Changes playPause to 'loading' state, prepares stream, starts stream
	public void startPlaying() {
		if (!kdicStream.isPlaying()) {
			isLoading = true;
			playButton.setBackgroundResource(R.drawable.button_blue_play);
			stopButton.setBackgroundResource(R.drawable.button_black_stop);

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
			stopButton.setBackgroundResource(R.drawable.button_blue_stop);
			playButton.setBackgroundResource(R.drawable.button_black_play);
			pauseButton.setBackgroundResource(R.drawable.button_black_pause);
			diskImage.clearAnimation();
		}
	}

	// Sets stream's type and URL
	public void setupPlayer() {
		kdicStream.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

	@Override
	public void onClick(View arg0) {
		if (arg0 == playButton)
			playPause(playButton);
		else if (arg0 == pauseButton)
			playPause(pauseButton);
		else if (arg0 == stopButton)
			stopPlaying(stopButton);
		else if (arg0 == diskImage) {
			playPause(diskImage);
		}
	}

	//Make sure the stream stops when the fragment is destroyed
	@Override
	public void onDestroy() {
		super.onDestroy();
		kdicStream.stop();
	}
}

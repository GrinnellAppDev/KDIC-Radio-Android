package edu.grinnell.kdic;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class RadioStreamService extends Service implements
		MediaPlayer.OnPreparedListener {

	MediaPlayer kdicStream = null;
	private final IBinder mBinder = new StreamBinder();

	public static final String TAG = "Radio Stream Service";
	private static final String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128";
	public static final String ACTION_PLAY = "PLAY_STREAM";

	private WifiLock wifiLock; // keep the wifi from turning off
	protected boolean stream_playing = false;
	protected boolean stream_loaded = false;

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class StreamBinder extends Binder {
		RadioStreamService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return RadioStreamService.this;
		}
	}

	public void onCreate() {
		prepareStream();
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {

		Log.i("TAG", "steam prepared");

		kdicStream.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);

		/*
		 * Aquire a wifi lock to protect against unexpected stopage of the
		 * stream
		 */
		wifiLock = ((WifiManager) getApplicationContext().getSystemService(
				Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_FULL, "mylock");
		wifiLock.acquire();

		stream_loaded = true;
	}

	/* Initialize the radio stream */
	public void prepareStream() {
		Log.e(TAG, "prepare stream");
		kdicStream = new MediaPlayer();
		kdicStream.setOnPreparedListener(this);
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

		kdicStream.prepareAsync(); // prepare async to not block main thread
	}

	public void pauseStream() {
		kdicStream.pause();
	}

	public void playStream() {
		kdicStream.start();
	}

	public void stopStream() {
		kdicStream.stop();
	}

	public boolean isPlaying() {
		return kdicStream.isPlaying();
	}

	public boolean isLoaded() {
		return stream_loaded;
	}

	public void releaseStream() {
		if (kdicStream != null) {
			kdicStream.stop();
			kdicStream.release();
			kdicStream = null;
		}
		
		try {
			wifiLock.release();
			Log.i(TAG, "wifiLock released");
		}
		catch (Exception e){
			Log.e(TAG, "Problem releasing wifiLock: " + e.toString());
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// All clients have unbound with unbindService()
		releaseStream();
		return false;
	}

}

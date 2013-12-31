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

public class RadioStreamService extends Service implements
		MediaPlayer.OnPreparedListener {

	private static final String ACTION_PLAY = "PLAY_STREAM";
	MediaPlayer kdicStream = null;
	private static final String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128";
	private WifiLock wifiLock; // keep the wifi from turning off
	private final IBinder mBinder = new StreamBinder();
	protected boolean stream_playing;

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

	@Override
	public void onPrepared(MediaPlayer arg0) {
		
		wifiLock = ((WifiManager) getApplicationContext().getSystemService(
				Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_FULL, "mylock");
		wifiLock.acquire();
	}

	
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		if (intent.getAction().equals(ACTION_PLAY)) {
			prepareStream();
			kdicStream.setOnPreparedListener(this);
			kdicStream.prepareAsync(); // prepare async to not block main thread
		}
		*/

		return Service.START_NOT_STICKY;
	}
	

	public void prepareStream() {
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

	public void pauseStream() {
		kdicStream.pause();
	}

	public void playStream() {
		kdicStream.start();
	}
	
	public void stopStream(){
		kdicStream.stop();	
	}

	
	public boolean isPlaying(){
		return kdicStream.isPlaying();
	}
	
	public void startStream() {
		if (!kdicStream.isPlaying()) {
			try {
				kdicStream.prepareAsync();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	public void releaseStream() {
		if (kdicStream != null) {
			kdicStream.stop();
			kdicStream.release();
			kdicStream = null;
		}
		wifiLock.release();
	}

}

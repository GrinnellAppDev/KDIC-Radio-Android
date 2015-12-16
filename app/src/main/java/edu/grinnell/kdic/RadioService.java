package edu.grinnell.kdic;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import edu.grinnell.kdic.schedule.Schedule;

/**
 * Service used to play the radio from the stream.
 */
public class RadioService extends Service {

    public static final String TAG = RadioService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private AudioManager audioManager;
    private OnAudioFocusChangeListener audioFocusListener;
    private boolean isLoaded;
    private boolean isLoading;
    private WifiManager.WifiLock wifiLock;
    private MediaPlayer mediaPlayer;

    // Binder given to clients
    private final IBinder mBinder = new RadioBinder();

    // timer for stopping stream after pause
    private Timer timer = new Timer();
    private static final long STOP_STREAM_DELAY = 30 * 1000;
    // reset the media player 30 seconds after pause

    private Runnable runOnStreamPrepared;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RadioBinder extends Binder {
        RadioService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RadioService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Unbound");

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {

        // obtain WifiLock
        wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "myWifiLock");

        setupMediaPlayer();

        setupAudioManager();

        // time change?? show change

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case Constants.ACTION_STOP_RADIO_SERVICE:
                    stopSelf();
                    break;
                case Constants.ACTION_STREAM_PLAY_PAUSE:
                    if (isPlaying())
                        pause();
                    else play();

                default:
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // reset the media player if an error occurs
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(RadioService.this, "There was an error playing the stream. Reloading...",
                        Toast.LENGTH_SHORT).show();
                mediaPlayer.reset();
                return false;
            }
        });
    }

    private void setupAudioManager() {
        // obtain audioManager for requesting audio focus
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        Log.d(TAG, "AudioManager: AUDIOFOCUS_GAIN");
                        // resume playback
                        if (mediaPlayer == null) setupMediaPlayer();
                        else if (!mediaPlayer.isPlaying()) play();
                        mediaPlayer.setVolume(1.0f, 1.0f);
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS:
                        // Lost focus for an unbounded amount of time: stop playback and release media player
                        Log.d(TAG, "AudioManager: AUDIOFOCUS_LOSS");
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        Log.d(TAG, "AudioManager: AUDIOFOCUS_LOSS_TRANSIENT");
                        // Lost focus for a short time, but we have to stop
                        // playback. We don't release the media player because playback
                        // is likely to resume
                        if (isPlaying()) pause();
                        break;

                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        Log.d(TAG, "AudioManager: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                        // Lost focus for a short time, but it's ok to keep playing
                        // at an attenuated level
                        if (isPlaying()) mediaPlayer.setVolume(0.2f, 0.2f);
                        break;
                }
            }
        };
    }

    private void prepStreamAndPlay() {
        if (mediaPlayer != null) {
            // callback for once the stream is prepared
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "Stream Prepared");
                    isLoading = false;
                    isLoaded = true;
                    play();
                    if (runOnStreamPrepared != null) runOnStreamPrepared.run();
                }
            });


            try {
                // set the URL for the stream
                mediaPlayer.setDataSource(Constants.STREAM_URL);

                // prepare the stream asynchronously
                isLoading = true;
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // mediaplayer == null
            setupMediaPlayer();
            prepStreamAndPlay();
        }
    }

    public void setRunOnStreamPrepared(final Runnable runOnStreamPrepared) {
        this.runOnStreamPrepared = runOnStreamPrepared;
    }

    public void play() {
        if (isLoaded) { // if stream is loaded

            timer.cancel(); // cancel the stop timer if it is loaded

            // request focus to play audio
            int result = audioManager.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // could not get audio focus.
                Toast.makeText(RadioService.this, "Cannot play audio.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Audio Manager request not granted.");
            } else {
                Log.d(TAG, "AUDIO REQUEST GRANTED.");
            }
            if (!wifiLock.isHeld()) wifiLock.acquire(); // don't let the wifi radio turn off
            mediaPlayer.start(); // play
            startForegroundNotification(); // start the notification and make this service foreground
        } else {
            prepStreamAndPlay();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

            audioManager.abandonAudioFocus(audioFocusListener); // abandon the audio focus

            timer = new Timer();
            final TimerTask stopPlayerTask = new TimerTask() {
                @Override
                public void run() {
                    reset();
                    this.cancel();
                }
            };
            timer.schedule(stopPlayerTask, STOP_STREAM_DELAY);

            stopForegroundNotification(); // stop making this service a foreground service
        }
        if (wifiLock.isHeld())
            wifiLock.release(); // release wifi lock


    }

    /**
     * reset the media player so that the stream needs to be loaded again
     */
    public void reset() {
        if (wifiLock.isHeld())
            wifiLock.release(); // let the wifi radio turn off
        audioManager.abandonAudioFocus(audioFocusListener); // abandon the audio focus
        isLoaded = false;
        if (mediaPlayer != null)
            mediaPlayer.reset();
        Log.d(TAG, "Stopping stream!");

        stopForegroundNotification();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isLoading() {
        return isLoading;
    }

    private void startForegroundNotification() {


        Show currentShow = Schedule.getCurrentShow(this);
        String title = currentShow != null ? currentShow.getTitle() : "Auto Play";

        Intent playPauseIntent = new Intent(this, RadioService.class);
        playPauseIntent.setAction(Constants.ACTION_STREAM_PLAY_PAUSE);

        PendingIntent playPausePendingIntent = PendingIntent.getService(
                this,
                0,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent deleteIntent = new Intent(this, RadioService.class);
        deleteIntent.setAction(Constants.ACTION_STOP_RADIO_SERVICE);

        PendingIntent pendingDelete = PendingIntent.getService(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtra("isPlaying", isPlaying());

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(isPlaying() ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setContentTitle(title)
                .addAction(isPlaying() ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp,
                        "Play/Pause", playPausePendingIntent)
                .setContentText("KDIC - Grinnell College Radio")
                .setShowWhen(false) // hide the time
                .setDeleteIntent(pendingDelete)
                .setColor(getResources().getColor(R.color.accent))
                .setContentIntent(notifyPendingIntent);

        startForeground(NOTIFICATION_ID, builder.build());

        /*
        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.setAction(Constants.ACTION_STREAM_PLAY_PAUSE);

        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Puts the PendingIntent into the notification builder
        builder.setContentIntent(notifyPendingIntent);
        // Notifications are issued by sending them to the
        // NotificationManager system service.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds an anonymous Notification object from the builder, and
        // passes it to the NotificationManager
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

        */
    }

    private void stopForegroundNotification() {

        startForegroundNotification();
        stopForeground(false);

        /*
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
        */

    }

    @Override
    public boolean stopService(Intent name) {
        Log.d(TAG, "RadioService stopped.");

        reset();

        stopForegroundNotification();

        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = null;
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "RadioService destroyed.");

        reset();

        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = null;
    }

}

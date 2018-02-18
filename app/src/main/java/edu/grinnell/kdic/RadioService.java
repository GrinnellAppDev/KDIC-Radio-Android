package edu.grinnell.kdic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import edu.grinnell.kdic.schedule.Schedule;

import static android.media.AudioManager.*;
import static android.media.AudioManager.OnAudioFocusChangeListener;
import static android.widget.Toast.*;
import static edu.grinnell.kdic.Constants.*;

import android.net.wifi.WifiManager;

/**
 * Service used to play the radio from the stream.
 */
public class RadioService extends Service {

    private AudioManager audioManager;
    private OnAudioFocusChangeListener audioFocusListener;
    private boolean isLoaded;
    private boolean isLoading;
    private WifiManager.WifiLock wifiLock;
    private MediaPlayer mediaPlayer;

    private final IBinder mBinder = new RadioBinder();

    // timer for stopping stream after pause
    private Timer timer = new Timer();

    private Runnable runOnStreamPrepared;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class RadioBinder extends Binder {
        RadioService getService() {
            return RadioService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate() {
        wifiLock = ((WifiManager) getSystemService(WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_TAG);

        setupMediaPlayer();
        setupAudioManager();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_STOP_RADIO_SERVICE:
                    hideNotification();
                    stopSelf();
                    break;
                case ACTION_STREAM_PLAY_PAUSE:
                    if (isPlaying()) {
                        pause();
                        showNotification();
                        stopForeground(false);
                    } else {
                        play();
                        showNotification();
                    }
                    break;
                default:
                    break;
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void setupMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(STREAM_MUSIC);

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                makeText(RadioService.this, R.string.error_playing_stream,
                        LENGTH_SHORT).show();
                mediaPlayer.reset();
                return false;
            }
        });
    }

    private void setupAudioManager() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioFocusListener = new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AUDIOFOCUS_GAIN:
                        if (mediaPlayer == null) setupMediaPlayer();
                        else if (!mediaPlayer.isPlaying()) play();
                        mediaPlayer.setVolume(MEDIA_PLAYER_LEFT_VOLUME,
                                MEDIA_PLAYER_RIGHT_VOLUME);
                        break;

                    case AUDIOFOCUS_LOSS:
                        if (isPlaying()) reset();
                        break;

                    case AUDIOFOCUS_LOSS_TRANSIENT:
                        if (isPlaying()) pause();
                        break;

                    case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (isPlaying()) mediaPlayer.setVolume(MEDIA_PLAYER_LEFT_VOLUME_LOW,
                                MEDIA_PLAYER_RIGHT_VOLUME_LOW);
                        break;
                }
            }
        };
    }

    private void prepStreamAndPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isLoading = false;
                    isLoaded = true;
                    play();
                    if (runOnStreamPrepared != null) runOnStreamPrepared.run();
                }
            });


            try {
                mediaPlayer.setDataSource(STREAM_URL);

                isLoading = true;
                mediaPlayer.prepareAsync();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setupMediaPlayer();
            prepStreamAndPlay();
        }
    }

    public void setRunOnStreamPrepared(final Runnable runOnStreamPrepared) {
        this.runOnStreamPrepared = runOnStreamPrepared;
    }

    /*
        Plays the currently loaded stream. If stream is not loaded, load stream and play.
    */
    public void play() {
        if (isLoaded) {
            timer.cancel();

            int result = audioManager.requestAudioFocus(audioFocusListener, STREAM_MUSIC,
                    AUDIOFOCUS_GAIN);

            if (result != AUDIOFOCUS_REQUEST_GRANTED) {
                makeText(RadioService.this, R.string.audio_playback_error, LENGTH_SHORT).show();
            }
            if (!wifiLock.isHeld()) wifiLock.acquire();
            mediaPlayer.start();
        } else {
            prepStreamAndPlay();
        }
    }

    /*
        Pauses the currently loaded stream.
    */
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();

            audioManager.abandonAudioFocus(audioFocusListener);

            timer = new Timer();
            final TimerTask stopPlayerTask = new TimerTask() {
                @Override
                public void run() {
                    reset();
                    this.cancel();
                }
            };
            timer.schedule(stopPlayerTask, STOP_STREAM_DELAY);
        }
        if (wifiLock.isHeld())
            wifiLock.release();
    }

    /**
     * reset the media player so that the stream needs to be loaded again
     */
    public void reset() {
        if (wifiLock.isHeld())
            wifiLock.release();
        audioManager.abandonAudioFocus(audioFocusListener);
        isLoaded = false;
        if (mediaPlayer != null)
            mediaPlayer.reset();
        hideNotification();
    }

    /**
     * Returns true if the media player is playing or loading.
     */
    public boolean isPlaying() {
        return mediaPlayer != null && (mediaPlayer.isPlaying() || isLoading);
    }

    /**
     * Returns true if the media player is loaded.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Returns true if the media player is loading.
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * Displays a notification with the current show name, as well as actionable play, pause and
     * close buttons.
     */
    public void showNotification() {


        Show currentShow = Schedule.getCurrentShow(this);
        String title = currentShow != null ? currentShow.getTitle() : getString(R.string.auto_play);

        Intent playPauseIntent = new Intent(this, RadioService.class);
        playPauseIntent.setAction(ACTION_STREAM_PLAY_PAUSE);

        PendingIntent playPausePendingIntent = PendingIntent.getService(
                this,
                REQUEST_CODE,
                playPauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent deleteIntent = new Intent(this, RadioService.class);
        deleteIntent.setAction(ACTION_STOP_RADIO_SERVICE);

        PendingIntent pendingDelete = PendingIntent.getService(
                this,
                REQUEST_CODE,
                deleteIntent,
                PendingIntent.FLAG_ONE_SHOT
        );

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notifyIntent.putExtra("isPlaying", isPlaying());

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        REQUEST_CODE,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(isPlaying() ? R.drawable.ic_play_arrow_white_24dp : R.drawable.ic_pause_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setContentTitle(title)
                .addAction(isPlaying() ? R.drawable.ic_pause_white_24dp : R.drawable.ic_play_arrow_white_24dp,
                        getString(R.string.play_or_pause), playPausePendingIntent)
                .addAction(R.drawable.ic_close_white_24dp, getString(R.string.close), pendingDelete)
                .setContentText(getString(R.string.kdic_college_radio))
                .setShowWhen(false)
                .setDeleteIntent(pendingDelete)
                .setColor(getResources().getColor(R.color.accent))
                .setContentIntent(notifyPendingIntent);

        startForeground(NOTIFICATION_ID, builder.build());

    }

    /**
     * Removes the notification.
     */
    public void hideNotification() {
        stopForeground(true);
    }

    @Override
    public boolean stopService(Intent name) {
        reset();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);

        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = null;
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {

        reset();

        if (mediaPlayer != null) mediaPlayer.release();
        timer = null;
        runOnStreamPrepared = null;
        mediaPlayer = null;
        super.onDestroy();
    }
}












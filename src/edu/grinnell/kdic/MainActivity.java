package edu.grinnell.kdic;

import java.net.URI;
import java.util.List;
import net.moraleboost.streamscraper.Stream;
import net.moraleboost.streamscraper.Scraper;
import net.moraleboost.streamscraper.scraper.IceCastScraper;


import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends Activity {

	private static final String URL = "http://kdic.grinnell.edu:8001/kdic128";
    
    MediaPlayer mediaPlayer = new MediaPlayer();
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button playPause = (Button) findViewById(R.id.playButton);
        playPause.setText("Pause");
        playPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        
        setupPlayer();
        startPlaying();
        
    }

    
    public void playPause(){
    	final Button playPause = (Button) findViewById(R.id.playButton);
    	if(mediaPlayer.isPlaying()){
        	stopPlaying();
            playPause.setText("Play");
    	}else{
        	startPlaying();
            playPause.setText("Pause");
    	}
    }
    
    public boolean startPlaying(){
    	
    	try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // might take long! (for buffering, etc)
        
        mediaPlayer.start();
        
        return true;
    }
    
    public boolean stopPlaying(){
    	mediaPlayer.stop();
    	return true;
    }
    
 
    public void setupPlayer(){
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
			mediaPlayer.setDataSource(URL);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*
        if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 10)
        	setupMetadata();
        	*/
    }
    
    /*
    @TargetApi(10)
	public void setupMetadata(){
    	final MediaMetadataRetriever metaGet = new MediaMetadataRetriever();
    	final TextView t = (TextView) findViewById(R.id.status);
    	mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {                     
                if (what == MediaPlayer.MEDIA_INFO_METADATA_UPDATE) {
                    t.setText(metaGet.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                }
				return true;          
            }
        });
    }
    */
    
}

package edu.grinnell.kdic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.moraleboost.streamscraper.ScrapeException;
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
import android.widget.TextView;



public class MainActivity extends Activity {

	private static final String URL = "http://kdic.grinnell.edu:8001/kdic128";
	private static final String METAURL = "http://kdic.grinnell.edu:8001/";
    
    MediaPlayer mediaPlayer = new MediaPlayer();
    Scraper scraper = new IceCastScraper();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final TextView t = (TextView) findViewById(R.id.status);
        final Button playPause = (Button) findViewById(R.id.playButton);
        playPause.setText("Pause");
        playPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        
        t.setText("initializing");
        
        setupPlayer();
        startPlaying();
        
        /*
        List<Stream> stream = null;
		try {
			stream = scraper.scrape(new URI(METAURL));
		} catch (Exception e) {
			e.printStackTrace();
		}
        t.setText(stream.get(0).getUri().toString());
        */
        
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
        
    }
    

    
}

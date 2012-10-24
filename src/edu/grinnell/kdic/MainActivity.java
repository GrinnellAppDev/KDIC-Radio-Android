package edu.grinnell.kdic;



import java.io.IOException;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



public class MainActivity extends Activity {

	private static final String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128"; //KDIC stream URL
	private static final String METAURL = "http://kdic.grinnell.edu:8001/"; //metadata URL
	private static final String PICURL = "http://kdic.grinnell.edu/wp-content/uploads/radio-300x199.jpg"; //an arbitrary picture, for testing metadata
    
    private MediaPlayer kdicStream = new MediaPlayer();
    private ImageView curPlayingImage;
    private Button playButton;
    
    boolean isLoading = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //playPause button junk, setting text and creating listener
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setText("Pause");
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        
        //stream listener, solely for changing the button text from 'loading' to 'pause' after it finishes loading.
        kdicStream.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				isLoading = false;
	            playButton.setText("Pause");
			}
		});
        
        //Starts Stream
        setupPlayer();
        if ((isLoading == false) || !(kdicStream.isPlaying())){
        	playPause();
        }
        
        
    }
    
    public void playPause(){
    	
    	//If the stream is loading or playing, stop it. Else, start it.
    		if( (isLoading) || (kdicStream.isPlaying()) ){
    			stopPlaying();
    		} else {
    			startPlaying();
    		}
    	}
    
    
    public boolean startPlaying(){
    	
    	playButton.setText("Starting..."); // WHY WON'T THIS APPEAR FOR MORE THAN A FRACTION OF A SECOND.
    	//prepare and start stream
    	try {
    		kdicStream.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // might take long! (for buffering, etc)
        
       kdicStream.start();
        
        return true;
    }
    
    public boolean stopPlaying(){
    	//STOP
    	kdicStream.stop();
    	playButton.setText("Play");
    	return true;
    }
    
 
    public void setupPlayer(){
    	//Setup stream type and URL
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
    
}

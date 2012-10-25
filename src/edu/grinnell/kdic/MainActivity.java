package edu.grinnell.kdic;



import java.io.IOException;
import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
//import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;



public class MainActivity extends Activity {

	private static final String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128"; //KDIC stream URL
	//private static final String METAURL = "http://kdic.grinnell.edu:8001/"; //metadata URL
	//private static final String PICURL = "http://kdic.grinnell.edu/wp-content/uploads/radio-300x199.jpg"; //an arbitrary picture, for testing metadata
    
    private MediaPlayer kdicStream = new MediaPlayer(); //KDIC stream
    //private ImageView metadataImage; //Metadata image. Duhh.
    private Button playButton; //playPause button
    
    boolean isLoading = false; //true if stream is loading but not playing
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Initial playPause settings
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setText("Pause");
        playButton.setBackgroundColor(Color.RED);
        
        
        //playPause listener, stops/starts stream
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
       
        //stream prepared listener, changes button state to 'playing'
        kdicStream.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
        	public void onPrepared(MediaPlayer mp) {
        		isLoading = false;
                playButton.setText("Pause");
                playButton.setBackgroundColor(Color.RED);
        	}
		});
        
        //Starts Stream
        setupPlayer();
        if (!(kdicStream.isPlaying())){
        	startPlaying();
        }
        
    }
    
    
	//If the stream is not stopped, stop. Else, start.
    public void playPause(){
    		if( (isLoading) || (kdicStream.isPlaying()) ){
    			stopPlaying();
    		} else {
    			startPlaying();
    		}
    	}
    
    //Changes playPause to 'loading' state, prepares stream, starts stream
    public void startPlaying(){
    	isLoading = true;
    	playButton.setText("Starting..."); // WHY WON'T THIS APPEAR FOR MORE THAN A FRACTION OF A SECOND.
        playButton.setBackgroundColor(Color.YELLOW);
    	
    	try {
    		kdicStream.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // might take long! (for buffering, etc)
        
       kdicStream.start();
    }
    
    //Stops stream, changes playPause to 'stopped' state.
    public void stopPlaying(){
    	kdicStream.stop();
    	playButton.setText("Play");
        playButton.setBackgroundColor(Color.GREEN);
    }
    
    //Sets stream's type and URL
    public void setupPlayer(){
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

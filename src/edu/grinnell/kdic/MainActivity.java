package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


//import android.net.Uri;
//import android.widget.ImageView;
//import android.widget.TextView;



public class MainActivity extends FragmentActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        	
        getSupportFragmentManager().beginTransaction()
        	.replace(R.id.radio_banner_container, new StreamBannerFragment())
        	.commit();
        
    }
    
}

package edu.grinnell.kdic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


//import android.net.Uri;
//import android.widget.ImageView;
//import android.widget.TextView;



public class MainActivity extends FragmentActivity {
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();	
        
        getSupportFragmentManager().beginTransaction()
        	.replace(R.id.radio_banner_container, new StreamBannerFragment())
        	.commit();
        
    }
    
}

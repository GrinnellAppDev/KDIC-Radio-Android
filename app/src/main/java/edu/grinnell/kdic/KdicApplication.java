package edu.grinnell.kdic;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class KdicApplication extends Application implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("KDIC Application", "Application Destroyed.");
        Intent intent = new Intent(this, RadioService.class);
        stopService(intent);

    }

    @Override
    public void onCreate() {

        super.onCreate();
    }
}

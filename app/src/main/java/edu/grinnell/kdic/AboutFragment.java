package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {

    public static final String TAG = AboutFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_splash, null);
    }
}

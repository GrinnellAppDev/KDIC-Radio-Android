package edu.grinnell.kdic;


import android.content.Context;
import android.content.SharedPreferences;


public class Favorites {

    private static final String SHARED_PREF = "favorites_shared_pref";
    private SharedPreferences sharedPreferences;


    public Favorites(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF, 0);
    }

    public void addFavorites(String showName) {
        if (!sharedPreferences.getBoolean(showName, false))
            sharedPreferences.edit().putBoolean(showName, true).apply();
    }

    public void removeFavorite(String showName) {
        if (sharedPreferences.getBoolean(showName, false))
            sharedPreferences.edit().remove(showName).apply();
    }

    public boolean isFavorite(String showName) {
        return sharedPreferences.contains(showName);
    }
}

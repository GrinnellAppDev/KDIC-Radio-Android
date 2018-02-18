package edu.grinnell.kdic;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


public class Favorites {

    private static final String SHARED_PREF = "favorites_shared_pref";
    private SharedPreferences mFavorites;

    public Favorites(Context context) {
        mFavorites = context.getSharedPreferences(SHARED_PREF, 0);
    }

    public void addFavorites(String showName) {
        if (!mFavorites.getBoolean(showName, false))
            mFavorites.edit().putBoolean(showName, true).apply();
    }

    public void removeFavorite(String showName) {
        if (mFavorites.getBoolean(showName, false))
            mFavorites.edit().remove(showName).apply();
    }

    public boolean isFavorite(String showName) {
        return mFavorites.contains(showName);
    }

    public Set<String> getFavorites() {
        return mFavorites.getAll().keySet();
    }
}

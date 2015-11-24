package edu.grinnell.kdic;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;


public class Favorites {

    private static final String SHARED_PREF = "favorites_shared_pref";
    private SharedPreferences favorites;


    public Favorites(Context context) {
        favorites = context.getSharedPreferences(SHARED_PREF, 0);
    }

    public void addFavorites(String showName) {
        if (!favorites.getBoolean(showName, false))
            favorites.edit().putBoolean(showName, true).apply();
    }

    public void removeFavorite(String showName) {
        if (favorites.getBoolean(showName, false))
            favorites.edit().remove(showName).apply();
    }

    public boolean isFavorite(String showName) {
        return favorites.contains(showName);
    }

    public Set<String> getFavorites () {
        return favorites.getAll().keySet();
    }
}

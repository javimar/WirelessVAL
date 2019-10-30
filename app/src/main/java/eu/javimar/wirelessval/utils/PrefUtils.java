package eu.javimar.wirelessval.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.GeoPoint;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;

public final class PrefUtils
{
    // final class
    private PrefUtils() {}

    public static GeoPoint ayuntamiento;

    /** Populates current position from preferences */
    public static void retrieveLongAndLatFromPreferences(Context c)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        // if position is not available, locate city center
        sCurrentPosition = new GeoPoint(
                pref.getFloat(c.getString(R.string.pref_longtitude_key), -0.3788316f),
                pref.getFloat(c.getString(R.string.pref_latitude_key), 39.4697621f));
    }


    /** Store longitude and latitude in preferences */
    public static void updateLongAndLatInPreferences(Context c, GeoPoint geoPoint)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        // set coordinates in preferences
        pref.edit().
                putFloat(c.getString(R.string.pref_longtitude_key),
                        (float)geoPoint.getLongitud()).apply();
        // set workplace field in preferences
        pref.edit().
                putFloat(c.getString(R.string.pref_latitude_key),
                        (float)geoPoint.getLatitude()).apply();
    }

}

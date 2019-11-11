package eu.javimar.wirelessval.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.WifiContract;

public final class HelperUtils
{
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String LOG_TAG = HelperUtils.class.getName();

    // final class
    private HelperUtils() {}


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean isGooglePlayServicesAvailable(Activity activity)
    {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS)
        {
            if (googleApiAvailability.isUserResolvableError(status))
            {
                googleApiAvailability.getErrorDialog(activity, status,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }


    /** Returns true if the network is connected or about_layout to become available */
    public static boolean isNetworkAvailable(Context context)
    {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    public static boolean wifiInDatabase(String lat, String lng, Context context)
    {
        String[] projection = new String[]
                {
                        WifiContract.WifiEntry._ID,
                        WifiContract.WifiEntry.COLUMN_WIFI_LATITUDE,
                        WifiContract.WifiEntry.COLUMN_WIFI_LONGITUDE
                };
        String selection =  WifiContract.WifiEntry.COLUMN_WIFI_LATITUDE + "=? AND " +
                WifiContract.WifiEntry.COLUMN_WIFI_LONGITUDE + "=?";
        String [] selectionArgs = new String[] { lat, lng };

        Cursor cursor = context.getContentResolver()
                .query(WifiContract.WifiEntry
                        .CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor == null || cursor.getCount() < 1)
        {
            return false;
        }
        else
        {
            cursor.close();
            return true;
        }
    }


    public static boolean isDatabaseEmpty(Context context)
    {
        Cursor cursor = context.getContentResolver()
                .query(WifiContract.WifiEntry.CONTENT_URI, null, null, null, null);
        if (cursor == null || cursor.getCount() < 1)
        {
            return true;
        }
        else
        {
            cursor.close();
            return false;
        }
    }



    /**
     * Helper method to delete a wifi
     */
    public static void deleteWifiFromDb(Context context, Uri uri)
    {
        // Only perform the delete if this is an existing wifi.
        if (uri != null)
        {
            // Call the ContentResolver to delete wifi at the given content URI.
            // Pass in null for the selection and selection args because the mCuuri
            // content URI already identifies the wifi we want.
            int rowsDeleted = context
                    .getContentResolver()
                    .delete(uri, null, null);

            // Show a message depending on whether or not the delete was successful.
            if (rowsDeleted == 0)
            {
                // If no rows were deleted, then there was an error with the delete.
                Toasty.info(context, context.getString(R.string.fragment_detail_delete_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Otherwise, the delete was successful
                Toasty.success(context, context.getString(R.string.fragment_detail_delete_ok),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int fetchColor(Context context, @ColorRes int color)
    {
        return ContextCompat.getColor(context, color);
    }

    /**
     * We are first scanning the file for invalid xml characters and ignoring them.
     * Rest of the valid characters are getting added to a String object.
     */
    public static String stripNonValidXMLCharacters(String xmlFile)
    {
        StringBuilder out = new StringBuilder();
        char current;

        if (xmlFile == null || ("".equals(xmlFile))) return "";

        for (int i = 0; i < xmlFile.length(); i++)
        {
            current = xmlFile.charAt(i);
            if (current == 0xFFFD)
            {
                out.append('í');
            }
            else
            {
                out.append(current);
            }
        }
        return out.toString();
    }
}


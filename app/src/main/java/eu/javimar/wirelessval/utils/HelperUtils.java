package eu.javimar.wirelessval.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;

public final class HelperUtils
{
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

       // final class
    private HelperUtils() {}

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static void isGooglePlayServicesAvailable(Activity activity)
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
        }
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

    public static boolean isWifiInDatabase(Context context, String name, double lat, double lng)
    {
        WifiViewModel wifiViewModel = new ViewModelProvider((ViewModelStoreOwner) context,
                new WifiViewModelFactory((Application)context.getApplicationContext(),
                        0, 0)).get(WifiViewModel.class);
        return wifiViewModel
                .checkIfWifiInDatabase(name, lat, lng) != 0;
    }
}
/**
 * Wireless Valencia
 *
 * @author Javier Martín
 * @email: javimardeveloper@gmail.com
 * @link http://www.javimar.eu
 * @package eu.javimar.wirelessvlc
 * @version 1.1
 *
BSD 3-Clause License

Copyright (c) 2016, 2017 JaviMar
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

 * Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package eu.javimar.wirelessval;
import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.model.GeoPoint;
import eu.javimar.wirelessval.model.WifiDbHelper;
import eu.javimar.wirelessval.sync.LoaderIntentService;
import eu.javimar.wirelessval.sync.LoadingTasks;
import eu.javimar.wirelessval.view.FragmentAbout;
import eu.javimar.wirelessval.view.DetailActivity;
import eu.javimar.wirelessval.view.FragmentDetail;
import eu.javimar.wirelessval.view.FragmentList;
import eu.javimar.wirelessval.view.MapWifis;

import static eu.javimar.wirelessval.utils.HelperUtils.PLAY_SERVICES_RESOLUTION_REQUEST;
import static eu.javimar.wirelessval.utils.HelperUtils.fetchColor;
import static eu.javimar.wirelessval.utils.HelperUtils.isDatabaseEmpty;
import static eu.javimar.wirelessval.utils.HelperUtils.isGooglePlayServicesAvailable;
import static eu.javimar.wirelessval.utils.HelperUtils.isNetworkAvailable;
import static eu.javimar.wirelessval.utils.PrefUtils.retrieveLongAndLatFromPreferences;
import static eu.javimar.wirelessval.utils.PrefUtils.updateLongAndLatInPreferences;


public class MainActivity extends AppCompatActivity implements
                FragmentList.OnItemSelectedListener,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                LocationListener
{
    private final static int REQUEST_LOCATION_PERMISSION_FINE = 0;
    private final static int REQUEST_LOCATION_PERMISSION_COARSE = 1;
    public static boolean HAVE_LOCATION_PERMISSION = false;

    private static String LOG_TAG = MainActivity.class.getName();

    /** The instance to access the DB */
    WifiDbHelper mDbHelper;

    /** variable to detect if we are on a phone or a tablet */
    public static boolean sTabletView = false;

    /** Google API location variables */
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    /** Refresh time */
    private static final long ONE_MINUTE = 60 * 1000;

    /** Current position */
    public static GeoPoint sCurrentPosition;

    // FragmentList instance
    FragmentList mFragmentList;
    FragmentAbout mFragmentAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);
        final CollapsingToolbarLayout collapsingToolbarLayout =
                findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        // get the palette from the image for the toolbar
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.valencia);
        Palette palette = Palette.from(bitmap).generate();
        Palette.Swatch vibrant = palette.getVibrantSwatch();
        Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();

        if(vibrant!= null && darkVibrant != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(darkVibrant.getRgb());
            }
            collapsingToolbarLayout.setContentScrimColor(vibrant.getRgb());
            collapsingToolbarLayout.setStatusBarScrimColor(darkVibrant.getRgb());
            collapsingToolbarLayout.setCollapsedTitleTextColor(vibrant.getTitleTextColor());
        }

        // check if Google Play Services is installed
        isGooglePlayServicesAvailable(this);

        // load wifi networks only when app is first installed
        if (isNetworkAvailable(this) && isDatabaseEmpty(this))
        {
            loadWifisFromServer();
        }

        // build and access Google Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // check if we have last known coordinates, only at app start
        if(sCurrentPosition == null)
            retrieveLongAndLatFromPreferences(this);

        // want to lock orientation in tablets to landscape only :-/
        if(getResources().getBoolean(R.bool.land_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // manage control permissions for location services
        askForLocationPermission();

        //* Instantiate the Fragments
        mFragmentList = new FragmentList();
        mFragmentAbout = new FragmentAbout();

        // Attach the fragment list on app main entry
        if(savedInstanceState == null)
        {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_main, mFragmentList)
                    .commit();
            // Set the listener
            mFragmentList.setItemListener(this);
        }



        // Check if fragment detail view is active and visible
        View detailsFrame = findViewById(R.id.fragment_detail);
        sTabletView = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        // Bottom navigation only for phone view
        if(!sTabletView)
            setupBottomNavigationBar();

        // creates the DB if first time, and we have the instance already ready
        mDbHelper = new WifiDbHelper(this);
    }


    private void setupBottomNavigationBar()
    {
        AHBottomNavigation mBottomNavigation =
                (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item0 = new AHBottomNavigationItem(
                R.string.bn_list, R.drawable.ic_view_list, R.color.colorOpinion3);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(
                R.string.bn_map, R.drawable.ic_map, R.color.colorOpinion5);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(
                R.string.bn_info, R.drawable.ic_info, R.color.materialRed300);

        mBottomNavigation.addItem(item0);
        mBottomNavigation.addItem(item1);
        mBottomNavigation.addItem(item2);
        mBottomNavigation.setCurrentItem(0);

        mBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigation.setBehaviorTranslationEnabled(true);

        // Colors for selected (active) and non-selected items
        mBottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(this, R.color.secondary_text));
        // Enables color Reveal effect
        mBottomNavigation.setColored(true);

        mBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener()
        {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected)
            {
                Intent i;
                // listen to item clicks
                switch (position)
                {
                    case 0:
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_main, mFragmentList)
                                .commit();
                        // Set the listener
                        mFragmentList.setItemListener(MainActivity.this);
                        break;
                    case 1:
                        i = new Intent(MainActivity.this, MapWifis.class);
                        startActivity(i);
                        break;
                    case 2:
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_main, mFragmentAbout)
                                .commit();
                        break;
                }
                return true;
            }
        });
        mBottomNavigation.setTranslucentNavigationEnabled(true);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        isGooglePlayServicesAvailable(this);
        if (HAVE_LOCATION_PERMISSION)
        {
            // connect the client only if we have permission
            mGoogleApiClient.connect();
        }

        if (!isNetworkAvailable(this))
        {

            Toasty.error(this, getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStop()
    {
        super.onStop();
        if (HAVE_LOCATION_PERMISSION)
        {
            // disconnect the client
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        }

        if(sCurrentPosition != null)
        {
            // update location in preferences
            updateLongAndLatInPreferences(this,
                    new GeoPoint(sCurrentPosition.getLongitud(),
                            sCurrentPosition.getLatitude()));
        }
    }


    /** MENU OPTIONS */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if(sTabletView)
        {
            menu.findItem(R.id.action_maps).setVisible(true);
            menu.findItem(R.id.action_about).setVisible(true);
        }
        else
        // hide the options that are already in the bottom navigation bar for phone view
        {
            menu.findItem(R.id.action_maps).setVisible(false);
            menu.findItem(R.id.action_about).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        Intent i;
        switch (id)
        {
            case R.id.action_settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.action_maps:
                i = new Intent(this, MapWifis.class);
                startActivity(i);
                return true;
            case R.id.action_about:
                i = new Intent(this, FragmentAbout.class);
                startActivity(i);
                return true;
            case R.id.action_load:
                if (!isNetworkAvailable(this))
                {
                    Toasty.error(this, getString(R.string.no_internet_connection),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadWifisFromServer();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /** LOCATION PERMISSION */
    private void askForLocationPermission()
    {
        // if we don't have the permission to access fine location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Este método muestra true si la app solicita el permiso anteriormente
            // y el usuario rechaza la solicitud
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {
                // we show an explanation why it is needed
                Snackbar.make(findViewById(android.R.id.content), R.string.permission_location,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION_PERMISSION_FINE);
                            }
                        }).show();
            }
            else
            {
                // no explanation needed, request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION_FINE);
            }
        }
        else
        {
            HAVE_LOCATION_PERMISSION = true;
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION_COARSE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_LOCATION_PERMISSION_FINE:
                // If request is cancelled, the result arrays are empty.
                // permission granted
                // permission denied, disable functionality that depends on this permission.
                HAVE_LOCATION_PERMISSION = grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;

            case REQUEST_LOCATION_PERMISSION_COARSE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission granted
                    HAVE_LOCATION_PERMISSION = true;
                }
                break;
            // other 'case' lines to check for other permissions this app might request
        }
    }



    /** ON ACTIVITY RESULT */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // comes from asking user to install GooglePlayServices
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLAY_SERVICES_RESOLUTION_REQUEST:
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Google Play Services must be installed.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            }).show();
                }
                break;
        }
    }



    /** GOOGLE API FOR LOCATION IMPLEMENT METHODS */
    @Override
    public void onConnected(Bundle bundle)
    {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(ONE_MINUTE);

        if(HAVE_LOCATION_PERMISSION)
        {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int cause)
    {
        mGoogleApiClient.connect();
    }
    // if the location fails
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i(LOG_TAG, "Connection has failed " + result.getErrorCode());
    }
    /** Capture the location info in here */
    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            sCurrentPosition.setLatitude(location.getLatitude());
            sCurrentPosition.setLongitud(location.getLongitude());
        }
    }



    /**
     *  When the FragmentList receives an onclick event for a list item,
     *  it's passed back to MainActivity through this method so that we can
     *  deliver it to the FragmentDetail accordingly, and update it
     */
    @Override
    public void onItemSelected(Uri wifi)
    {
        if (wifi != null)
        {
            if (!sTabletView) // phone view
            {
                // If showing only FragmentList start the DetailActivity
                // and pass it the info
                Intent intent = new Intent(this, DetailActivity.class);
                intent.setData(wifi);
                startActivity(intent);
            }
            else // tablet view
            {
                FragmentDetail mFragmentDetail = (FragmentDetail)getFragmentManager()
                        .findFragmentById(R.id.fragment_detail);
                if (mFragmentDetail == null) // first time on click is null
                {
                    // hide the textview
                    findViewById(R.id.text_empty_detail).setVisibility(View.GONE);
                    // add fragment programmatically to the framelayout
                    mFragmentDetail = new FragmentDetail();
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragment_detail, mFragmentDetail)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                }
                mFragmentDetail.displayFragmentDetail(wifi);
            }
        }
    }


    /** Load the WiFi networks from the server via IntentService */
    private void loadWifisFromServer()
    {
        Intent updateTokenIntent = new Intent(this, LoaderIntentService.class);
        updateTokenIntent.setAction(LoadingTasks.ACTION_LOAD_WIFIS);
        startService(updateTokenIntent);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // store last known coordinates
        if(sCurrentPosition != null)
        {
            // update location in preferences
            updateLongAndLatInPreferences(this,
                    new GeoPoint(sCurrentPosition.getLongitud(),
                            sCurrentPosition.getLatitude()));
        }
    }




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
    }








} // END OF MAIN

/**
 * Wireless Valencia
 *
 * @author Javier Martín
 * @email: javimardeveloper@gmail.com
 * @link http://www.javimar.eu
 * @package eu.javimar.wirelessvlc
 * @version 1.3
 *
BSD 3-Clause License

Copyright (c) 2016, 2019 JaviMar
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

import static eu.javimar.wirelessval.utils.HelperUtils.PLAY_SERVICES_RESOLUTION_REQUEST;
import static eu.javimar.wirelessval.utils.HelperUtils.isGooglePlayServicesAvailable;
import static eu.javimar.wirelessval.utils.HelperUtils.isNetworkAvailable;
import static eu.javimar.wirelessval.utils.HelperUtils.stripNonValidXMLCharacters;
import static eu.javimar.wirelessval.utils.PrefUtils.retrieveLongAndLatFromPreferences;
import static eu.javimar.wirelessval.utils.PrefUtils.updateLongAndLatInPreferences;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.model.Wifi;
import eu.javimar.wirelessval.parser.WifiParserSax;
import eu.javimar.wirelessval.utils.GeoPoint;
import eu.javimar.wirelessval.view.AboutActivity;
import eu.javimar.wirelessval.view.DetailActivity;
import eu.javimar.wirelessval.view.FragmentDetail;
import eu.javimar.wirelessval.view.FragmentList;
import eu.javimar.wirelessval.view.MapWifis;
import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
                FragmentList.OnItemSelectedListener,
                GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener,
                LocationListener
{
    @BindView(R.id.toolbarMain) Toolbar mToolbar;
    @BindView(R.id.collapse_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    // URL for the Ayto de Valencia web service for the list of the city Wireless spots
    private static final String WIFI_URL = "http://mapas.valencia.es/lanzadera/opendata/wifi/KML";

    private final static int REQUEST_LOCATION_PERMISSION_FINE = 0;
    private final static int REQUEST_LOCATION_PERMISSION_COARSE = 1;
    public static boolean HAVE_LOCATION_PERMISSION = false;

    private WifiViewModel mWifiViewModel;

    private static final String LOG_TAG = MainActivity.class.getName();

    /** variable to detect if we are on a phone or a tablet */
    public static boolean sTabletView = false;

    /** Google API location variables */
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    /** Refresh time */
    private static final long REFRESH_TIME = 3 * 60 * 1000; // 3 minutes

    /** Current position */
    public static GeoPoint sCurrentPosition;

    // FragmentList instance
    private FragmentList mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // want to lock orientation in tablets to landscape only :-/
        if(getResources().getBoolean(R.bool.land_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Toolbar
        setSupportActionBar(mToolbar);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        // Init ViewModel
        mWifiViewModel = new ViewModelProvider(MainActivity.this,
                new WifiViewModelFactory(getApplication(),0, 0))
                .get(WifiViewModel.class);

        // Get the intent, verify the action and get the query
        if(Intent.ACTION_SEARCH.equals(getIntent().getAction()))
        {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            search4Wifis(query);
        }

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
        if (isNetworkAvailable(this) && isDatabaseEmpty())
        {
            loadWifisFromServer();
        }

        // build and access Google Location Services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null) return;

                for(Location location : locationResult.getLocations())
                {
                    // location data
                    location.getLatitude();
                    location.getLongitude();
                    location.getTime();
                    location.getAltitude();
                }
            }
        };

        // check if we have last known coordinates, only at app start
        if(sCurrentPosition == null) retrieveLongAndLatFromPreferences(this);

        // manage control permissions for location services
        askForLocationPermission();

        // Check if fragment detail view is active and visible
        View detailsFrame = findViewById(R.id.fragment_detail);
        sTabletView = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if(!sTabletView)
        {
            // Set the listener
            mFragmentList = (FragmentList) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_list_handset);
            mFragmentList.setItemListener(this);
        }
        else
        {
            mFragmentList = (FragmentList) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_list_tablet);
            mFragmentList.setItemListener(this);
        }
    }

    private boolean isDatabaseEmpty()
    {
        return mWifiViewModel.getCountNumberOfWifis() == 0;
    }

    private void search4Wifis(String query)
    {
        mFragmentList.passSearchResults(query);
    }

    /** MENU OPTIONS */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        //searchView.setColor(getResources().getColor(R.color.white));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setIconifiedByDefault(true);

        SearchView.SearchAutoComplete searchAutoComplete =
                searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorAccent));
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.materialGreen100));

        // Get the search close button image view
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        // Set on click listener
        closeButton.setOnClickListener(v -> //onClick() :-)
        {
            //Clear query
            searchView.setQuery("", false);
            //Collapse the action view
            searchView.onActionViewCollapsed();
            // Restablish Recycler View in FragmentList
            mFragmentList.resetRecycler();
        });
        return true;
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
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.action_load:
                if (!isNetworkAvailable(this))
                {
                    Toasty.warning(this, getString(R.string.no_internet_connection),
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
                        .setAction("OK", view -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_LOCATION_PERMISSION_FINE)).show();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
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
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected())
                {
                    mGoogleApiClient.connect();
                }
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Snackbar.make(findViewById(android.R.id.content),
                        "Google Play Services must be installed.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", view -> finish()).show();
            }
        }
    }

    /** GOOGLE API FOR LOCATION IMPLEMENT METHODS */
    @Override
    public void onConnected(Bundle bundle)
    {
        if(HAVE_LOCATION_PERMISSION)
        {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location ->
                    {
                        // Got last known location. In some rare situations, this can be null.
                        if (location != null)
                        {
                            sCurrentPosition.setLatitude(location.getLatitude());
                            sCurrentPosition.setLongitude(location.getLongitude());
                        }
                    });
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
        Toasty.warning(this, "Connection has failed", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "Connection has failed " + result.getErrorCode());
    }

    /** Capture the location info in here */
    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            sCurrentPosition.setLatitude(location.getLatitude());
            sCurrentPosition.setLongitude(location.getLongitude());
        }
    }

    private void startLocationUpdates()
    {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(REFRESH_TIME);
        mFusedLocationClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates()
    {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     *  When the FragmentList receives an onclick event for a list item,
     *  it's passed back to MainActivity through this method so that we can
     *  deliver it to the FragmentDetail accordingly, and update it
     */
    @Override
    public void onItemSelected(String[] wifi)
    {
        if (wifi != null)
        {
            if (!sTabletView) // phone view
            {
                // If showing only FragmentList start the DetailActivity
                // and pass it the info
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("wifiKey", wifi);
                startActivity(intent);
            }
            else // tablet view
            {
                FragmentDetail mFragmentDetail = (FragmentDetail)getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_detail);
                if (mFragmentDetail == null) // first time on click is null
                {
                    // hide the textview
                    findViewById(R.id.text_empty_detail).setVisibility(View.GONE);
                    // add fragment programmatically to the framelayout
                    mFragmentDetail = new FragmentDetail();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_detail, mFragmentDetail)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                    mFragmentDetail.displayFragmentDetail1(wifi);
                }
                else // when clicking again and again after the first click
                {
                    mFragmentDetail.displayFragmentDetail2(wifi);
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search4Wifis(query);
        }
    }

    /** Load the WiFi networks from the server via IntentService */
    private void loadWifisFromServer()
    {
        new DownloadFilesTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFilesTask extends AsyncTask<Void, Void, Void>
    {
        List<Wifi> wifisListFromServer;
        boolean failToLoad;

        protected Void doInBackground(Void... params)
        {
            // establish connection with the server
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(WIFI_URL)
                    .build();
            try (Response response = client.newCall(request).execute())
            {
                // remove illegal characters from the XML file
                String goodXml = stripNonValidXMLCharacters(response.body().string());
                // convert "Clean String" into an InputStream so it can be fed into the parser
                InputStream is = new ByteArrayInputStream(goodXml.getBytes());

                WifiParserSax saxparser = new WifiParserSax(is, MainActivity.this);
                wifisListFromServer = saxparser.parse();
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Problem accessing the server while loading wifis", e);
                Toasty.error(MainActivity.this, "Problem accessing the server while loading wifis",
                        Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            if(wifisListFromServer != null)
            {
                // insert List with all wifis into DB
                mWifiViewModel.insertWifis(wifisListFromServer);

                saveDate();

                failToLoad = false;
            }
            else failToLoad = true;

            if (!failToLoad)
            {
                int count = wifisListFromServer.size();
                if(count > 0)
                {
                    Toasty.info(MainActivity.this,
                            String.format(getString(R.string.number_wifis_loaded), count),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toasty.info(MainActivity.this, R.string.no_more_wifis,
                            Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toasty.error(MainActivity.this, R.string.err_loading_wifis,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
        String current = LocalDateTime.now().format(formatter);

        SharedPreferences sharedPref = getSharedPreferences("MY_PREFERENCE_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("LAST_UPDATED", current);
        editor.apply();
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
            startLocationUpdates();
        }
        if (!isNetworkAvailable(this))
        {
            Toasty.warning(this, getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (HAVE_LOCATION_PERMISSION)
        {
            stopLocationUpdates();
            // disconnect the client
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();
        }
        if(sCurrentPosition != null)
        {
            // update location in preferences
            updateLongAndLatInPreferences(this,
                    new GeoPoint(sCurrentPosition.getLongitude(),
                            sCurrentPosition.getLatitude()));
        }
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
                    new GeoPoint(sCurrentPosition.getLongitude(),
                            sCurrentPosition.getLatitude()));
        }
    }
} // END OF MAIN
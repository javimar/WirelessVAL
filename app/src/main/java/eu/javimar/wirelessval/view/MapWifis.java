package eu.javimar.wirelessval.view;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.javimar.wirelessval.MainActivity;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.GeoPoint;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;

import static eu.javimar.wirelessval.MainActivity.HAVE_LOCATION_PERMISSION;


public class MapWifis extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        LoaderManager.LoaderCallbacks<Cursor>
{
    GoogleMap mapa;
    LatLng myLocation, wifiLocation;
    double lat, lng = 0.0;
    String wifiName;
    CameraPosition cameraPosition;

    /** Identifier for the wifi loaders */
    private static final int MAPA_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        MapFragment mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        setTitle(getString(R.string.title_mapa_activity));

        // want to lock orientation in tablets to landscape only :-/
        if(getResources().getBoolean(R.bool.land_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // start the loader to get all wifis
        getLoaderManager().initLoader(MAPA_LOADER, null, this);
    }


    /** LOADER LOGIC */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] projection;

        switch (id)
        {
            case MAPA_LOADER:
                // Define a projection that specifies the columns from the table we care about_layout.
                projection = new String[]
                        {
                                WifiEntry._ID,
                                WifiEntry.COLUMN_WIFI_NAME,
                                WifiEntry.COLUMN_WIFI_LATITUDE,
                                WifiEntry.COLUMN_WIFI_LONGITUDE,
                        };
                // This loader will execute the ContentProvider's query method on a background thread
                return new CursorLoader(
                        this,                   // Parent activity context
                        WifiEntry.CONTENT_URI,  // Provider content URI to query
                        projection,             // Columns to include in the resulting Cursor
                        null,                   // No selection clause
                        null,                   // No selection arguments
                        null);                  // Default sort order
        }
        return null;
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // Bail early if the cursor is null or there is no rows in the cursor
        if (cursor == null || cursor.getCount() < 1)
        {
            return;
        }
        addMarkersForAllWifis(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (HAVE_LOCATION_PERMISSION)
        {
            mapa = map;

            // where I am now
            myLocation = new LatLng(MainActivity.sCurrentPosition.getLatitude(),
                    MainActivity.sCurrentPosition.getLongitud());

            mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mapa.getUiSettings().setZoomControlsEnabled(true);
            mapa.getUiSettings().setCompassEnabled(true);

            mapa.addCircle(new CircleOptions()
                    .center(myLocation)
                    .radius(500) // meters
                    .strokeColor(Color.argb(255, 196, 202, 235))
                    .fillColor(Color.argb(110, 196, 202, 235)));

            mapa.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("Hola")
                    //.snippet(getString(R.string.geo_take_me))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .anchor(0.5f, 0.5f));
        }
    }

    private void addMarkersForAllWifis(Cursor cursor)
    {
        if (mapa == null || cursor == null)
            return;

        MarkerOptions mo = new MarkerOptions();

        // Loop through the cursor
        while (cursor.moveToNext())
        {
            // Find the columns of wifi attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_NAME);
            int lngColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LONGITUDE);
            int latColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LATITUDE);

            wifiName = cursor.getString(nameColumnIndex);
            double longitude = cursor.getDouble(lngColumnIndex);
            double latitude = cursor.getDouble(latColumnIndex);
            wifiLocation = new LatLng(latitude, longitude);

            mo.position(wifiLocation)
                    .title(wifiName)
                    .snippet(new GeoPoint(longitude, latitude).toString());
            mo.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mapa.addMarker(mo);

        }
        // set view to my current location
        moveCameraTo(myLocation);
        mapa.setOnInfoWindowClickListener(this);
    }

    private void moveCameraTo(LatLng coor)
    {
        cameraPosition = CameraPosition.builder()
                .target(coor)
                .zoom(15)
                .bearing(0) // north
                .tilt(40) // some inclination
                .build();
        mapa.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                5000, null); // 5'' animation
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        if (!HAVE_LOCATION_PERMISSION)
            return;
        // Give me data for new query WHERE the Wifi name equals the one I just clicked on
        showWifiInStreetView(marker.getTitle());
    }

    private void showWifiInStreetView(String wifi)
    {
        String[] projection = new String[]
                {
                        WifiEntry._ID,
                        WifiEntry.COLUMN_WIFI_LATITUDE,
                        WifiEntry.COLUMN_WIFI_LONGITUDE,
                };
        String selection = WifiEntry.COLUMN_WIFI_NAME + "=?";
        String [] selectionArgs = new String[] { wifi };

        Cursor cursor = getContentResolver()
                .query(WifiEntry.CONTENT_URI, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.getCount() >= 1)
        {
            // Proceed with moving to the first row of the cursor and reading data from it
            // (This should be the only row in the cursor)
            if (cursor.moveToFirst())
            {
                int lngColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LONGITUDE);
                int latColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LATITUDE);

                lng = cursor.getDouble(lngColumnIndex);
                lat = cursor.getDouble(latColumnIndex);

                Intent i = new Intent(this, PanoramaActivity.class);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                startActivity(i);
            }
            cursor.close();
        }
    }
}

package eu.javimar.wirelessval.view;

import static eu.javimar.wirelessval.MainActivity.HAVE_LOCATION_PERMISSION;
import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.features.wifi.domain.utils.GeoPoint;
import eu.javimar.wirelessval.model.Wifi;
import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;


public class MapWifis extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener
{
    private GoogleMap googleMap;
    private LatLng myLocation;

    private List<Wifi> mWifis;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.wifisMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

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
        observerSetup();
    }

    private void observerSetup()
    {
        WifiViewModel wifiViewModel= new ViewModelProvider(this, new WifiViewModelFactory(getApplication(),
                sCurrentPosition.getLatitude(),
                sCurrentPosition.getLongitude())).get(WifiViewModel.class);

        wifiViewModel.getAllWifisByName().observe(this, wifis->
        {
            if (wifis != null && wifis.size() > 0)
            {
                mWifis = wifis;
                addMarkersForAllWifis();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        if (HAVE_LOCATION_PERMISSION)
        {
            googleMap = map;

            // where I am now
            myLocation = new LatLng(sCurrentPosition.getLatitude(),
                    sCurrentPosition.getLongitude());

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);

            googleMap.addCircle(new CircleOptions()
                    .center(myLocation)
                    .radius(500) // meters
                    .strokeColor(Color.argb(255, 196, 202, 235))
                    .fillColor(Color.argb(110, 196, 202, 235)));

            googleMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("Hola")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .anchor(0.5f, 0.5f));
        }
    }

    private void addMarkersForAllWifis()
    {
        if(googleMap == null) return;

        MarkerOptions mo = new MarkerOptions();

        // Loop through the cursor
        for(Wifi wifi : mWifis)
        {
            LatLng wifiLocation = new LatLng(wifi.getLatitude(),
                    wifi.getLongitude());

            mo.position(wifiLocation)
                    .title(wifi.getWifiName())
                    .snippet(new GeoPoint(wifi.getLongitude(), wifi.getLatitude()).toString());
            mo.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.addMarker(mo);
        }
        // set view to my current location
        moveCameraTo(myLocation);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private void moveCameraTo(LatLng coor)
    {
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(coor)
                .zoom(15)
                .bearing(0) // north
                .tilt(40) // some inclination
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                5000, null); // 5'' animation
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        if (!HAVE_LOCATION_PERMISSION)
            return;
        // Give me the coordinates for new query WHERE the Wifi name equals the one I just clicked on
        showWifiInStreetView(marker.getTitle());
    }

    private void showWifiInStreetView(String name)
    {
        double lat = 0, lng = 0;
        if(mWifis.indexOf(name) != -1)
        {
            lat = mWifis.get(mWifis.indexOf(name)).getLatitude();
            lng = mWifis.get(mWifis.indexOf(name)).getLongitude();
        }
        Intent i = new Intent(this, PanoramaActivity.class);
        i.putExtra("lat", lat);
        i.putExtra("lng", lng);
        startActivity(i);
    }
}

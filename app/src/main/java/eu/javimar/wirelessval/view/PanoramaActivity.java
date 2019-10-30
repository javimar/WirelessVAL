package eu.javimar.wirelessval.view;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import eu.javimar.wirelessval.R;

public class PanoramaActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback
{
    double lat, lng;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view);

        //App bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        setTitle(getString(R.string.title_streetview_activity));
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama)
    {
        lat = getIntent().getDoubleExtra("lat", 0.0);
        lng = getIntent().getDoubleExtra("lng", 0.0);

        if (lat == 0.0 && lng == 0.0)
        {
            panorama.setPosition(new LatLng(39.470465, -0.376226));
        }
        else
        {
            // Set the panorama location on startup, when no panoramas have been loaded.
            panorama.setPosition(new LatLng(lat, lng));
        }
        StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                .bearing(0)
                .build();
        panorama.animateTo(camera,10000);
    }

}
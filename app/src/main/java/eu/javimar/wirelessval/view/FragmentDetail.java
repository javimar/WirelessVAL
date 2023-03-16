package eu.javimar.wirelessval.view;


import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;
import static eu.javimar.wirelessval.MainActivity.sTabletView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.databinding.DetailLayoutBinding;
import eu.javimar.wirelessval.model.Wifi;
import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;

public class FragmentDetail extends Fragment implements OnMapReadyCallback
{
    private DetailLayoutBinding binding;
    private LatLng mWifiLocation;
    private GoogleMap mGoogleMap;

    /** Save states on screen orientation */
    private static final String EDIT_TEXT_CONTENTS = "edit_text_contents";
    private static final String MAPVIEW_BUNDLE_KEY = "map_view_bundle";
    private static final String RATING_BAR_CONTENT = "rating_bar";

    private String[] mWifiKey;
    private WifiViewModel mWifiViewModel;
    private Wifi mWifi;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DetailLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        binding.wifiOpinion.setOnRatingBarChangeListener(null);

        // avoid focusable mode on the edit text
        getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // MapView requires that the Bundle contains _ONLY_ MapView SDK objects or sub-Bundles
        Bundle mapViewBundle = null;
        if(savedInstanceState != null)
        {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            binding.wifiComments.setText(savedInstanceState.getString(EDIT_TEXT_CONTENTS));
            binding.wifiOpinion.setRating(savedInstanceState.getFloat(RATING_BAR_CONTENT));
        }
        binding.mapView.onCreate(mapViewBundle);
        binding.mapView.getMapAsync(this);

        mWifiViewModel= new ViewModelProvider(this,
                new WifiViewModelFactory(getActivity().getApplication(),
                        sCurrentPosition.getLatitude(), sCurrentPosition.getLongitude())).get(WifiViewModel.class);

        mWifiViewModel.findWifi(mWifiKey[0], mWifiKey[1], mWifiKey[2]);
        observerSetup();
    }

    private void observerSetup()
    {
        mWifiViewModel.getSearchWifiResults().observe(getViewLifecycleOwner(), wifis ->
        {
            if(wifis.size() > 0)
            {
                mWifi = mWifiViewModel.getSearchWifiResults().getValue().get(0);

                // Update the views on the screen with the values from the database
                binding.wifiNameDetail.setText(mWifiKey[0]);

                // don't overwrite edit text when screen is rotated if there is a value
                if(mWifi.getComments() != null && !mWifi.getComments().equals(""))
                    binding.wifiComments.setText(mWifi.getComments());

                binding.wifiOpinion.setOnRatingBarChangeListener(
                        (ratingBar, valor, fromUser) -> binding.wifiOpinion.setRating(valor));
                if(mWifi.getOpinion() != 0)
                    binding.wifiOpinion.setRating(mWifi.getOpinion());

                // on tablet view, refresh values always
                if(sTabletView)
                {
                    binding.wifiComments.setText(mWifi.getComments());
                    binding.wifiOpinion.setRating(mWifi.getOpinion());
                }

                // place wifi in Map
                mWifiLocation = new LatLng(mWifi.getLatitude(),
                        mWifi.getLongitude());
                showWifiLocationInMap(mWifiKey[0]);
            }
        });
    }

    public void displayFragmentDetail1(String[] wifi)
    {
        mWifiKey = wifi;
    }

    public void displayFragmentDetail2(String[] wifi)
    {
        mWifiKey = wifi;

        if(sTabletView)
        {
            mWifiViewModel= new ViewModelProvider(this,
                    new WifiViewModelFactory(getActivity().getApplication(),
                            sCurrentPosition.getLatitude(),
                            sCurrentPosition.getLongitude()))
                    .get(WifiViewModel.class);

            mWifiViewModel.findWifi(mWifiKey[0], mWifiKey[1], mWifiKey[2]);
            observerSetup();
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // calls to FragmentTransaction are asynchronous, this gives a NPE
        // since MainActivity won't be attached by the time commit() returns
        getActivity().setTitle(R.string.title_editor_activity);
    }

    @Override
    public void onMapReady(GoogleMap gMap)
    {
        mGoogleMap = gMap;
        LatLng myLocation = new LatLng(sCurrentPosition.getLatitude(), sCurrentPosition.getLongitude());

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(500) // meters
                .strokeColor(Color.argb(255, 196, 202, 235))
                .fillColor(Color.argb(110, 196, 202, 235)));

        if(mWifiKey != null)
            showWifiLocationInMap(mWifiKey[0]);
        else
            showWifiLocationInMap("");
    }

    private void showWifiLocationInMap(String wifiName)
    {
        if(mWifiLocation == null || mGoogleMap == null)
            return;

        // Updates the location of the Map
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mWifiLocation)
                .title(wifiName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .anchor(0.5f, 0.5f));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mWifiLocation)
                .zoom(16)
                .bearing(0) // north
                .tilt(40) // some inclination
                .build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    private void updateWifi()
    {
        String comments = binding.wifiComments.getText().toString().trim();
        float opinion = binding.wifiOpinion.getRating();

        mWifiViewModel.updateOpinion(mWifiKey[0], comments,
                opinion, Double.parseDouble(mWifiKey[1]),
                Double.parseDouble(mWifiKey[2]));
        Toasty.info(getActivity(), getString(R.string.fragment_detail_update_ok),
                Toast.LENGTH_SHORT).show();

        // if we come from the tablet view we cannot close the activity
        if (!sTabletView)
        {
            if (getActivity() != null)
                getActivity().finish();
        }
        else
        {
            getActivity().getSupportFragmentManager()
                    // remove fragment from view
                    .beginTransaction().remove(this).commit();
            // show empty message
            getActivity().findViewById(R.id.text_empty_detail).setVisibility(View.VISIBLE);        }
    }

    /** MENU HANDLING */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if present.
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_save:
                updateWifi();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete a wifi
     */
    private void deleteWifi()
    {
        // Only perform the delete if this is an existing wifi.
        if (mWifiKey != null)
        {
            mWifiViewModel.deleteWifi(mWifiKey[0],
                    Double.parseDouble(mWifiKey[1]),
                    Double.parseDouble(mWifiKey[2]));
            Toasty.info(getActivity(), R.string.wifiDeleted, Toast.LENGTH_SHORT).show();

            // close activity if we are not in a tablet
            if (!sTabletView)
            {
                if (getActivity() != null)
                    getActivity().finish();
            }
            else
            {
                getActivity().getSupportFragmentManager()
                // remove fragment from view
                .beginTransaction().remove(this).commit();
                // show empty message
                getActivity().findViewById(R.id.text_empty_detail).setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDeleteConfirmationDialog()
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.fragment_detail_changes_delete_confirmation);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            // User clicked the "Delete" button
            deleteWifi();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            // User clicked the "Cancel" button, so dismiss the dialog
            if (dialog != null) {
                dialog.dismiss();
            }
        });
        // Create and show the AlertDialog
        builder.create().show();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        binding.mapView.onDestroy();
        binding = null;
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    /** Save edit text contents on rotation change and MapView */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null)
        {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        binding.mapView.onSaveInstanceState(mapViewBundle);

        outState.putString(EDIT_TEXT_CONTENTS, binding.wifiComments.getText().toString());
        outState.putFloat(RATING_BAR_CONTENT, binding.wifiOpinion.getRating());
    }
}
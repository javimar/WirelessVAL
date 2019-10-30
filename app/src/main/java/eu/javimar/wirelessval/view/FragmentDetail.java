package eu.javimar.wirelessval.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;
import static eu.javimar.wirelessval.MainActivity.sTabletView;
import static eu.javimar.wirelessval.utils.HelperUtils.deleteWifiFromDb;

public class FragmentDetail extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback
{
    private MapView mMapView;
    private LatLng wifiLocation;
    private GoogleMap map;

    /** Save states on screen orientation */
    private static final String EDIT_TEXT_CONTENTS = "edit_text_contents";
    private static final String MAPVIEW_BUNDLE_KEY = "map_view_bundle";
    private static final String RATING_BAR_CONTENT = "rating_bar";

    /** Content URI for the existing wifi */
    private Uri mCurrentWifiUri;

    /** Identifier for the wifi data loader */
    private static final int EXISTING_WIFI_LOADER = 0;

    /** Layout variables */
    private TextView mNameText;
    private TextInputEditText mCommentsEditText;
    private RatingBar mOpinionRatingBar;

    /** Store the wifi name for rotation */
    private String mWfiName;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // para indicar que el fragmento intenta agregar elementos al menú de opciones
        // sino el fragmento no recibirá una llamada a onCreateOptionsMenu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.detail_layout, container, false);

        mNameText = rootView.findViewById(R.id.wifi_name_detail);
        mCommentsEditText = rootView.findViewById(R.id.wifi_comments);
        mOpinionRatingBar =  rootView.findViewById(R.id.wifi_opinion);
        mOpinionRatingBar.setOnRatingBarChangeListener(null);
        mMapView = rootView.findViewById(R.id.mapView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // avoid focusable mode on the edit text
        getActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // MapView requires that the Bundle contains _ONLY_ MapView SDK objects or sub-Bundles
        Bundle mapViewBundle = null;
        if(savedInstanceState != null)
        {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            mCommentsEditText.setText(savedInstanceState.getString(EDIT_TEXT_CONTENTS));
            mOpinionRatingBar.setRating(savedInstanceState.getFloat(RATING_BAR_CONTENT));
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // calls to FragmentTransaction are asynchronous, this gives a NPE
        // since MainActivity won't be attached by the time commit() returns
        getActivity().setTitle(R.string.title_editor_activity);
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
        mMapView.onSaveInstanceState(mapViewBundle);

        outState.putString(EDIT_TEXT_CONTENTS, mCommentsEditText.getText().toString());
        outState.putFloat(RATING_BAR_CONTENT, mOpinionRatingBar.getRating());
    }


    @Override
    public void onMapReady(GoogleMap gMap)
    {
        map = gMap;
        LatLng myLocation = new LatLng(sCurrentPosition.getLatitude(), sCurrentPosition.getLongitud());

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(500) // meters
                .strokeColor(Color.argb(255, 196, 202, 235))
                .fillColor(Color.argb(110, 196, 202, 235)));

        if(mWfiName != null)
            showWifiLocationInMap(mWfiName);
        else
            showWifiLocationInMap("");
    }


    private void showWifiLocationInMap(String wifiName)
    {
        if(wifiLocation == null || map == null)
            return;

        // Updates the location of the Map
        map.addMarker(new MarkerOptions()
                .position(wifiLocation)
                .title(wifiName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .anchor(0.5f, 0.5f));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(wifiLocation)
                .zoom(16)
                .bearing(0) // north
                .tilt(40) // some inclination
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }


    /** Called from Detail Activity */
    public void displayFragmentDetail(Uri wifi)
    {
        mCurrentWifiUri = wifi;

        if (mCurrentWifiUri != null)
        {
            if (!sTabletView)
            {
                // Initialize a loader to read the wifi data from the database
                // and display the current values in the editor, since we kill the activity
                // everytime we update, loader data is always set to initialize
                getLoaderManager().initLoader(EXISTING_WIFI_LOADER, null, this);
            }
            else
            {
                // Tablet mode, need to restart loader to refresh detail everytime we click
                // on a different list item
                getLoaderManager().restartLoader(EXISTING_WIFI_LOADER, null, this);
            }
        }
    }


    /** LOADER HANDLING */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // Since the editor shows all attributes, define a projection that contains
        // all columns from the wifi table
        String[] projection =
                {
                        WifiEntry._ID,
                        WifiEntry.COLUMN_WIFI_NAME,
                        WifiEntry.COLUMN_WIFI_LATITUDE,
                        WifiEntry.COLUMN_WIFI_LONGITUDE,
                        WifiEntry.COLUMN_WIFI_INFO,
                        WifiEntry.COLUMN_WIFI_OPINION
                };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                getActivity(),          // Parent activity context
                mCurrentWifiUri,        // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1)
        {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst())
        {
            // Find the columns of wifi attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_NAME);
            int lngColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LONGITUDE);
            int latColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LATITUDE);
            int InfoColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_INFO);
            int opinionColumnIndex = cursor.getColumnIndex(WifiEntry.COLUMN_WIFI_OPINION);

            mWfiName = cursor.getString(nameColumnIndex);
            String wifiComments = cursor.getString(InfoColumnIndex);
            String wifiOpinion = cursor.getString(opinionColumnIndex);

            // Update the views on the screen with the values from the database
            mNameText.setText(mWfiName);

            // don't overwrite edit text when screen is rotated if there is a value
            if(wifiComments != null && !wifiComments.equals(""))
                mCommentsEditText.setText(wifiComments);

            if(Float.parseFloat(wifiOpinion) != 0)
                mOpinionRatingBar.setRating(Float.parseFloat(wifiOpinion));
            mOpinionRatingBar.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar,
                                                    float valor, boolean fromUser) {
                            mOpinionRatingBar.setRating(valor);
                        }
                    });

            // on tablet view, refresh values always
            if(sTabletView)
            {
                mCommentsEditText.setText(wifiComments);
                mOpinionRatingBar.setRating(Float.parseFloat(wifiOpinion));
            }

            // place wifi in Map
            wifiLocation = new LatLng(cursor.getDouble(latColumnIndex),
                    cursor.getDouble(lngColumnIndex));
            showWifiLocationInMap(mWfiName);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mNameText.setText("");
        mCommentsEditText.setText("");
        mOpinionRatingBar.setRating(0);
    }


    private void updateWifi()
    {
        String info = mCommentsEditText.getText().toString().trim();
        float opinion = mOpinionRatingBar.getRating();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(WifiEntry.COLUMN_WIFI_INFO, info);
        values.put(WifiEntry.COLUMN_WIFI_OPINION, opinion);

        int rowsAffected = getActivity()
                .getContentResolver()
                .update(mCurrentWifiUri, values, null, null);

        // Show message depending on whether or not the update was successful.
        if (rowsAffected == 0)
        {
            // If no rows were affected, then there was an error with the update.
            Toasty.error(getActivity(), getString(R.string.fragment_detail_update_failed),
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Otherwise, the update was successful.
            Toasty.info(getActivity(), getString(R.string.fragment_detail_update_ok),
                    Toast.LENGTH_SHORT).show();
        }
        // if we come from the tablet view we cannot close the activity
        if (!sTabletView)
        {
            if (getActivity() != null)
                getActivity().finish();
        }
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
        if (mCurrentWifiUri != null)
        {
            deleteWifiFromDb(getActivity(), mCurrentWifiUri);

            // close activity if we are not in a tablet
            if (!sTabletView)
            {
                if (getActivity() != null)
                    getActivity().finish();
            }
            else
            {
                // remove fragment from view
                getFragmentManager().beginTransaction().remove(this).commit();
                // show empty message textview
                getActivity().findViewById(R.id.text_empty_detail).setVisibility(View.VISIBLE);
            }
        }
    }

    private void showDeleteConfirmationDialog()
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.fragment_detail_changes_delete_confirmation);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button
                deleteWifi();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause()
    {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}

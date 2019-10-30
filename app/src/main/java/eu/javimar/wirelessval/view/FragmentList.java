package eu.javimar.wirelessval.view;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.app.Fragment;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;
import eu.javimar.wirelessval.utils.SwipeUtil;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;
import static eu.javimar.wirelessval.MainActivity.sTabletView;


/** Keep all code that concerns content in a fragment inside that fragment,
 *  rather than putting it in its host activity's code (MainActivity)
 */
public class FragmentList extends Fragment implements
        WifiAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>
{
    /** Save the recycler position on screen orientation and when coming back from DETAIL*/
    static int lastFirstVisiblePosition;

    /** Identifier for the wifi database loader */
    private static final int WIFI_DB_LOADER = 0;

    /** Reference to implementation de OnItemSelectedListener by MainActivity */
    OnItemSelectedListener mListener;

    /** Holds the current URI for the wifi being selected */
    Uri mCurrentWifiUri;

    /** Adapter and RecyclerView modified for the list of fallas */
    WifiAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    /** Empty view if no wifis are present */
    TextView mEmptyView;
    ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.list_layout, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mEmptyView = getActivity().findViewById(R.id.empty_view);
        mProgressBar = getActivity().findViewById(R.id.loading_indicator);

        // find a reference to the RecyclerView
        mRecyclerView = getActivity().findViewById(R.id.wifiRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new WifiAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        // just allow swipe functionality on mobile view
        if(!sTabletView)
            setSwipeForRecyclerView();

        // load wifis in GUI from database
        getLoaderManager().initLoader(WIFI_DB_LOADER, null, this);
    }


    private void setSwipeForRecyclerView()
    {
        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity())
        {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                WifiAdapter adapter = (WifiAdapter) mRecyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
            }
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                WifiAdapter adapter = (WifiAdapter) mRecyclerView.getAdapter();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        // set swipe label
        swipeHelper.setLeftSwipeLabel(getString(R.string.deleteString));
        // set swipe background-Color
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(getActivity(), R.color.colorOpinion1));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case WIFI_DB_LOADER:
                String [] projection = new String[]
                        {
                                WifiEntry._ID,
                                WifiEntry.COLUMN_WIFI_NAME,
                                WifiEntry.COLUMN_WIFI_LATITUDE,
                                WifiEntry.COLUMN_WIFI_LONGITUDE,
                                WifiEntry.COLUMN_WIFI_OPINION
                        };
                SharedPreferences prefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                int sortPref = Integer.parseInt(prefs.getString(
                        getString(R.string.settings_sort_criteria_key), "0"));
                switch (sortPref)
                {
                    case 0:
                        return new CursorLoader(
                                getActivity(),          // Parent activity context
                                WifiEntry.CONTENT_URI,  // Provider content URI to query
                                projection,             // Columns to include in the resulting Cursor
                                null,                   // No selection clause
                                null,                   // No selection arguments
                                WifiEntry.COLUMN_WIFI_NAME + " ASC");
                    case 1:
                        String [] projectionDistance = new String[]
                        {
                            WifiEntry._ID,
                            WifiEntry.COLUMN_WIFI_NAME,
                            WifiEntry.COLUMN_WIFI_LATITUDE,
                            WifiEntry.COLUMN_WIFI_LONGITUDE,
                            WifiEntry.COLUMN_WIFI_OPINION,
                            // manhattan distance,  cannot use GeoPoint distance in the query
                            "MAX (" + WifiEntry.COLUMN_WIFI_LATITUDE + ", " +
                                    sCurrentPosition.getLatitude() + ") - MIN (" +
                                    WifiEntry.COLUMN_WIFI_LATITUDE + ", " +
                                    sCurrentPosition.getLatitude() + ") + MAX (" +
                                    WifiEntry.COLUMN_WIFI_LONGITUDE + ", " +
                                    sCurrentPosition.getLongitud() + ") - MIN (" +
                                    WifiEntry.COLUMN_WIFI_LONGITUDE + ", " +
                                    sCurrentPosition.getLongitud() + ") AS DISTANCE"
                        };
                        return new CursorLoader(
                                getActivity(),
                                WifiEntry.CONTENT_URI,
                                projectionDistance,
                                null,
                                null,
                                "DISTANCE ASC");
                    case 2:
                        return new CursorLoader(
                                getActivity(),
                                WifiEntry.CONTENT_URI,
                                projection,
                                null,
                                null,
                                WifiEntry.COLUMN_WIFI_OPINION + " DESC");
                    default:
                        return new CursorLoader(
                                getActivity(),
                                WifiEntry.CONTENT_URI,
                                projection,
                                null,
                                null,
                                null);
                }
        }
        return null;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        if ((cursor == null || cursor.getCount() < 1))
        {
            mEmptyView.setText(R.string.no_wifis_found);
        }
        else
            mEmptyView.setVisibility(View.GONE);

        switch (loader.getId())
        {
            case WIFI_DB_LOADER:
                // Update CursorAdapter with this new cursor containing fresh wifi data
                mAdapter.swapCursor(cursor);
                mRecyclerView.invalidate();
                break;
        }
        // Hide the progress bar
        mProgressBar.setVisibility(View.GONE);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        if (loader.getId() == WIFI_DB_LOADER)
        // Callback called when the data needs to be deleted
            mAdapter.swapCursor(null);
    }


    @Override
    public void onListItemClick(int idWifi)
    {
        // Make the content URI that represents the specific wifi that was clicked on,
        // by appending the "id" (passed as input to this method) onto the
        // WifiEntry#CONTENT_URI}.
        // For example, the URI would be "content://eu.javimar.wireslessvlc/wifis/2"
        // if the wifi with ID 2 was clicked on.
        mCurrentWifiUri = ContentUris.withAppendedId(WifiEntry.CONTENT_URI, idWifi);
        // Pass the Uri to MainActivity for the fragment(s) if tablet or phone
        mListener.onItemSelected(mCurrentWifiUri);
    }


    /** Save and restore position of the RecyclerView */
    @Override
    public void onPause()
    {
        super.onPause();
        lastFirstVisiblePosition = ((LinearLayoutManager)mRecyclerView
                .getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
    }


    /** MainActivity must implement this interface */
    public interface OnItemSelectedListener
    {
        void onItemSelected(Uri wifi);
    }
    public void setItemListener(OnItemSelectedListener listener)
    {
        this.mListener = listener;
    }

}

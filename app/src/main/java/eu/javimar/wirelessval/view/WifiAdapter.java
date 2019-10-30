package eu.javimar.wirelessval.view;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.GeoPoint;
import eu.javimar.wirelessval.model.WifiContract.WifiEntry;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;
import static eu.javimar.wirelessval.MainActivity.sTabletView;
import static eu.javimar.wirelessval.utils.HelperUtils.deleteWifiFromDb;


@SuppressWarnings("all")
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder>
{
    private Context mContext;
    private Cursor mCursor;

    private static String LOG_TAG = WifiAdapter.class.getName();

    /** To Keep track of swiped Items create arraylist “itemsPendingRemoval”
     * on Swipe add to item to itemsPendingRemoval list
     * on Undo Remove item from itemsPendingRemoval list
     */
    private List<GeoPoint> itemsPendingRemoval; // GeoPoints are unique for each wifi

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private Handler handler = new Handler(); // handler for running delayed runnables
    // map of items to pending runnables, so we can cancel a wifi removal if needed
    HashMap<GeoPoint, Runnable> pendingRunnables = new HashMap<>();

    /** An on-click handler to make it easy for an Activity to interface with our RecyclerView */
    private final ListItemClickListener mOnClickListener;

    /** The interface that receives onClick messages */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    /** Adapter constructor */
    public WifiAdapter(ListItemClickListener listener, Context context)
    {
        mContext = context;
        mOnClickListener = listener;
        itemsPendingRemoval = new ArrayList<>();
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     */
    @Override
    public WifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.wifi_item, parent, false);
        return new WifiViewHolder(view);
    }


    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the wifi
     * details for this particular position, using the "position" argument passed into us.
     */
    @Override
    public void onBindViewHolder(WifiViewHolder holder, final int position)
    {
        if (!mCursor.moveToPosition(position))
            return; // bail if returned null

        mCursor.moveToPosition(position);
        // get necessary information to compare if item is already waiting to be removed
        final GeoPoint wifiLocation = getCoordinatesFromCursor();

        if (itemsPendingRemoval.contains(wifiLocation))
        {
            // show swipe layout and hide the regular layout
            holder.regularLayout.setVisibility(View.GONE);
            holder.swipeLayout.setVisibility(View.VISIBLE);
            holder.undoTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    undoOption(wifiLocation, position);
                }
            });
        }
        else
        {
            // Proceed normally with the regular layout and hide the swipe layout
            holder.regularLayout.setVisibility(View.VISIBLE);
            holder.swipeLayout.setVisibility(View.GONE);

            // Find the columns of wifi attributes that we're interested in
            int nameColumnIndex = mCursor.getColumnIndex(WifiEntry.COLUMN_WIFI_NAME);
            int opinionColumnIndex = mCursor.getColumnIndex(WifiEntry.COLUMN_WIFI_OPINION);

            // Read the wifi attributes from the Cursor for the current wifi
            String wifiName = mCursor.getString(nameColumnIndex);
            String wifiOpinion = mCursor.getString(opinionColumnIndex);

            // Update the TextViews with the attributes for the current wifi
            holder.nameTextView.setText(wifiName);
            holder.opinionTextView.setText(String.valueOf(Double.parseDouble(wifiOpinion)));

            if (sCurrentPosition != null)
            {
                int d = (int) sCurrentPosition.distance(wifiLocation);
                if (d < 2000)
                {
                    holder.distanceView.setText(d + " m");
                }
                else
                {
                    holder.distanceView.setText(d / 1000 + " Km");
                }
            }
            else
            {
                holder.distanceView.setText("--");
            }
            GradientDrawable sectionCircle = (GradientDrawable)holder.opinionTextView.getBackground();
            // Get the appropriate background color based on the current section
            int sectionColor = getOpinionColor(Double.parseDouble(wifiOpinion));
            // Set the color on circle
            sectionCircle.setColor(sectionColor);
        }
    }


    private GeoPoint getCoordinatesFromCursor()
    {
        double wifiLng = mCursor.getDouble(mCursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LONGITUDE));
        double wifiLat = mCursor.getDouble(mCursor.getColumnIndex(WifiEntry.COLUMN_WIFI_LATITUDE));
        return new GeoPoint(wifiLng, wifiLat);
    }


    private void undoOption(GeoPoint wifiLoc, int position)
    {
        Runnable pendingRemovalRunnable = pendingRunnables.get(wifiLoc);
        pendingRunnables.remove(wifiLoc);
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.remove(wifiLoc);
        // this will rebind the row in "normal" state
        notifyItemChanged(position);
    }

    /** Called when swipe action is initiated */
    public void pendingRemoval(final int position)
    {
        mCursor.moveToPosition(position);
        final GeoPoint wifiLoc = getCoordinatesFromCursor();
        if (!itemsPendingRemoval.contains(wifiLoc))
        {
            // there can be only one per run
            itemsPendingRemoval.add(wifiLoc);
            // this will redraw row in "undoTextView" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable()
            {
                @Override
                public void run() {
                    remove(wifiLoc, position);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(wifiLoc, pendingRemovalRunnable);
        }
    }

    // Delete the wifi from database
    public void remove(GeoPoint wifiLoc, int position)
    {
        if (itemsPendingRemoval.contains(wifiLoc))
        {
            // clear list
            itemsPendingRemoval.remove(wifiLoc);
        }
        Uri uri = ContentUris.withAppendedId(WifiEntry.CONTENT_URI,
                getIdWifi(position));

        if(sTabletView)
        {

        }

        deleteWifiFromDb(mContext, uri);
        notifyItemRemoved(position);
    }

    public boolean isPendingRemoval(int position)
    {
        mCursor.moveToPosition(position);
        return itemsPendingRemoval.contains(getCoordinatesFromCursor());
    }


    /**
     * Cache of the children views for a wifi list item.
     */
    public class WifiViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private TextView nameTextView, opinionTextView, distanceView, undoTextView;
        private LinearLayout regularLayout, swipeLayout;

        public WifiViewHolder(View itemView)
        {
            super(itemView);
            // Find individual views that we want to modify in the list item layout
            nameTextView = (TextView) itemView.findViewById(R.id.wifi_name);
            opinionTextView = (TextView) itemView.findViewById(R.id.wifi_opinion_circle);
            distanceView = (TextView) itemView.findViewById(R.id.distance);

            // swipe functionality views
            regularLayout = (LinearLayout) itemView.findViewById(R.id.regularLayout);
            swipeLayout = (LinearLayout) itemView.findViewById(R.id.swipeLayout);
            undoTextView = (TextView) itemView.findViewById(R.id.undo);
            // listener
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v)
        {
            mOnClickListener.onListItemClick(getIdWifi(getAdapterPosition()));
        }
    }

    /** Returns the value of a database column from its current list position */
    private int getIdWifi(int position)
    {
        if (mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getInt(mCursor.getColumnIndex(WifiEntry._ID));
            }
            else {
                return -1;
            }
        }
        else {
            return -1;
        }
    }


    @Override
    public int getItemCount()
    {
        if (mCursor != null)
            return mCursor.getCount();
        return 0;
    }

    public void swapCursor(Cursor newCursor)
    {
        if (newCursor != null)
        {
            mCursor = newCursor;
            notifyDataSetChanged();
        }
    }

    private int getOpinionColor(double rating)
    {
        int opinionColorResourceId;
        if (rating <= 5.0 && rating > 4.0)
        {
            opinionColorResourceId = R.color.colorOpinion5;
        }
        else if (rating <= 4.0 && rating > 3.0)
        {
            opinionColorResourceId = R.color.colorOpinion4;
        }
        else if (rating <= 3.0 && rating > 2.0)
        {
            opinionColorResourceId = R.color.colorOpinion3;
        }
        else if (rating <= 2.0 && rating > 1.0)
        {
            opinionColorResourceId = R.color.colorOpinion2;
        }
        else if (rating <= 1.0 && rating > 0.0)
        {
            opinionColorResourceId = R.color.colorOpinion1;
        }
        else
        {
            opinionColorResourceId = R.color.colorOpinion0;
        }
        // convert the color resource ID into an actual integer color value
        return ContextCompat.getColor(mContext, opinionColorResourceId);
    }


}

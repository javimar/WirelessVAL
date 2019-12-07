package eu.javimar.wirelessval.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.model.Wifi;
import eu.javimar.wirelessval.utils.GeoPoint;
import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;

@SuppressWarnings("all")
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiViewHolder>
{
    private Context mContext;
    private List<Wifi> wifiList;
    private WifiViewModel mViewModel;

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
    public interface ListItemClickListener
    {
        void onListItemClick(String [] clickWifi);
    }

    public void setWifiList(List<Wifi> wifis)
    {
        wifiList = wifis;
        notifyDataSetChanged();
    }

    /** Adapter constructor */
    public WifiAdapter(ListItemClickListener listener, Context context)
    {
        mContext = context;
        mOnClickListener = listener;
        itemsPendingRemoval = new ArrayList<>();

        mViewModel = new ViewModelProvider((FragmentActivity) context,
                new WifiViewModelFactory(((FragmentActivity) context)
                        .getApplication(), 0,0)).get(WifiViewModel.class);
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
        GeoPoint wifiLocation;
        if(wifiList != null)
        {
            wifiLocation =
                    new GeoPoint(wifiList.get(position).getLongitude(),
                            wifiList.get(position).getLatitude());

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
                holder.nameTextView.setText(wifiList.get(position).getWifiName());
                double opinion = wifiList.get(position).getOpinion();
                holder.opinionTextView.setText(String.valueOf(opinion));

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
                int sectionColor = getOpinionColor(Double.parseDouble(String.valueOf(opinion)));
                // Set the color on circle
                sectionCircle.setColor(sectionColor);
            }
        }
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
        GeoPoint geoPoint = new GeoPoint(wifiList.get(position).getLatitude(),
                wifiList.get(position).getLongitude());
        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

        if (!itemsPendingRemoval.contains(location))
        {
            // there can be only one per run
            itemsPendingRemoval.add(geoPoint);
            // this will redraw row in "undoTextView" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the data
            Runnable pendingRemovalRunnable = new Runnable()
            {
                @Override
                public void run() {
                    remove(geoPoint, position);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(geoPoint, pendingRemovalRunnable);
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
        mViewModel.deleteWifi(wifiList.get(position).getWifiName(),
                wifiList.get(position).getLatitude(),
                wifiList.get(position).getLongitude());
        notifyItemRemoved(position);
    }

    public boolean isPendingRemoval(int position)
    {
        return itemsPendingRemoval.contains(new
                GeoPoint(wifiList.get(position).getLatitude(),
                wifiList.get(position).getLongitude()));
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
            mOnClickListener.onListItemClick(getWifi(getAdapterPosition()));
        }
    }

    private String[] getWifi(int position)
    {
        // Wifi is made of name and coordinates to be unique
        String[] wifi = new String[3];
        if (wifiList != null)
        {
            wifi[0] = wifiList.get(position).getWifiName();
            wifi[1] = String.valueOf(wifiList.get(position).getLatitude());
            wifi[2] = String.valueOf(wifiList.get(position).getLongitude());
        }
        return wifi;
    }

    @Override
    public int getItemCount()
    {
        return wifiList == null ? 0 : wifiList.size();
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

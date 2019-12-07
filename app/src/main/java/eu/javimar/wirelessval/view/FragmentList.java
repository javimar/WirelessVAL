package eu.javimar.wirelessval.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.javimar.wirelessval.R;
import eu.javimar.wirelessval.utils.SwipeUtil;
import eu.javimar.wirelessval.viewmodel.WifiViewModel;
import eu.javimar.wirelessval.viewmodel.WifiViewModelFactory;

import static eu.javimar.wirelessval.MainActivity.sCurrentPosition;
import static eu.javimar.wirelessval.MainActivity.sTabletView;


public class FragmentList extends Fragment implements
        WifiAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener
{
    @BindView(R.id.wifiRecyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.empty_view) TextView mEmptyView;
    @BindView(R.id.loading_indicator) ProgressBar mProgressBar;

    /** Save the recycler position on screen orientation and when coming back from DETAIL*/
    private static int lastFirstVisiblePosition;

    private static boolean sPreferencesHaveBeenUpdated = false;

    /** Reference to implementation de OnItemSelectedListener by MainActivity */
    private OnItemSelectedListener mListener;

    /** Adapter and RecyclerView modified for the list of fallas */
    private WifiAdapter mAdapter;
    private WifiViewModel mWifiViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.list_layout, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                LinearLayoutManager.VERTICAL));
        mAdapter = new WifiAdapter(this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mWifiViewModel = new ViewModelProvider(this,
                new WifiViewModelFactory(getActivity().getApplication(),
                sCurrentPosition.getLatitude(), sCurrentPosition.getLongitude())).get(WifiViewModel.class);
        observerSetup();

        // just allow swipe functionality on mobile view
        if(!sTabletView)
            setSwipeForRecyclerView();

        // add support for preferences changes callback
        PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void observerSetup()
    {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        int sortPref = Integer.parseInt(prefs.getString(
                getString(R.string.settings_sort_criteria_key), "0"));
        switch (sortPref)
        {
            case 0: // by name
                mWifiViewModel.getAllWifisByName().observe(getViewLifecycleOwner(), wifis ->
                {
                    mAdapter.setWifiList(wifis);
                    if(wifis== null || wifis.size() <= 0)
                    {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText(R.string.no_wifis_found);
                    }
                    else
                    {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                });
                break;

            case 1: // by distance
                mWifiViewModel.getAllWifisByDistance().observe(getViewLifecycleOwner(), wifis ->
                {
                    mAdapter.setWifiList(wifis);
                    if(wifis== null || wifis.size() <= 0)
                    {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText(R.string.no_wifis_found);
                    }
                    else
                    {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                });
                break;

            case 2: // by opinion
                mWifiViewModel.getAllWifisByOpinion().observe(getViewLifecycleOwner(), wifis ->
                {
                    mAdapter.setWifiList(wifis);
                    if(wifis== null || wifis.size() <= 0)
                    {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mEmptyView.setText(R.string.no_wifis_found);
                    }
                    else
                    {
                        mEmptyView.setVisibility(View.GONE);
                    }
                    mProgressBar.setVisibility(View.GONE);
                });
                break;
        }
    }

    /** Any changes in preferences will trigger this method */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        sPreferencesHaveBeenUpdated = true;
    }

    @Override
    public void onListItemClick(String[] wifiKey)
    {
        mListener.onItemSelected(wifiKey);
    }

    public void resetRecycler()
    {
        observerSetup();
    }

    public void passSearchResults(String query)
    {
        // SQL wildcards %
        mWifiViewModel.getSearchResults("%" + query + "%");

        mWifiViewModel.getSearchQueryResults().observe(getViewLifecycleOwner(), wifis ->
        {
            mAdapter.setWifiList(wifis);
            if(wifis == null || wifis.size() <= 0)
            {
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_wifis_found);
            }
            else
            {
                mEmptyView.setVisibility(View.GONE);
            }
        });
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
                if (adapter.isPendingRemoval(position))
                {
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
        swipeHelper.setLeftColorCode(ContextCompat.getColor(getActivity(), R.color.colorOpinion1));
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
        if (sPreferencesHaveBeenUpdated)
        {
            // reload to reflect new preferences
            observerSetup();
            sPreferencesHaveBeenUpdated = false;
        }
        mRecyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
    }

    /** MainActivity must implement this interface */
    public interface OnItemSelectedListener
    {
        void onItemSelected(String[] wifi);
    }
    public void setItemListener(OnItemSelectedListener listener)
    {
        this.mListener = listener;
    }
}

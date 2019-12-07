package eu.javimar.wirelessval.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import eu.javimar.wirelessval.R;

/** DetailActivity is never used on a tablet. It is simply a container to present FragmentDetail,
 *  so is only used on handset devices when the two fragments are displayed separately.
 */
public class DetailActivity extends AppCompatActivity
{
    private FragmentDetail fragmentDetail;
    private static final String FRAGMENT_TAG = "fragment_tag";
    private String[] mWifiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        // want to lock orientation in tablets to landscape only :-/
        if(getResources().getBoolean(R.bool.land_only))
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mWifiKey = getIntent().getStringArrayExtra("wifiKey");

        if(savedInstanceState != null)
        {
            fragmentDetail = (FragmentDetail)
                    getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }

        if(fragmentDetail == null)
        {
            fragmentDetail = new FragmentDetail();
        }
        displayFragment();
    }

    private void displayFragment()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragmentDetail.isAdded()) // if fragment is already in container
        {
            ft.show(fragmentDetail);
        }
        else
        {
            ft.add(R.id.frame_detail_phone, fragmentDetail, FRAGMENT_TAG);
        }

        // commit changes
        ft.commit();
        getSupportFragmentManager().executePendingTransactions();
        fragmentDetail.displayFragmentDetail1(mWifiKey);
    }
}

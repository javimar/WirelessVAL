package eu.javimar.wirelessval.view;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import eu.javimar.wirelessval.R;

/** DetailActivity is never used on a tablet. It is simply a container to present FragmentDetail,
 *  so is only used on handset devices when the two fragments are displayed separately.
 */
public class DetailActivity extends AppCompatActivity
{
    FragmentDetail mFragmentDetail;

    /** Save the fragment contents on screen orientation */
    private final String FRAGMENT_STATE = "fragment_state";

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

        Uri wifi = getIntent().getData();
        if (wifi != null)
        {
            if (savedInstanceState != null)
            {
                mFragmentDetail = (FragmentDetail)
                        getFragmentManager().getFragment(savedInstanceState, FRAGMENT_STATE);
                mFragmentDetail.displayFragmentDetail(wifi);
            }
            else
            {
                // Take the info from the intent and deliver it to the fragment so it can update
                mFragmentDetail = (FragmentDetail)
                        getFragmentManager().findFragmentById(R.id.fragment_detail);

                mFragmentDetail.displayFragmentDetail(wifi);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getFragmentManager().putFragment(outState, FRAGMENT_STATE, mFragmentDetail );
    }

}

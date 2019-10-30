package eu.javimar.wirelessval;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import eu.javimar.wirelessval.view.MySettingsFragment;


public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment, new MySettingsFragment())
                .commit();
    }
}

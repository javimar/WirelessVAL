package eu.javimar.wirelessval.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import eu.javimar.wirelessval.R;

public class MySettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.settings, rootKey);

        // update the preference summary when the settings activity is launched.
        // Given the key of a preference, we use findPreference to get the Preference object,
        // and setup the preference using a helper method called bindPreferenceSummaryToValue().
        // this is for preference SORT
        Preference sortType = findPreference(getString(R.string.settings_sort_criteria_key));
        bindPreferenceSummaryToValue(sortType);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        String stringValue = newValue.toString();

        // For "ListPrefences" we need to show the label, instead of the key (0, 1)
        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0)
            {
                CharSequence[] labels = listPreference.getEntries();
                preference.setSummary(labels[prefIndex]);
            }
        }
        else
        {
            preference.setSummary(stringValue);
        }
        return true;
    }

    /**
     * Set the current PreferenceFragment instance as the listener on each
     * preference. We also read the current value of the preference stored in the
     * SharedPreferences on the device, and display that in the preference summary
     * (so that the user can see the current value of the preference).
     */
    private void bindPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(preference.getContext());
        String preferenceString = preferences.getString(preference.getKey(), "");
        onPreferenceChange(preference, preferenceString);
    }
}

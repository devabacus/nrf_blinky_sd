package no.nordicsemi.android.blinky.preferences;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import no.nordicsemi.android.blinky.R;


public class SetPrefActivity extends AppCompatActivity {

    private static final String TAG = "SetPrefActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pref);



        Fragment fragment = new SettingsFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            //created for the first time
            fragmentTransaction.add(R.id.sett_frame, fragment, "settings_fragment");
            fragmentTransaction.commit();
        } else {
            fragment = getFragmentManager().findFragmentByTag("settings_fragment");
        }
    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static final String KEY_LIST_NUM_BUTTONS = "num_buttons";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            Preference curPref = findPreference(KEY_LIST_NUM_BUTTONS);
            String numButts = sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8");
            curPref.setSummary(numButts);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference curPref = findPreference(key);
            curPref.setSummary(sharedPreferences.getString(key, "8"));
        }
    }
}





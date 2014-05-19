package com.org.openwifi.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.org.openwifi.openwifi.R;

/**
 * Created by jack gurulian
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

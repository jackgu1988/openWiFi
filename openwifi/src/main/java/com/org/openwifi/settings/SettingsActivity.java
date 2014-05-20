package com.org.openwifi.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.org.openwifi.openwifi.R;

/**
 * Created by jack gurulian
 */
public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.app_name) + ": "
                + getString(R.string.settings));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }
}

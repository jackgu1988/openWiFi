package com.org.openwifi.openwifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class Main extends Activity {

    private CheckBox understand;
    private AlertDialog acceptD;
    private boolean firstrun;

    private List<ScanResult> results;
    private WifiManager wifi;
    private WifiListAdapter adapter;
    private ListView wifiList;
    private ArrayList<String> wifiNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            showDisclaimer();
        }

        scanWifi();
    }

    public void refresh(View v) {
        scanWifi();
    }

    private void scanWifi() {

        wifiList = (ListView) findViewById(R.id.wifiList);

        wifi = (WifiManager) getSystemService(this.WIFI_SERVICE);

        if (!wifi.isWifiEnabled())
            Toast.makeText(getApplicationContext(), getString(R.string.wifi_off), Toast.LENGTH_LONG).show();
        else
            scan();
    }

    private void scan() {
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();

        adapter = new WifiListAdapter(this, R.id.results, wifiNames);
        wifiList.setAdapter(adapter);

        wifi.startScan();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                getWifiNames();
                adapter.notifyDataSetChanged();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void getWifiNames() {
        wifiNames.clear();

        for (int i = 0; i < results.size(); i++) {
            wifiNames.add(results.get(i).SSID);
        }
    }

    private void showDisclaimer() {
        AlertDialog.Builder disclaimer = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DiscDialog));

        LayoutInflater accept = LayoutInflater.from(this);
        View acceptLayout = accept.inflate(R.layout.checkbox, null);
        assert acceptLayout != null;
        understand = (CheckBox) acceptLayout.findViewById(R.id.understand);

        disclaimer
                .setView(acceptLayout)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.disclaimer))
                .setMessage(Html.fromHtml(getString(R.string.disc_msg)))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .edit()
                                .putBoolean("firstrun", false)
                                .commit();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        acceptD = disclaimer.create();
        acceptD.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                acceptD.getButton(BUTTON_POSITIVE).setEnabled(false);
            }
        });
        acceptD.setCancelable(false);

        acceptD.show();
    }

    public void enableOK(View v) {
        if (!understand.isChecked())
            acceptD.getButton(BUTTON_POSITIVE).setEnabled(false);
        else
            acceptD.getButton(BUTTON_POSITIVE).setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

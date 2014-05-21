/**
 * Copyright (C) 2014 jackgu1988 and vladei
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.org.openwifi.openwifi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.org.openwifi.about.AboutDialogue;
import com.org.openwifi.settings.SettingsActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity extends ActionBarActivity {

    private CheckBox understand;
    private AlertDialog acceptD;
    private List<ScanResult> results;
    private WifiManager wifi;
    private WifiListAdapter adapter;
    private ListView wifiList;
    private ArrayList<String> wifiNames = new ArrayList<String>();
    private ArrayList<String> macNames = new ArrayList<String>();
    private ArrayList<String> wifiSec = new ArrayList<String>();
    private Connector connector;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOverflowMenu();

        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun",
                true);
        if (firstrun) {
            showDisclaimer();
        }

        wifiList = (ListView) findViewById(R.id.wifiList);

        wifi = (WifiManager) getSystemService(WIFI_SERVICE);

        wifiSelect();
        scanWifi();
    }

    private void wifiSelect() {

        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                connectionDialog(position);
            }
        });

        wifiList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(250);
                if (connector.checkIfExists(wifiNames.get(position)))
                    networkLongPress(position);
                else
                    connectionDialog(position);

                return true;
            }
        });
    }

    private void networkLongPress(int pos) {
        LongPressDialogue alert = new LongPressDialogue(this, wifiNames.get(pos), connector,
                results, pos);
        alert.build();
        alert.showAlert();
    }

    private void connectionDialog(int pos) {
        ConnectionDialogue alert = new ConnectionDialogue(this, wifiNames.get(pos), connector,
                results, pos);
        alert.build();
        alert.showAlert();
    }

    public void refresh(View v) {
        scanWifi();
    }

    private void scanWifi() {

        WifiInfo wifiInfo = wifi.getConnectionInfo();

        adapter = new WifiListAdapter(this, R.id.withText, wifiNames, macNames, wifiSec,
                wifiInfo.getSSID(), wifiInfo.getBSSID());
        wifiList.setAdapter(adapter);

        connector = new Connector(this);

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), getString(R.string.wifi_off),
                    Toast.LENGTH_LONG).show();
            noWifi();
            wifiList.setEnabled(false);
        } else
            scan();
    }

    private void scan() {
        Toast.makeText(this, getString(R.string.scanning), Toast.LENGTH_SHORT).show();

        wifi.startScan();

        registerReceiver(receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();

                getWifiNames();
                adapter.notifyDataSetChanged();
                wifiList.setEnabled(true);
                if (results.size() == 0) {
                    noWifi();
                    wifiList.setEnabled(false);
                }
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void noWifi() {
        wifiNames.clear();
        macNames.clear();
        wifiSec.clear();

        wifiNames.add(getString(R.string.no_networks));
        macNames.add("");
        wifiSec.add("");
        adapter.notifyDataSetChanged();
    }

    private void getWifiNames() {
        wifiNames.clear();
        macNames.clear();
        wifiSec.clear();

        for (ScanResult result : results) {
            wifiNames.add(result.SSID);
            macNames.add(result.BSSID);
            wifiSec.add(getSecurityType(result.capabilities));
        }
    }

    private String getSecurityType(String sec) {
        if (sec.contains("WPA2"))
            return "WPA2";
        else if (sec.contains("WPA"))
            return "WPA";
        else if (sec.contains("WEP"))
            return "WEP";
        else
            return "Open";
    }

    @SuppressLint("InflateParams")
    private void showDisclaimer() {
        AlertDialog.Builder disclaimer = new AlertDialog.Builder(new ContextThemeWrapper(this,
                R.style.DiscDialog));

        LayoutInflater accept = LayoutInflater.from(this);
        View acceptLayout = accept.inflate(R.layout.checkbox, null);
        assert acceptLayout != null;
        understand = (CheckBox) acceptLayout.findViewById(R.id.understand);

        disclaimer
                .setView(acceptLayout)
                .setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.disclaimer))
                .setMessage(
                        Html.fromHtml(getString(R.string.disc_msg)))
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

        MenuItem switchItem = menu.findItem(R.id.wifi_on_off);
        Switch onOffSwitch = (Switch) switchItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        } else if (id == R.id.action_about) {
            new AboutDialogue(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ignored) {

        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

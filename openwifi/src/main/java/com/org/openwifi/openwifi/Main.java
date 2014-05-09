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
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private ArrayList<String> macNames = new ArrayList<String>();
    private ArrayList<String> wifiSec = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun) {
            showDisclaimer();
        }

        wifiList = (ListView) findViewById(R.id.wifiList);

        wifi = (WifiManager) getSystemService(this.WIFI_SERVICE);

        adapter = new WifiListAdapter(this, R.id.withText, wifiNames, macNames, wifiSec);
        wifiList.setAdapter(adapter);

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
    }

    private void connectionDialog(int pos) {

        String security = results.get(pos).capabilities;

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTextColor(0xFF000000);

        final CheckBox showPass = new CheckBox(this);
        showPass.setSelected(false);
        showPass.setText(getString(R.string.show_pass));
        showPass.callOnClick();
        showPass.setTextColor(0xFF000000);

        LinearLayout boxFields = new LinearLayout(this);
        boxFields.setOrientation(LinearLayout.VERTICAL);
        boxFields.addView(input);
        boxFields.addView(showPass);

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DiscDialog));
        alert.setTitle(wifiNames.get(pos))
                .setMessage(getString(R.string.enter_pass) + " (" + getSecurityType(security) + "):")
                .setView(boxFields)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String pass = input.getText().toString();
                        return;
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .show();
    }

    private String getSecurityType(String sec) {
        if (sec.contains("WPA"))
            return "WPA/WPA2";
        else if (sec.contains("WEP"))
            return "WEP";
        else
            return "Open";
    }

    public void refresh() {
        scanWifi();
    }

    private void scanWifi() {

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), getString(R.string.wifi_off), Toast.LENGTH_LONG).show();
            noWifi();
        } else
            scan();
    }

    private void scan() {
        Toast.makeText(this, getString(R.string.scanning), Toast.LENGTH_SHORT).show();

        wifi.startScan();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifi.getScanResults();
                getWifiNames();
                adapter.notifyDataSetChanged();
                if (results.size() == 0)
                    noWifi();
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

        for (int i = 0; i < results.size(); i++) {
            wifiNames.add(results.get(i).SSID);
            macNames.add(results.get(i).BSSID);
            wifiSec.add(getSecurityType(results.get(i).capabilities));
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
        } else if (id == R.id.action_refresh) {
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

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
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jack gurulian
 */
public class WifiListAdapter extends ArrayAdapter<String> {

    private Context context;

    private ArrayList<String> ssid;
    private ArrayList<String> bssid;
    private ArrayList<String> sec;

    private String currentSSID;

    public WifiListAdapter(Context context, int textViewResourceId,
                           ArrayList<String> ssid, ArrayList<String> bssid, ArrayList<String> sec,
                           String currentSSID) {
        super(context, textViewResourceId, ssid);

        ssid.clear();
        bssid.clear();

        this.ssid = ssid;
        this.bssid = bssid;
        this.sec = sec;
        this.currentSSID = currentSSID;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String currentSsid = ssid.get(position);
        String currentBssid = bssid.get(position);
        String currentSec = sec.get(position);

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.wifi_adapter_layout, null);
        }
        TextView ssidText = (TextView) v.findViewById(R.id.ssid);
        TextView bssidText = (TextView) v.findViewById(R.id.bssid);

        // clear
        ssidText.setText("");
        bssidText.setText("");

        ssidText.setText(currentSsid);
        if (!currentBssid.equals(""))
            bssidText.setText(currentSec + " / BSSID: " + currentBssid);

        if (currentSsid != null && currentSSID != null
                && currentSSID.equals(currentSsid) && currentBssid.equals(currentBssid)) {
            ssidText.setTextColor(0xFF0000FF);
            bssidText.setTextColor(0xFF0000FF);
        }
        return v;
    }
}

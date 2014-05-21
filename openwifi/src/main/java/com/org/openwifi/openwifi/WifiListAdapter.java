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
import android.text.Html;
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
    private String currentBSSID;

    public WifiListAdapter(Context context, int textViewResourceId,
                           ArrayList<String> ssid, ArrayList<String> bssid, ArrayList<String> sec,
                           String currentSSID, String currentBSSID) {
        super(context, textViewResourceId, ssid);

        this.context = context;

        ssid.clear();
        bssid.clear();

        this.ssid = ssid;
        this.bssid = bssid;
        this.sec = sec;
        this.currentSSID = currentSSID;
        this.currentBSSID = currentBSSID;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String currentSsid = ssid.get(position);
        String currentBssid = bssid.get(position);
        String currentSec = sec.get(position);

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.wifi_adapter_layout, null);
        }
        TextView ssidText = (TextView) v.findViewById(R.id.ssid);
        TextView bssidText = (TextView) v.findViewById(R.id.bssid);

        // clear
        ssidText.setText("");
        bssidText.setText("");

        ssidText.setText(currentSsid);
        if (!currentBssid.equals(""))
            if (currentSec.equals("WPA") || currentSec.equals("WPA2") || currentSec.equals("WEP"))
                bssidText.setText(Html.fromHtml(context.getString(R.string.secured)
                        + " <i>(" + currentSec + ")</i>"));
            else if (currentSec.equals("Open"))
                bssidText.setText(context.getString(R.string.open));

        if (currentSsid != null && currentSSID != null && currentSSID.equals(currentSsid) &&
                currentBSSID.equals(currentBssid)) {
            ssidText.setTextColor(context.getResources().getColor(R.color.holo_blue));
            bssidText.setText(context.getString(R.string.connected));
        } else
            ssidText.setTextColor(context.getResources().getColor(R.color.black));
        return v;
    }
}

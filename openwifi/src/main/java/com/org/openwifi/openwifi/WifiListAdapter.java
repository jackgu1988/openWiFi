package com.org.openwifi.openwifi;

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
public class WifiListAdapter extends ArrayAdapter {

    private ArrayList<String> ssid;
    private ArrayList<String> bssid;

    public WifiListAdapter(Context context, int textViewResourceId,
                           ArrayList<String> ssid, ArrayList<String> bssid) {
        super(context, textViewResourceId, ssid);

        ssid.clear();
        bssid.clear();

        this.ssid = ssid;
        this.bssid = bssid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String currentSsid = ssid.get(position);
        String currentBssid = bssid.get(position);

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
            bssidText.setText("BSSID: " + currentBssid);
        return v;
    }
}

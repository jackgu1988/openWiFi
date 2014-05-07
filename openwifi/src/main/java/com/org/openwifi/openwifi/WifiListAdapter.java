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

    private ArrayList<String> items;

    public WifiListAdapter(Context context, int textViewResourceId,
                           ArrayList<String> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        String current = items.get(position);

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.wifi_adapter_layout, null);
        }
        TextView ssid = (TextView) v.findViewById(R.id.ssid);
        ssid.setText(current);
        return v;
    }
}

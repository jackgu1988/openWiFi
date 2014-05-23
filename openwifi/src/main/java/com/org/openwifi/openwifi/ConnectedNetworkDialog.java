package com.org.openwifi.openwifi;

import android.app.AlertDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.org.openwifi.interfaces.IDialogue;

import java.util.ArrayList;

/**
 * Created by jack gurulian
 */
public class ConnectedNetworkDialog extends Dialogue implements IDialogue {

    private Context context;
    private int ipAddress = 0;
    private String name;
    private AlertDialog alertConnect;

    protected ConnectedNetworkDialog(Context context, WifiManager wifimgr, String name) {
        super(context);

        this.context = context;
        this.name = name;

        WifiInfo wifiInfo = wifimgr.getConnectionInfo();
        ipAddress = wifiInfo.getIpAddress();
    }

    public int getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(int ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void build() {
        TextView optionAdapter = new TextView(context);
        optionAdapter.setText("test");

        ScrollView optionList = new ScrollView(context);
        optionList.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        optionList.addView(optionAdapter);
        optionList.addView(optionAdapter);

        LinearLayout options = new LinearLayout(context);
        options.addView(optionList);

        this.createDialogue(name, null, options, 0);

        alertConnect = this.createAlert();
    }

    private ArrayList<String> buildOptions() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(context.getString(R.string.forget));
        options.add(context.getString(R.string.modify));

        return options;
    }

    @Override
    public void showAlert() {

    }

    @Override
    public void hideAlert() {

    }

    @Override
    public void dismissAlert() {

    }
}

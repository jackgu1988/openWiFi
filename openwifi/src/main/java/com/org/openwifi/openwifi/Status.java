package com.org.openwifi.openwifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by jack gurulian
 */
public class Status {

    private Context context;

    private LinearLayout linLayout;
    private TextView status;
    private ProgressBar progBar;
    private boolean wifiOn;

    public Status(Context context, LinearLayout linLayout, TextView status, ProgressBar progBar) {

        this.context = context;

        this.linLayout = linLayout;
        this.status = status;
        this.progBar = progBar;

        statusChangeAdapter();
    }

    public boolean isWifiOn() {
        return wifiOn;
    }

    public void setWifiOn(boolean wifiOn) {
        this.wifiOn = wifiOn;
    }

    public void hide() {
        linLayout.setVisibility(View.GONE);
    }

    public void show() {
        linLayout.setVisibility(View.VISIBLE);
    }

    private void statusChangeAdapter() {
        BroadcastReceiver connectionStateReceiver;
        context.registerReceiver(connectionStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                SupplicantState supState;
                WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifi.getConnectionInfo();
                supState = wifiInfo.getSupplicantState();

                if (wifiOn)
                    show();
                else
                    hide();

                switch (supState) {
                    case ASSOCIATED:
                        status.setText(context.getString(R.string.connected));
                        progBar.setVisibility(View.GONE);
                        break;
                    case ASSOCIATING:
                        status.setText(context.getString(R.string.connecting));
                        progBar.setVisibility(View.VISIBLE);
                        break;
                    case COMPLETED:
                        hide();
                        break;
                    case DISCONNECTED:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.GONE);
                        break;
                    case DORMANT:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.GONE);
                        break;
                    case FOUR_WAY_HANDSHAKE:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.VISIBLE);
                        break;
                    case GROUP_HANDSHAKE:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.VISIBLE);
                        break;
                    case INACTIVE:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.GONE);
                        break;
                    case INVALID:
                        status.setText(context.getString(R.string.error));
                        progBar.setVisibility(View.GONE);
                        break;
                    case UNINITIALIZED:
                        status.setText(context.getString(R.string.authenticating));
                        progBar.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        }, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

    public void scanning() {
        show();
        status.setText(context.getResources().getString(R.string.scanning));
        progBar.setVisibility(View.VISIBLE);
    }
}

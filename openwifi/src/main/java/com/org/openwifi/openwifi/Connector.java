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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by jack gurulian
 */
public class Connector {

    private Context context;
    private WifiConfiguration wifi;
    private WifiManager mgr;

    /**
     * @param context
     */
    public Connector(Context context) {
        this.context = context;
    }

    /**
     * Connect to AP
     *
     * @param ssid the ssid of the AP
     * @param pass the pass of the AP
     * @param type 0: open, 1: wep, 2: wpa
     * @return true if succeed
     */
    public boolean connectToAp(String ssid, String pass, int type) {
        wifi = new WifiConfiguration();
        wifi.SSID = "\"" + ssid + "\"";

        switch (type) {
            case 0:
                open();
                break;
            case 1:
                wep(pass);
                break;
            case 2:
                wpa(pass, 2);
                break;
            case 3:
                wpa(pass, 3);
                break;
            default:
                return false;
        }

        mgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mgr.saveConfiguration();
        mgr.addNetwork(wifi);

        List<WifiConfiguration> list = mgr.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                mgr.disconnect();
                mgr.enableNetwork(i.networkId, true);
                connectToNetwork();
            }
        }

        return true;
    }

    private void open() {
        wifi.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }

    private void wep(String pass) {
        wifi.wepKeys[0] = "\"" + pass + "\"";
        wifi.wepTxKeyIndex = 0;
        wifi.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifi.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
    }

    private void wpa(String pass, int type) {
        wifi.preSharedKey = "\"" + pass + "\"";
        wifi.hiddenSSID = true;
        wifi.status = WifiConfiguration.Status.ENABLED;
        wifi.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifi.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifi.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifi.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifi.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        if (type == 3)
            wifi.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        else
            wifi.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
    }

    private void connectToNetwork() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getNetworkInfo(0);

        new Thread() {
            @Override
            public void run() {

                while (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    try {

                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mgr.reconnect();
            }
        }.start();
    }

    /**
     * Checks if the target network has already been configured
     *
     * @param SSID the SSID of the target network
     * @return true if it has already been configured
     */
    public boolean checkIfExists(String SSID) {
        mgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = mgr.getConfiguredNetworks();
        for (WifiConfiguration i : list)
            if (i.SSID.equals("\"" + SSID + "\""))
                return true;
        return false;
    }

    /**
     * @param SSID
     * @return
     */
    public boolean forgetNetwork(String SSID) {
        return true;
    }
}

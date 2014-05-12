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
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by jack gurulian
 */
public class Connector {

    private Context context;
    private WifiConfiguration wifi;

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
                wpa(pass);
                break;
            default:
                return false;
        }

        WifiManager mgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mgr.addNetwork(wifi);

        List<WifiConfiguration> list = mgr.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                mgr.disconnect();
                mgr.enableNetwork(i.networkId, true);
                mgr.reconnect();

                break;
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

    private void wpa(String pass) {
        wifi.preSharedKey = "\"" + pass + "\"";
    }
}

package com.example.wifip2ptest.wifip2p;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;

import java.util.Collection;

public interface WifiP2PListener {
    void onWifiP2pEnabled(boolean enabled);
    void onCreateGroup(boolean isSuccess);
    void onCreateGroup(boolean isSuccess, int reason);
    void onRemoveGroup(boolean isSuccess);
    void onDiscoverPeers(boolean isSuccess);
    void onConnectionChanged(boolean connected);
    void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList);
    void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo);
    void onGroupInfoAvailable(WifiP2pGroup wifiP2pgroup);
}

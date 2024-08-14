package com.example.wifip2ptest.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

public class P2PBroadCastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private String TAG = "[WifiP2p Demo App] P2PBroadCastReceiver";
    private WifiP2PListener mWifiP2PListener;

    public P2PBroadCastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel){
        this.manager = manager;
        this.channel = channel;
    }
    public void setListener(WifiP2PListener wifiP2PListener) {
        this.mWifiP2PListener = wifiP2PListener;
    }
    // 注册广播
    public void registerReceiver(Context context){
        Log.d(TAG, "registerReceiver");
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        context.registerReceiver(this, intentFilter);
    }
    // 取消注册广播
    public void unregisterReceiver(Context context){
        Log.d(TAG, "unregisterReceiver");
        context.unregisterReceiver(this);
    }
    // 广播接收器
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (context == null ||
                intent == null ||
                TextUtils.isEmpty(intent.getAction())
                || manager == null) {
            Log.e(TAG, "context or intent is null");
            return;
        }

        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION！！！");
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(TAG, "Wi-Fi P2P is enabled");
                mWifiP2PListener.onWifiP2pEnabled(true);
            } else {
                Log.e(TAG, "Wi-Fi P2P is not enabled");
                mWifiP2PListener.onWifiP2pEnabled(false);
            }

        }else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION！！！");
            WifiHelper.getInstance(context).requstpeers();

        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION！！！");
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.isConnected() && manager != null){
                Log.d(TAG, "networkInfo is not null");
                WifiHelper.getInstance(context).requestConnectInfo();
            } else {
                Log.e(TAG, "networkInfo is null");
            }

        }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
            Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION！！！");

            WifiP2pDevice wifip2pDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            if (wifip2pDevice != null){
                Log.d(TAG, "wifip2pDevice : " + wifip2pDevice);
                Log.d(TAG, "wifip2pDevice deviceName : " + wifip2pDevice.deviceName);
                Log.d(TAG, "wifip2pDevice status : " + wifip2pDevice.status);
                Log.d(TAG, "wifip2pDevice deviceAddress : " + wifip2pDevice.deviceAddress);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                manager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
                    @Override
                    public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
                        if(wifiP2pDevice != null){
                            Log.d(TAG, "WifiP2pDevice : " + wifiP2pDevice);
                            Log.d(TAG, "WifiP2pDevice deviceName : " + wifiP2pDevice.deviceName);
                            Log.d(TAG, "WifiP2pDevice status : " + wifiP2pDevice.status);
                            Log.d(TAG, "WifiP2pDevice deviceAddress : " + wifiP2pDevice.deviceAddress);
                            Log.d(TAG, "WifiP2pDevice primaryDeviceType : " + wifiP2pDevice.primaryDeviceType);
                        }
                    }
                });
            }
        }
    }
}

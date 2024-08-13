package com.example.wifip2ptest.wifip2p;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WifiHelper {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private P2PBroadCastReceiver receiver;
    /**
     * 单例模式实现的WifiHelper类的实例变量。
     * 使用volatile关键字确保多线程环境下的可见性和一致性。
     */
    private static volatile WifiHelper instance;

    private Context mContext;
    private WifiP2pInfo mWifiP2pInfo;

    private List<WifiP2pDevice> mDeviceList = new ArrayList<>();

    private String TAG = "[WifiP2p Demo App] WifiHelper";
    private WifiP2PListener mWifiP2PListener;

    /**
     * 获取WifiHelper的单例对象。
     * 通过此方法确保整个应用中只存在一个WifiHelper的实例，便于统一管理WiFi相关的操作。
     * 使用synchronized关键字保证线程安全，在多线程环境下也能正确获取到单例对象。
     *
     * @param context 上下文对象，用于WifiHelper初始化时的必要参数，通常为Activity或Application的Context。
     * @return WifiHelper的单例对象。
     */
    public synchronized static WifiHelper getInstance(Context context){
        // 检查是否已经存在实例，如果不存在则创建新的实例
        if(instance == null){
            instance = new WifiHelper(context);
        }
        // 返回已存在的或新创建的实例
        return instance;
    }

    /**
     * WifiHelper的构造函数。
     * 初始化WifiP2P相关的管理器和通道，并注册广播接收器，为进行Wifi直连通信做好准备。
     *
     * @param context 上下文对象，用于获取系统服务和进行其他初始化操作。
     */
    public WifiHelper(@NonNull Context context) {
        Log.d(TAG,"WifiHelper onCreate");
        // 获取应用程序上下文，以避免内存泄漏。
        mContext = context.getApplicationContext();
        // 获取WifiP2P管理器实例。
        manager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);
        // 初始化WifiP2P通道。
        channel = manager.initialize(mContext, mContext.getMainLooper(), null);
        Log.d(TAG,"manager.initialize : " + channel);
        // 创建并注册P2P广播接收器。
        receiver = new P2PBroadCastReceiver(manager, channel);
    }

    public WifiHelper setListener(WifiP2PListener wifiP2PListener) {
        this.mWifiP2PListener = wifiP2PListener;
        if (receiver != null) {
            receiver.setListener(this.mWifiP2PListener);
        }
        return this;
    }

    /**
     * 创建Wi-Fi Direct组。
     * 通过调用WifiP2pManager的createGroup方法来发起组创建请求。如果组创建成功，将调用mWifiP2PListener的onCreateGroup方法，并传入true作为参数。
     * 如果组创建失败，将根据失败原因打印日志，并调用mWifiP2PListener的onCreateGroup方法，传入false和失败原因代码。
     */
    //创建组
    public void createGroup(){
        Log.d(TAG,"createGroup");
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"createGroup is Success");
                // 如果mWifiP2PListener不为空，则通知组创建成功
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onCreateGroup(true);
                }
            }
            @Override
            public void onFailure(int reason) {
                Log.d(TAG,"createGroup is Failure :" + reason);
                // 根据不同的失败原因打印相应的日志
                if (reason == WifiP2pManager.BUSY){
                    Log.d(TAG,"createGroup is Failure : BUSY");
                }else if (reason == WifiP2pManager.NO_SERVICE_REQUESTS){
                    Log.d(TAG,"createGroup is Failure : NO_SERVICE_REQUESTS");
                }else if (reason == WifiP2pManager.P2P_UNSUPPORTED){
                    Log.d(TAG,"createGroup is Failure : P2P_UNSUPPORTED");
                }
                // 如果mWifiP2PListener不为空，则通知组创建失败及失败原因
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onCreateGroup(false, reason);
                }
            }
        });
    }

    //删除组
    public void removeGroup(){
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"removeGroup is Success");
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onRemoveGroup(true);
                }
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG,"removeGroup is  : " + i);
                if (i == WifiP2pManager.BUSY){
                    Log.e(TAG,"removeGroup is Failure : BUSY");
                }else if (i == WifiP2pManager.NO_SERVICE_REQUESTS){
                    Log.e(TAG,"removeGroup is Failure : NO_SERVICE_REQUESTS");
                }else if (i == WifiP2pManager.P2P_UNSUPPORTED){
                    Log.e(TAG,"removeGroup is Failure : P2P_UNSUPPORTED");
                }
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onRemoveGroup(false);
                }
            }
        });
    }
    //发现设备
    public void discoverPeers(){
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"discoverPeers is Success");
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onDiscoverPeers(true);
                }
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG,"discoverPeers is Failure : " + reason);
                if (reason == WifiP2pManager.BUSY){
                    Log.e(TAG,"discoverPeers is Failure : BUSY");
                }else if (reason == WifiP2pManager.NO_SERVICE_REQUESTS){
                    Log.e(TAG,"discoverPeers is Failure : NO_SERVICE_REQUESTS");
                }else if (reason == WifiP2pManager.P2P_UNSUPPORTED){
                    Log.e(TAG,"discoverPeersis Failure : P2P_UNSUPPORTED");
                }
                if(mWifiP2PListener != null){
                    mWifiP2PListener.onDiscoverPeers(false);
                }
            }
        });
    }
    //连接设备
    public void connect(WifiP2pDevice device){
        if(device == null || TextUtils.isEmpty(device.deviceAddress)){
            return;
        }

        Log.d(TAG,"device : " + device);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        Log.d(TAG, "config.deviceAddress : " + config.deviceAddress);
        config.wps.setup = WpsInfo.PBC;
        Log.d(TAG,"config.wps.setup : " + config.wps.setup);
        config.groupOwnerIntent = 0;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"connect is Success");
                if (mWifiP2PListener != null){
                    mWifiP2PListener.onConnectionChanged(true);
                }
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG,"connect is Failure : " + i);
                if (i == WifiP2pManager.BUSY){
                    Log.e(TAG,"connect is Failure : BUSY");
                }else if (i == WifiP2pManager.NO_SERVICE_REQUESTS){
                    Log.e(TAG,"connect is Failure : NO_SERVICE_REQUESTS");
                }else if (i == WifiP2pManager.P2P_UNSUPPORTED){
                    Log.e(TAG,"connect is Failure : P2P_UNSUPPORTED");
                }
                if (mWifiP2PListener != null){
                    mWifiP2PListener.onConnectionChanged(false);
                }
            }
        });
    }

    //取消连接
    public void cancelConnect(){
        Log.d(TAG, "cancelConnect channel : " + channel);
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG,"cancelConnect is Success");
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG,"cancelConnect is Failure : " + i);
            }
        });
    }

    //请求查找设备
    public void requstpeers(){
        manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                Log.d(TAG,"requstpeers onPeersAvailable！！！");
                if (wifiP2pDeviceList != null){
                    Log.d(TAG,"wifiP2pDeviceList.getDeviceList() : " + wifiP2pDeviceList.getDeviceList() );
                    Log.d(TAG," wifiP2pDeviceList.getDeviceList().size() : " + wifiP2pDeviceList.getDeviceList().size());
                    List<WifiP2pDevice> mDeviceList = new ArrayList<>(wifiP2pDeviceList.getDeviceList());
                    mWifiP2PListener.onPeersAvailable(mDeviceList);
                    setDeviceList(mDeviceList);
                } else {
                    Log.e(TAG,"wifiP2pDeviceList is null");
                }
            }
        });
    }

    //请求组信息
    public void requestGroupInfo(){
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                Log.d(TAG,"requestGroupInfo onGroupInfoAvailable！！！");
                if (wifiP2pGroup != null){
                    Log.d(TAG,"WifiP2pGroup : " + wifiP2pGroup);
                    WifiP2pDevice devices = wifiP2pGroup.getOwner();
                    if (devices != null){
                        Log.d(TAG,"devices : " + devices.deviceAddress);
                    }
                    mWifiP2PListener.onGroupInfoAvailable(wifiP2pGroup);
                } else {
                    Log.e(TAG,"WifiP2pGroup is null");
                }
            }
        });
    }

    //请求连接信息
    public void requestConnectInfo(){
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                if (wifiP2pInfo != null){
                    mWifiP2PListener.onConnectionInfoAvailable(wifiP2pInfo);
                }
            }
        });
    }

    //调用注册广播
    public void registerReceiver() {
        if (receiver != null){
            Log.d(TAG,"WifiHelper registerReceiver");
            receiver.registerReceiver(mContext);
        } else{
            Log.e(TAG,"WifiHelper registerReceiver is null");
        }
    }
    //调用注销广播
    public void unregisterReceiver() {
        if (receiver != null){
            Log.d(TAG,"WifiHelper unregisterReceiver");
            receiver.unregisterReceiver(mContext);
        } else {
            Log.e(TAG,"WifiHelper unregisterReceiver is null");
        }
    }

    public void requestDeviceInfo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            manager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
                @Override
                public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
                    Log.d(TAG, "onDeviceInfoAvailable: " + wifiP2pDevice);
                }
            });
        }
        WpsInfo wpsInfo = new WpsInfo();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String s = wpsInfo.BSSID;
            Log.d(TAG, "wpsInfo.BSSID: " + s);
        }
    }

    //获取设备状态
    public String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "可用的";
            case WifiP2pDevice.INVITED:
                return "邀请中";
            case WifiP2pDevice.CONNECTED:
                return "已连接";
            case WifiP2pDevice.FAILED:
                return "失败的";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用的";
            default:
                return "未知";
        }
    }

    //获取设备状态
    public String getBtnStatus(int btnStatus) {
        switch (btnStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "连接";
            case WifiP2pDevice.INVITED:
                return "取消";
            case WifiP2pDevice.CONNECTED:
                return "断开";
            case WifiP2pDevice.FAILED:
                return "连接";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用";
            default:
                return "未知";
        }
    }

    public void setDeviceList(List<WifiP2pDevice> devicelist){
        mDeviceList = devicelist;
    }

    public List<WifiP2pDevice> getmDeviceList() {
        return mDeviceList;
    }
}

package com.example.wifip2ptest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifip2ptest.wifip2p.WifiHelper;
import com.example.wifip2ptest.wifip2p.WifiP2PListener;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements WifiP2PListener {
    private static final int REQUEST_CODE_WIFIP2P_PERMISSIONS = 2001;
    private boolean isWifiP2pPermissions = false;

    private RecyclerView mRecyclerView;
    private DeviceAdapter mDeviceAdapter;
    private Button mBtnHandle;
    private Button mBtnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_devicelist);
        mDeviceAdapter = new DeviceAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mDeviceAdapter);

        mBtnHandle = findViewById(R.id.btn_wifip2p_handle);
        mBtnHandle.setOnClickListener(v -> {
            if (mBtnHandle.getText().toString().equals("开启WifiP2P")) {
                if(isWifiP2pPermissions){
                    WifiHelper.getInstance(this).registerReceiver();
                    WifiHelper.getInstance(this).discoverPeers();
                    WifiHelper.getInstance(this).requstpeers();
                    mBtnHandle.setText("WifiP2P已开启");
                }
            }
        });

        mBtnRequest = findViewById(R.id.btn_request_device);
        mBtnRequest.setOnClickListener(v -> {
            mDeviceAdapter.setDeviceList(WifiHelper.getInstance(this).getmDeviceList());
        });

        requestWifiP2pPermissions();
        WifiHelper.getInstance(this).setListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWifiP2pEnabled(boolean enabled) {

    }

    @Override
    public void onCreateGroup(boolean isSuccess) {

    }

    @Override
    public void onCreateGroup(boolean isSuccess, int reason) {

    }

    @Override
    public void onRemoveGroup(boolean isSuccess) {

    }

    @Override
    public void onDiscoverPeers(boolean isSuccess) {

    }

    @Override
    public void onConnectionChanged(boolean connected) {

    }

    @Override
    public void onPeersAvailable(Collection<WifiP2pDevice> wifiP2pDeviceList) {
        mDeviceAdapter.setDeviceList(wifiP2pDeviceList);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

    }

    @Override
    public void onGroupInfoAvailable(WifiP2pGroup wifiP2pgroup) {

    }

    private void requestWifiP2pPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_WIFIP2P_PERMISSIONS
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WIFIP2P_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                isWifiP2pPermissions = true;
            }
        }
    }
}
package com.example.wifip2ptest;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifip2ptest.wifip2p.WifiHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{
    private List<WifiP2pDevice> mDeviceList = new ArrayList<>();
    private Context mContext;

    public DeviceAdapter(Context context) {
        mContext = context;
    }

    public void setDeviceList(Collection<WifiP2pDevice> deviceList) {
        this.mDeviceList.clear();
        if (mDeviceList != null) {
            this.mDeviceList.addAll(deviceList);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DeviceAdapter.DeviceViewHolder holder, int position) {
        holder.tv_deviceName.setText(mDeviceList.get(position).deviceName);
        holder.tv_deviceAddress.setText(mDeviceList.get(position).deviceAddress);
        holder.tv_deviceStatus.setText(WifiHelper.getInstance(mContext).getDeviceStatus(mDeviceList.get(position).status));
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_deviceName;
        private TextView tv_deviceAddress;
        private TextView tv_deviceStatus;
        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_deviceAddress = itemView.findViewById(R.id.tv_device_ip);
            tv_deviceName = itemView.findViewById(R.id.tv_device_name);
            tv_deviceStatus = itemView.findViewById(R.id.tv_device_status);
        }
    }
}
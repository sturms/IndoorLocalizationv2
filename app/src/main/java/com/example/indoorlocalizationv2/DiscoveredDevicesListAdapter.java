package com.example.indoorlocalizationv2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.indoorlocalizationv2.models.DiscoveredDeviceInfo;
import com.example.indoorlocalizationv2.models.LocalizationInfo;

import java.util.List;

public class DiscoveredDevicesListAdapter extends BaseAdapter {
    private Context _context;
    private List<DiscoveredDeviceInfo> _discoveredDevicesInfoList;

    public DiscoveredDevicesListAdapter(Context context, List<DiscoveredDeviceInfo> discoveredDevicesInfoList) {
        _context = context;
        _discoveredDevicesInfoList = discoveredDevicesInfoList;
    }

    @Override
    public int getCount() {
        return _discoveredDevicesInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return _discoveredDevicesInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = View.inflate(_context, R.layout.discovered_devices_list, null);

        TextView tv_mac_addr = listView.findViewById(R.id.device_mac_addr);
        TextView tv_rssi = listView.findViewById(R.id.device_rssi_to_phone);
        TextView tv_name = listView.findViewById(R.id.device_defined_name);
        TextView tv_type = listView.findViewById(R.id.device_defined_type);

        DiscoveredDeviceInfo row = _discoveredDevicesInfoList.get(position);
        tv_mac_addr.setText(row.getMacAddress());
        tv_rssi.setText(Integer.toString(row.getRSSIToPhone()));
        tv_name.setText(row.getDefinedDeviceName());
        tv_type.setText(row.getDefinedDeviceType());

        return listView;
    }
}

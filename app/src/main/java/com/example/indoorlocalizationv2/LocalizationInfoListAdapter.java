package com.example.indoorlocalizationv2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.indoorlocalizationv2.models.BLEPosition;
import com.example.indoorlocalizationv2.models.LocalizationInfo;
import java.util.List;

public class LocalizationInfoListAdapter extends BaseAdapter {
    private Context _context;
    private List<LocalizationInfo> _localizationInfoList;

    public LocalizationInfoListAdapter(Context context, List<LocalizationInfo> localizationInfoList) {
        _context = context;
        _localizationInfoList = localizationInfoList;
    }

    @Override
    public int getCount() {
        return _localizationInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return _localizationInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(_context, R.layout.localization_info_list, null);

        BLEPosition leftAnchorData = _localizationInfoList.get(position).getLeftAnchor();
        if (leftAnchorData != null) {
            // Find left anchor text fields
            TextView tv_left_anchor = v.findViewById(R.id.device_anchor_left);
            TextView tv_left_anchor_rssi = v.findViewById(R.id.device_anchor_left_rssi);
            TextView tv_left_anchor_distance = v.findViewById(R.id.device_anchor_left_distance);
            TextView tv_left_anchor_macAddr = v.findViewById(R.id.device_anchor_left_mac_addr);

            // Set left anchor text fields values
            tv_left_anchor.setText(leftAnchorData.getName());
            tv_left_anchor_rssi.setText(Integer.toString(leftAnchorData.getRSSI()));
            tv_left_anchor_distance.setText(Float.toString(leftAnchorData.getDistance()));
            tv_left_anchor_macAddr.setText(leftAnchorData.getMacAddress());
        }

        BLEPosition frontAnchorData = _localizationInfoList.get(position).getFrontAnchor();
        if (frontAnchorData != null) {
            // Find front anchor text fields
            TextView tv_front_anchor = v.findViewById(R.id.device_anchor_front);
            TextView tv_front_anchor_rssi = v.findViewById(R.id.device_anchor_front_rssi);
            TextView tv_front_anchor_distance = v.findViewById(R.id.device_anchor_front_distance);
            TextView tv_front_anchor_macAddr = v.findViewById(R.id.device_anchor_front_mac_addr);

            // Set front anchor text fields values
            tv_front_anchor.setText(frontAnchorData.getName());
            tv_front_anchor_rssi.setText(Integer.toString(frontAnchorData.getRSSI()));
            tv_front_anchor_distance.setText(Float.toString(frontAnchorData.getDistance()));
            tv_front_anchor_macAddr.setText(frontAnchorData.getMacAddress());
        }

        BLEPosition rightAnchorData = _localizationInfoList.get(position).getRightAnchor();
        if (rightAnchorData != null) {
            // Find right anchor text fields
            TextView tv_right_anchor = v.findViewById(R.id.device_anchor_right);
            TextView tv_right_anchor_rssi = v.findViewById(R.id.device_anchor_right_rssi);
            TextView tv_right_anchor_distance = v.findViewById(R.id.device_anchor_right_distance);
            TextView tv_right_anchor_macAddr = v.findViewById(R.id.device_anchor_right_mac_addr);

            // Set right anchor text fields values
            tv_right_anchor.setText(rightAnchorData.getName());
            tv_right_anchor_rssi.setText(Integer.toString(rightAnchorData.getRSSI()));
            tv_right_anchor_distance.setText(Float.toString(rightAnchorData.getDistance()));
            tv_right_anchor_macAddr.setText(rightAnchorData.getMacAddress());
        }

        BLEPosition topAnchorData = _localizationInfoList.get(position).getTopAnchor();
        if (topAnchorData != null) {
            // Find top anchor text fields
            TextView tv_top_anchor = v.findViewById(R.id.device_anchor_top);
            TextView tv_top_anchor_rssi = v.findViewById(R.id.device_anchor_top_rssi);
            TextView tv_top_anchor_distance = v.findViewById(R.id.device_anchor_top_distance);
            TextView tv_top_anchor_macAddr = v.findViewById(R.id.device_anchor_top_mac_addr);

            // Set top anchor text fields values
            tv_top_anchor.setText(topAnchorData.getName());
            tv_top_anchor_rssi.setText(Integer.toString(topAnchorData.getRSSI()));
            tv_top_anchor_distance.setText(Float.toString(topAnchorData.getDistance()));
            tv_top_anchor_macAddr.setText(topAnchorData.getMacAddress());
        }

        BLEPosition beaconData = _localizationInfoList.get(position).getBeacon();
        if (beaconData != null) {
            // Find beacon text fields
            TextView tv_beacon = v.findViewById(R.id.device_beacon);
            TextView tv_beacon_rssi = v.findViewById(R.id.device_beacon_rssi);
            TextView tv_beacon_distance = v.findViewById(R.id.device_beacon_distance);
            TextView tv_bacon_macAddr = v.findViewById(R.id.device_beacon_mac_addr);
            TextView tv_posXYZ = v.findViewById(R.id.device_beacon_pos_xyz);

            // Set beacon text fields values
            tv_beacon.setText(beaconData.getName());
            tv_beacon_rssi.setText("-");
            tv_beacon_distance.setText("-");
            tv_bacon_macAddr.setText(beaconData.getMacAddress());
            tv_posXYZ.setText(beaconData.getX() + ", " + beaconData.getY() + ", " + beaconData.getZ());
        }

        CustomView customView = v.findViewById(R.id.v_localiz_position_graphically);

        // Front view
        customView.changeBeaconFrontViewCoordinates(beaconData.getX(), beaconData.getZ(), 10);
        customView.setTheShelfSizeFrontView(rightAnchorData.getX(), topAnchorData.getZ());

        // Top view
        customView.changeBeaconTopViewCoordinates(beaconData.getX(), beaconData.getY(), 10);
        customView.setTheShelfDepthTopView(frontAnchorData.getY());

        Button btn_moreInfo = v.findViewById(R.id.btn_localiz_view_position_graphically);
        btn_moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btnView) {
                // TODO: toggle visibility
            }
        });

        return v;
    }
}

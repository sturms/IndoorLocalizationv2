package com.example.indoorlocalizationv2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
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

        TextView tv_relationship = (TextView)v.findViewById(R.id.device_relationship);
        TextView tv_rssi = (TextView)v.findViewById(R.id.device_rssi);
        TextView tv_distance = (TextView)v.findViewById(R.id.device_distance);

        // Basic info
        tv_relationship.setText(_localizationInfoList.get(position).getDeviceRelationshipDisplayText());
        tv_rssi.setText(Integer.toString(_localizationInfoList.get(position).getRSSI()));
        tv_distance.setText(Float.toString(_localizationInfoList.get(position).getDistance()));

        // Additional info
        TextView tv_anchorMacAddr = (TextView)v.findViewById(R.id.anchor_mac_addr);
        TextView tv_beaconMacAddr = (TextView)v.findViewById(R.id.beacon_mac_addr);
        TextView tv_posXYZ = (TextView)v.findViewById(R.id.beacon_pos_xyz);

        tv_anchorMacAddr.setText(_localizationInfoList.get(position).getAnchorAddress());
        tv_beaconMacAddr.setText(_localizationInfoList.get(position).getBeaconAddress());
        tv_posXYZ.setText(
                _localizationInfoList.get(position).getPositionX()
                + ", "
                + _localizationInfoList.get(position).getPositionY()
                + ", "
                + _localizationInfoList.get(position).getPositionZ());

        return v;
    }
}

package com.example.indoorlocalizationv2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.indoorlocalizationv2.models.SearchBelongingsItemInfo;

import java.util.List;

public class SearchBelongingsListAdapter extends BaseAdapter {

    private Context _context;
    private List<SearchBelongingsItemInfo> _searchBelongingsInfoList;

    public SearchBelongingsListAdapter(Context context, List<SearchBelongingsItemInfo> searchBelongingsInfoList) {
        _context = context;
        _searchBelongingsInfoList = searchBelongingsInfoList;
    }

    @Override
    public int getCount() {
        return _searchBelongingsInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return _searchBelongingsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(_context, R.layout.search_belongings_list, null);

        SearchBelongingsItemInfo item = _searchBelongingsInfoList.get(position);
        if (item != null) {
            TextView tv_id = v.findViewById(R.id.belongings_item_id);
            TextView tv_name = v.findViewById(R.id.belongings_item_name);

            tv_id.setText(Integer.toString(item.getId()));
            tv_name.setText(item.getName());
        }

        return v;
    }
}

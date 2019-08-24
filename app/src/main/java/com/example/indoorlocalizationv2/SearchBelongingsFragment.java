package com.example.indoorlocalizationv2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.indoorlocalizationv2.models.SearchBelongingsItemInfo;

import java.util.ArrayList;
import java.util.List;

public class SearchBelongingsFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView _lvSearchBelongingsInfo;
    private List<SearchBelongingsItemInfo> _searchBelongingsInfoList;
    private SearchBelongingsListAdapter _searchBelongingsListAdapter;
    private List<String> _belongingsBoxesDropdownValues;
    private String _selectedBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // TODO: get arguments
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_belongings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        _lvSearchBelongingsInfo = view.findViewById(R.id.listview_search_belongings);
        if (_lvSearchBelongingsInfo != null) {
            _searchBelongingsInfoList = new ArrayList<>();
            _belongingsBoxesDropdownValues = new ArrayList<>();

            // Initializes search belongings list adapter
            _searchBelongingsListAdapter = new SearchBelongingsListAdapter(view.getContext(), _searchBelongingsInfoList);
            _lvSearchBelongingsInfo.setAdapter(_searchBelongingsListAdapter);

            // Populate the list with data from database (TODO: First needs to add a Belongings table)
            // TODO: using hard-coded data for testing purposes (remove when table is added)
            _searchBelongingsInfoList.add(new SearchBelongingsItemInfo(1, "Some stuff"));
            _searchBelongingsInfoList.add(new SearchBelongingsItemInfo(2, "Another stuff but long text"));
            _searchBelongingsInfoList.add(new SearchBelongingsItemInfo(3, "Other thingies"));
            _searchBelongingsListAdapter.notifyDataSetChanged();

            // TODO: replace dropdown's values with real data
            _belongingsBoxesDropdownValues.add(""); // First item should be empty
            _belongingsBoxesDropdownValues.add("b1");
            _belongingsBoxesDropdownValues.add("b2");
            _belongingsBoxesDropdownValues.add("b3");

            //get the spinner from the xml.
            Spinner dropdown = view.findViewById(R.id.belongings_boxes_dropdown);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, _belongingsBoxesDropdownValues);
            dropdown.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // On item click listener
            _lvSearchBelongingsInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Store the belongings values in bundle that wil be sent to 'ManageBelongingsFragment'
                    SearchBelongingsItemInfo clickedItem = _searchBelongingsInfoList.get(position);
                    // TODO: When ManageBelongingsFragment is created add the needed values to its bundle and navigate
                    // ManageDeviceFragment manageDeviceFragm = new ManageDeviceFragment();
                    // manageDeviceFragm.setArguments(bundle);
                    // _mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, manageDeviceFragm).commit();
                }
            });

            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String val = _belongingsBoxesDropdownValues.get(position);
                    _selectedBox = val; // Store into variable for later usage
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO: or not TODO
                }
            });

        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO: Search button click
        }
    }
}

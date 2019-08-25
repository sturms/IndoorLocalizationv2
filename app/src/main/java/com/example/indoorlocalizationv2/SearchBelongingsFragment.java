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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;
import com.example.indoorlocalizationv2.models.BelongingsOperationType;
import com.example.indoorlocalizationv2.models.SearchBelongingsItemInfo;
import com.example.indoorlocalizationv2.models.entities.BoxItem;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;

import java.util.ArrayList;
import java.util.List;

public class SearchBelongingsFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView _lvSearchBelongingsInfo;
    private List<SearchBelongingsItemInfo> _searchBelongingsInfoList;
    private SearchBelongingsListAdapter _searchBelongingsListAdapter;
    private List<String> _belongingsBoxesDropdownValues;
    private String _selectedBeaconName = "";
    private String _searchCriteria = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _selectedBeaconName = getArguments().get("beaconName").toString();
            _searchCriteria = getArguments().get("searchCriteria").toString();
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
        _searchBelongingsInfoList = new ArrayList<>();
        _belongingsBoxesDropdownValues = new ArrayList<>();

        // Initializes search belongings list adapter
        _searchBelongingsListAdapter = new SearchBelongingsListAdapter(view.getContext(), _searchBelongingsInfoList);
        _lvSearchBelongingsInfo.setAdapter(_searchBelongingsListAdapter);

        // On item click listener
        _lvSearchBelongingsInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Store the belongings values in bundle that wil be sent to 'ManageBelongingsFragment'
                SearchBelongingsItemInfo item = _searchBelongingsInfoList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("operationType", BelongingsOperationType.EDIT.toString());
                bundle.putString("searchCriteria", _searchCriteria);
                bundle.putInt("itemId", item.getId());
                bundle.putString("beaconName", _selectedBeaconName);

                BelongingsManagementFragment belongingsManagementFragm = new BelongingsManagementFragment();
                belongingsManagementFragm.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, belongingsManagementFragm).commit();

            }
        });

        _belongingsBoxesDropdownValues.add(""); // First item should be empty
        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
        List<DefinedDevice> beacons = database.definedDeviceDao().getByDeviceType(getString(R.string.manage_rdio_device_type_beacon));
        for (DefinedDevice b : beacons) {
            _belongingsBoxesDropdownValues.add(b.getDeviceName());
        }
        IndoorLocalizationDatabase.destroyInstance();

        // Get the spinner from the xml
        Spinner dropdown = view.findViewById(R.id.belongings_boxes_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, _belongingsBoxesDropdownValues);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _selectedBeaconName = _belongingsBoxesDropdownValues.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO: or not TODO
            }
        });

        // The value may be set if navigate back from Belongings management
        if (!_selectedBeaconName.isEmpty()) {
            // Selects the dropdown value
            dropdown.setSelection(adapter.getPosition(_selectedBeaconName));
        }
        adapter.notifyDataSetChanged();

        // Lastly we need to initiate the search
        EditText et_search = view.findViewById(R.id.et_belongings_search);
        et_search.setText(_searchCriteria);
        Button btn_search = view.findViewById(R.id.btn_belongings_search);
        btn_search.setOnClickListener(this);

        // Only trigger the search if coming back from belongings management page
        if (!_searchCriteria.isEmpty() || !_selectedBeaconName.isEmpty()) {
            btn_search.callOnClick();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_belongings_search:
                _searchCriteria = ((EditText)getView().findViewById(R.id.et_belongings_search)).getText().toString();

                // Resets the list data
                _searchBelongingsInfoList.clear();

                // Populate the list with data from database
                IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
                List<BoxItem> boxItems = database.boxItemDao().getAll();
                for (BoxItem item : boxItems) {

                    // Filtering by beacon name
                    if (!_selectedBeaconName.isEmpty()) {
                        DefinedDevice beacon = database.definedDeviceDao().getById(item.getBeaconMacAddress());
                        if (!beacon.getDeviceName().equals(_selectedBeaconName)) {
                            continue;
                        }
                    }

                    // Filtering by item name and description
                    if (!_searchCriteria.isEmpty()
                        && !item.getBoxItemName().contains(_searchCriteria)
                            && !_searchCriteria.contains(item.getBoxItemName())
                            && !item.getBoxItemDescription().contains(_searchCriteria)) {
                        continue;
                    }

                    _searchBelongingsInfoList.add(new SearchBelongingsItemInfo(item.getId(), item.getBoxItemName()));
                }

                _searchBelongingsListAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Search invoked", Toast.LENGTH_SHORT).show();
                IndoorLocalizationDatabase.destroyInstance();
                break;
        }
    }
}

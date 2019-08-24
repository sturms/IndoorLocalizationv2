package com.example.indoorlocalizationv2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;
import com.example.indoorlocalizationv2.models.BelongingsOperationType;
import com.example.indoorlocalizationv2.models.entities.BoxItem;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;

import java.util.ArrayList;
import java.util.List;

public class BelongingsManagementFragment extends Fragment implements View.OnClickListener {

    /**
     * The type operation which will be performed with belongings item.
     * Fore example- ADD, EDIT (BelongingsOperationType)
     */
    private String _operationType;
    private String _beaconName;
    private String _searchCriteria;
    private int _itemId;
    private List<String> _belongingsBoxesDropdownValues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            _operationType = getArguments().getString("operationType");
            _beaconName = getArguments().getString("beaconName");
            _searchCriteria = getArguments().getString("searchCriteria");
            _itemId = getArguments().getInt("itemId");
        }

        return inflater.inflate(R.layout.fragment_belongings_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        _belongingsBoxesDropdownValues = new ArrayList<>();
        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
        List<DefinedDevice> beacons = database.definedDeviceDao().getByDeviceType(getString(R.string.manage_rdio_device_type_beacon));
        for (DefinedDevice b : beacons) {
            _belongingsBoxesDropdownValues.add(b.getDeviceName());
        }

        Spinner dropdown = view.findViewById(R.id.belongings_manage_beacon_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, _belongingsBoxesDropdownValues);
        dropdown.setAdapter(adapter);

        if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
            Button deleteButton = getView().findViewById(R.id.btn_belongings_manage_item_delete);
            deleteButton.setVisibility(View.GONE);
        }
        else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
            // The delete button will be visible only in edit mode
            Button btn_delete = getView().findViewById(R.id.btn_belongings_manage_item_delete);
            btn_delete.setOnClickListener(this);

            // Retrieves the item's beacon name from database and selects dropdown
            BoxItem item = database.boxItemDao().getById(_itemId);
            ((EditText)view.findViewById(R.id.et_belongings_manage_item_name)).setText(item.getBoxItemName());
            ((EditText)view.findViewById(R.id.et_belongings_manage_item_descr)).setText(item.getBoxItemDescription());
            DefinedDevice itemDevice = database.definedDeviceDao().getById(item.getBeaconMacAddress());
            dropdown.setSelection(adapter.getPosition(itemDevice.getDeviceName()));
        }

        Button btn_save = getView().findViewById(R.id.btn_belongings_manage_item_save);
        Button btn_back = getView().findViewById(R.id.btn_belongings_manage_item_back);
        btn_save.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        IndoorLocalizationDatabase.destroyInstance();
    }

    @Override
    public void onClick(View btnView) {
        IndoorLocalizationDatabase database;

        switch (btnView.getId()) {
            case R.id.btn_belongings_manage_item_save:

                String selectedBeacon = ((Spinner)getView().findViewById(R.id.belongings_manage_beacon_dropdown)).getSelectedItem().toString();
                if (selectedBeacon.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select beacon value", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
                    database = IndoorLocalizationDatabase.getAppDatabase(getContext());
                    DefinedDevice beacon = database.definedDeviceDao().getByName(selectedBeacon);
                    BoxItem item = new BoxItem();
                    item.setBeaconMacAddress(beacon.getId());
                    String newItemName = ((EditText)getView().findViewById(R.id.et_belongings_manage_item_name)).getText().toString();
                    item.setBoxItemName(newItemName);
                    String newItemDescr = ((EditText)getView().findViewById(R.id.et_belongings_manage_item_descr)).getText().toString();
                    item.setBoxItemDescription(newItemDescr);
                    database.boxItemDao().insert(item);

                    Toast.makeText(getActivity(), "The item was added successfully", Toast.LENGTH_SHORT).show();
                    IndoorLocalizationDatabase.destroyInstance();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();

                }
                else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
                    database = IndoorLocalizationDatabase.getAppDatabase(getContext());
                    DefinedDevice beacon = database.definedDeviceDao().getByName(selectedBeacon);
                    BoxItem item = database.boxItemDao().getById(_itemId);
                    item.setBeaconMacAddress(beacon.getId());
                    String newItemName = ((EditText)getView().findViewById(R.id.et_belongings_manage_item_name)).getText().toString();
                    item.setBoxItemName(newItemName);
                    String newItemDescr = ((EditText)getView().findViewById(R.id.et_belongings_manage_item_descr)).getText().toString();
                    item.setBoxItemDescription(newItemDescr);
                    database.boxItemDao().update(item);

                    Toast.makeText(getActivity(), "The item was updated successfully", Toast.LENGTH_SHORT).show();
                    IndoorLocalizationDatabase.destroyInstance();
                    this.navigateBackToSearchPage();
                }

                break;
            case R.id.btn_belongings_manage_item_delete:
                database = IndoorLocalizationDatabase.getAppDatabase(getContext());
                database.boxItemDao().delete(_itemId);
                Toast.makeText(getActivity(), "The item was deleted successfully", Toast.LENGTH_SHORT).show();
                IndoorLocalizationDatabase.destroyInstance();
                this.navigateBackToSearchPage();
                break;
            case R.id.btn_belongings_manage_item_back:
                if (_operationType.equals(BelongingsOperationType.ADD.toString())) {
                    // Navigate back to home
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LocalizationInfoFragment()).commit();
                }
                else if (_operationType.equals(BelongingsOperationType.EDIT.toString())) {
                    this.navigateBackToSearchPage();
                }
                break;
        }
    }

    private void navigateBackToSearchPage() {
        Bundle bundle = new Bundle();
        bundle.putString("searchCriteria", _searchCriteria);
        bundle.putString("beaconName", _beaconName);
        SearchBelongingsFragment belongingsSearchFragm = new SearchBelongingsFragment();
        belongingsSearchFragm.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, belongingsSearchFragm).commit();
    }
}

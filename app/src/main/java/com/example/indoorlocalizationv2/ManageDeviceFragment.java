package com.example.indoorlocalizationv2;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;
import com.example.indoorlocalizationv2.models.BLEDevice;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;

import java.util.List;

public class ManageDeviceFragment extends Fragment implements View.OnClickListener {

    private String _macAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        _macAddress = getArguments().getString("macAddress");
        return inflater.inflate(R.layout.fragment_manage_device, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        EditText et_macAddress = view.findViewById(R.id.et_device_mac_address);
        EditText et_deviceName = view.findViewById(R.id.et_device_name);
        et_macAddress.setText(_macAddress);

        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
        DefinedDevice existingDeviceDb = database.definedDeviceDao().getById(_macAddress);
        if (existingDeviceDb != null) {
            et_deviceName.setText(existingDeviceDb.getDeviceName());
            this.setDeviceTypeRadioButtonValue(existingDeviceDb.getDeviceType());

            // Prefill the coordinates
            EditText et_posittionX = view.findViewById(R.id.et_anchor_coordinates_x);
            EditText et_posittionY = view.findViewById(R.id.et_anchor_coordinates_y);
            EditText et_posittionZ = view.findViewById(R.id.et_anchor_coordinates_z);
            et_posittionX.setText(Float.toString(existingDeviceDb.getAnchorCoordinateX()));
            et_posittionY.setText(Float.toString(existingDeviceDb.getAnchorCoordinateY()));
            et_posittionZ.setText(Float.toString(existingDeviceDb.getAnchorCoordinateZ()));
        }

        // This instance should be destroyed ASAP to save resources
        IndoorLocalizationDatabase.destroyInstance();

        // Sets event listeners for buttons
        Button btnSave = view.findViewById(R.id.btn_save_device_info);
        Button btnCancel = view.findViewById(R.id.btn_cancel_device_saving);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View btnView) {
        switch (btnView.getId()) {
            case R.id.btn_save_device_info:
                IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());

                View view = getView();
                EditText et_macAddress = view.findViewById(R.id.et_device_mac_address);
                EditText et_deviceName = view.findViewById(R.id.et_device_name);

                // Gets the selected radio button value
                RadioGroup rdio_deviceTypeGroup = view.findViewById(R.id.rdio_device_type_group);
                int selectedId = rdio_deviceTypeGroup.getCheckedRadioButtonId();
                RadioButton rdio_selectedBtn = view.findViewById(selectedId);

                String macAddress = et_macAddress.getText().toString();
                String deviceName = et_deviceName.getText().toString();
                String selectedRadioValue = rdio_selectedBtn.getText().toString();
                DefinedDevice existingDeviceDb = database.definedDeviceDao().getById(macAddress);

                float anchorCoordinateX = 0,
                        anchorCoordinateY = 0,
                        anchorCoordinateZ = 0;

                if (selectedRadioValue != getString(R.string.manage_rdio_device_type_beacon)) {
                    EditText et_anchorCoordinateX = view.findViewById(R.id.et_anchor_coordinates_x);
                    String anchorCoordinateXStr = et_anchorCoordinateX.getText().toString();
                    EditText et_anchorCoordinateY = view.findViewById(R.id.et_anchor_coordinates_y);
                    String anchorCoordinateYStr = et_anchorCoordinateY.getText().toString();
                    EditText et_anchorCoordinateZ = view.findViewById(R.id.et_anchor_coordinates_z);
                    String anchorCoordinateZStr = et_anchorCoordinateZ.getText().toString();

                    try {
                        anchorCoordinateX = Float.parseFloat(anchorCoordinateXStr);
                        anchorCoordinateY = Float.parseFloat(anchorCoordinateYStr);
                        anchorCoordinateZ = Float.parseFloat(anchorCoordinateZStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "Please provide valid X, Y, Z coordinates!", Toast.LENGTH_SHORT).show();
                        IndoorLocalizationDatabase.destroyInstance();
                        return;
                    }
                }

                DefinedDevice deviceByName = database.definedDeviceDao().getByName(deviceName);
                if (deviceByName != null) {
                    // Cannot be two devices with the same name
                    Toast.makeText(getActivity(), "Device with this name already exists!", Toast.LENGTH_SHORT).show();
                    IndoorLocalizationDatabase.destroyInstance();
                    return;
                }

                if (existingDeviceDb == null) {
                    // If device not already exists then adds it
                    this.addNewDeviceToDatabase(database, macAddress, deviceName, selectedRadioValue, anchorCoordinateX, anchorCoordinateY, anchorCoordinateZ);
                }
                else {
                    // If device exists then update its data
                    existingDeviceDb.setDeviceName(deviceName);
                    existingDeviceDb.setDeviceType(selectedRadioValue);
                    existingDeviceDb.setAnchorCoordinateX(anchorCoordinateX);
                    existingDeviceDb.setAnchorCoordinateY(anchorCoordinateY);
                    existingDeviceDb.setAnchorCoordinateZ(anchorCoordinateZ);
                    this.updateDeviceInDatabase(database, existingDeviceDb);
                }

                IndoorLocalizationDatabase.destroyInstance();
                break;
            case R.id.btn_cancel_device_saving:
                // Returns back to the Discover devices list
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new DiscoverDevicesFragment()).commit();
                break;
            default:
                break;
        }
    }

    private void addNewDeviceToDatabase(
            IndoorLocalizationDatabase database,
            String macAddress,
            String deviceName,
            String deviceType,
            float anchorCoordinateX,
            float anchorCoordinateY,
            float anchorCoordinateZ) {
        DefinedDevice definedDeviceDb = new DefinedDevice();

        definedDeviceDb.setId(macAddress);
        definedDeviceDb.setDeviceName(deviceName);
        definedDeviceDb.setDeviceType(deviceType);
        definedDeviceDb.setAnchorCoordinateX(anchorCoordinateX);
        definedDeviceDb.setAnchorCoordinateY(anchorCoordinateY);
        definedDeviceDb.setAnchorCoordinateZ(anchorCoordinateZ);

        database.definedDeviceDao().insert(definedDeviceDb);
        Toast.makeText(getActivity(), "Device successfully added!", Toast.LENGTH_SHORT).show();
    }

    private void updateDeviceInDatabase(IndoorLocalizationDatabase database, DefinedDevice deviceModified) {
        database.definedDeviceDao().update(deviceModified);
        Toast.makeText(getActivity(), "Device successfully updated!", Toast.LENGTH_SHORT).show();
    }

    private void setDeviceTypeRadioButtonValue(String deviceTypeText) {
        if (deviceTypeText.equals(getString(R.string.manage_rdio_device_type_f_anchor))) {
            ((RadioButton)getView().findViewById(R.id.rdio_device_type_f_anchor)).setChecked(true);

        } else if (deviceTypeText.equals(getString(R.string.manage_rdio_device_type_l_anchor))) {
            ((RadioButton)getView().findViewById(R.id.rdio_device_type_l_anchor)).setChecked(true);

        } else if (deviceTypeText.equals(getString(R.string.manage_rdio_device_type_r_anchor))) {
            ((RadioButton)getView().findViewById(R.id.rdio_device_type_r_anchor)).setChecked(true);

        } else if (deviceTypeText.equals(getString(R.string.manage_rdio_device_type_t_anchor))) {
            ((RadioButton)getView().findViewById(R.id.rdio_device_type_t_anchor)).setChecked(true);

        } else if (deviceTypeText.equals(getString(R.string.manage_rdio_device_type_beacon))) {
            ((RadioButton)getView().findViewById(R.id.rdio_device_type_beacon)).setChecked(true);

        } else {
            // No valid value was given..
        }
    }
}

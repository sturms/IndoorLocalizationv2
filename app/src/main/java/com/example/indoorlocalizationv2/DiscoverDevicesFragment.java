package com.example.indoorlocalizationv2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;

import com.example.indoorlocalizationv2.logic.IndoorLocalizationDatabase;
import com.example.indoorlocalizationv2.models.BLEPosition;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;
import com.example.indoorlocalizationv2.logic.BLELogic;
import com.example.indoorlocalizationv2.models.BLEDevice;
import com.example.indoorlocalizationv2.models.DiscoveredDeviceInfo;
import com.example.indoorlocalizationv2.models.entities.DeviceLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DiscoverDevicesFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private ListView _lvDiscoveredDevicesInfo;
    private List<DiscoveredDeviceInfo> _discoveredDevicesInfoList;
    private HashMap<String, DiscoveredDeviceInfo> _definedDevices;
    private DiscoveredDevicesListAdapter _discoveredDevicesListAdapter;
    private BLELogic _bleLogic;
    private MainActivity _mainActivity;
    private Button _btnScan;
    private boolean _isShowMyDevicesFilterEnabled;
    private boolean _isLoggingEnabled;
    private int _loggingCounter;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        _bleLogic.stopBluetoothScanner(mLeScanCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _mainActivity = (MainActivity)getActivity();
        _bleLogic = new BLELogic(_mainActivity);

        if (!_bleLogic.isBluetoothSupported()) {
            _mainActivity.finish();
        }

        if (!_bleLogic.isCoarseLocationEnabled()) {
            _bleLogic.enableCoarseLocation();
        }

        return inflater.inflate(R.layout.fragment_discover_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Localization list initialize
        _lvDiscoveredDevicesInfo = (ListView) view.findViewById(R.id.listview_discover_devices);
        if (_lvDiscoveredDevicesInfo != null) {
            _discoveredDevicesInfoList = new ArrayList<>();
            _definedDevices = new HashMap<>();

            // Initialize discovered devices list adapter
            _discoveredDevicesListAdapter = new DiscoveredDevicesListAdapter(view.getContext(), _discoveredDevicesInfoList);
            _lvDiscoveredDevicesInfo.setAdapter(_discoveredDevicesListAdapter);
            _lvDiscoveredDevicesInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Store the device values in bundle that will be sent to 'ManageDeviceFragment'
                    BLEDevice device = _bleLogic.getScannedDevices().get(_discoveredDevicesInfoList.get(position).getMacAddress());
                    Bundle bundle = new Bundle();
                    bundle.putString("macAddress", device.getMacAddress());
                    bundle.putString("deviceName", device.getDeviceName());
                    bundle.putString("deviceType", device.getDeviceType());

                    // TODO: send also the position

                    ManageDeviceFragment manageDeviceFragm = new ManageDeviceFragment();
                    manageDeviceFragm.setArguments(bundle);
                    _mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, manageDeviceFragm).commit();
                }
            });

            // Hash the defined devices which are stored in the database
            IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
            List<DefinedDevice> definedDevicesDbList = database.definedDeviceDao().getAll();
            IndoorLocalizationDatabase.destroyInstance();
            for (DefinedDevice definedDevice : definedDevicesDbList) {
                String macAddr = definedDevice.getId();
                BLEPosition position = new BLEPosition();
                position.setX(definedDevice.getAnchorCoordinateX());
                position.setY(definedDevice.getAnchorCoordinateY());
                position.setZ(definedDevice.getAnchorCoordinateZ());
                _definedDevices.put(macAddr, new DiscoveredDeviceInfo(null, macAddr, 0, definedDevice.getDeviceName(), definedDevice.getDeviceType(), position));
            }

            this.initializeEventHandlersForControls(view);
            this.initializeHardcodedAnchorNodes();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private void initializeHardcodedAnchorNodes() {
        for (String defaultAnchorAddr : _bleLogic.getDefaultAnchorNodes()) {
            String name = "";
            String type = "";
            BLEPosition position = null;
            if (_definedDevices.containsKey(defaultAnchorAddr)) {
                name = _definedDevices.get(defaultAnchorAddr).getDefinedDeviceName();
                type = _definedDevices.get(defaultAnchorAddr).getDefinedDeviceType();
                position = _definedDevices.get(defaultAnchorAddr).getPosition();
            }

            _discoveredDevicesInfoList.add(new DiscoveredDeviceInfo(null, defaultAnchorAddr, 0, name, type, position));
        }
    }

    /**
     * Note: Should the adapter be ArrayAdapter ?
     * @param device
     * @param rssiToPhone
     */
    public void addNewDeviceToList(BluetoothDevice device, int rssiToPhone) {
        String macAddress = device.getAddress();

        // If the filter option is enabled to show only defined devices
        // and this device is not user defined device then skip adding.
        boolean isUserDefinedDevice = _definedDevices.containsKey(macAddress);
        if (_isShowMyDevicesFilterEnabled && !isUserDefinedDevice) {
            return;
        }

        HashMap<String, BLEDevice> allDevices = _bleLogic.getScannedDevices();
        BLEDevice deviceInfo = new BLEDevice(device, isUserDefinedDevice);
        String definedDeviceName = "";
        String definedDeviceType = "";
        BLEPosition position = null;
        if (!allDevices.containsKey(macAddress)) {
            if (_definedDevices.containsKey(macAddress)) {
                DiscoveredDeviceInfo definedDevice = _definedDevices.get(macAddress);
                definedDeviceName = definedDevice.getDefinedDeviceName();
                definedDeviceType = definedDevice.getDefinedDeviceType();
                position = definedDevice.getPosition();
            }

            DiscoveredDeviceInfo discoveredDevice = new DiscoveredDeviceInfo(device, macAddress, rssiToPhone, definedDeviceName, definedDeviceType, position);
            _discoveredDevicesInfoList.add(discoveredDevice);
            deviceInfo.setMacAddress(macAddress);
        }
        else {
            // As we already know the device is already shown in the list view,
            // getting corresponding record to be able to update the list
            for (DiscoveredDeviceInfo existingDevice : _discoveredDevicesInfoList) {
                if (existingDevice.getMacAddress().equals(macAddress)) {
                    existingDevice.setRSSIToPhone(rssiToPhone);
                    break;
                }
            }

            deviceInfo = allDevices.get(macAddress);
        }

        if (_isLoggingEnabled && _loggingCounter % 5 == 0) {
            this.logData(macAddress, definedDeviceName, definedDeviceType, rssiToPhone);
            _loggingCounter = 0;
        }

        if (_isLoggingEnabled) {
            _loggingCounter++;
        }

        _bleLogic.addOrUpdateDevice(macAddress, deviceInfo);
        _discoveredDevicesListAdapter.notifyDataSetChanged();
    }

    /**
     * Click event for buttons which are subscribed to this event.
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discover_btn_scan:
                Button scanBtn = getView().findViewById(R.id.discover_btn_scan);
                if (!_bleLogic.isScanning()) {
                    scanBtn.setText(R.string.discov_btn_stop_scan);
                    _discoveredDevicesInfoList.clear();
                    _bleLogic.getScannedDevices().clear();
                    _bleLogic.addAnchorNodesToScannedDevices();
                    initializeHardcodedAnchorNodes();
                    _discoveredDevicesListAdapter.notifyDataSetChanged();
                    _bleLogic.startBluetoothScanner(mLeScanCallback);
                }
                else {
                    scanBtn.setText(R.string.discov_btn_start_scan);
                    _bleLogic.stopBluetoothScanner(mLeScanCallback);
                }

                break;
            default:
                break;
        }
    }

    private void initializeEventHandlersForControls(View view) {
        _btnScan = view.findViewById(R.id.discover_btn_scan);
        _btnScan.setOnClickListener(this);

        CheckBox chboxOnlyMyDevices = view.findViewById(R.id.discover_chbx_only_my_devices);
        chboxOnlyMyDevices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                _discoveredDevicesInfoList.clear();
                _bleLogic.getScannedDevices().clear();
                _bleLogic.addAnchorNodesToScannedDevices();
                _discoveredDevicesListAdapter.notifyDataSetChanged();
                _isShowMyDevicesFilterEnabled = isChecked;
            }
        });

        CheckBox chboxLogData = view.findViewById(R.id.discover_chbx_log_data);
        chboxLogData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO: logging logic here
                _isLoggingEnabled = isChecked;
            }
        });
    }

    private void logData(String deviceAddress, String deviceName, String deviceType, int rssi) {
        DeviceLog log = new DeviceLog();
        log.setId(UUID.randomUUID().toString());
        log.setMacAddress(deviceAddress);
        log.setDeviceName(deviceName);
        log.setDeviceType(deviceType);
        String flag = ((EditText)getView().findViewById(R.id.discover_et_log_flag)).getText().toString();
        log.set_flag(flag);
        log.setRssi(rssi);

        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(getContext());
        database.deviceLogDao().insert(log);
        IndoorLocalizationDatabase.destroyInstance();
    }

    private Handler _leScanHandler = new Handler();
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            final int newRSSI = rssi;
            _leScanHandler.post(new Runnable() {
                @Override
                public void run() {
                    addNewDeviceToList(device, newRSSI);
                }
            });
        }
    };
}

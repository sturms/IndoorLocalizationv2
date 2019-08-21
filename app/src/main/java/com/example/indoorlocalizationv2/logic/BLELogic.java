package com.example.indoorlocalizationv2.logic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.example.indoorlocalizationv2.models.BLEDevice;
import com.example.indoorlocalizationv2.models.BLEPosition;
import com.example.indoorlocalizationv2.models.DiscoveredDeviceInfo;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Logic regarding BLE scanning and GATT service usage.
 */
public class BLELogic {

    public static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private final BluetoothAdapter _bluetoothAdapter;
    private Activity _activity;
    private boolean _isScanning;
    private HashMap<String, BLEDevice> _devices;
    private HashMap<String, DiscoveredDeviceInfo> _definedDevicesHashMap;
    private List<String> _definedDevicesList;
    private List<String> _defaultAnchorNodes;

    /**
     * Constructor
     */
    public BLELogic(Activity activity) {
        final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        _bluetoothAdapter = bluetoothManager.getAdapter();
        _activity = activity;
        _devices = new HashMap<>();
        _definedDevicesHashMap = new HashMap<>();
        _definedDevicesList = new ArrayList<>();
        _defaultAnchorNodes = new ArrayList<>();
        _defaultAnchorNodes.add("3C:71:BF:AB:19:22");
        _defaultAnchorNodes.add("3C:71:BF:AB:17:66");
        _defaultAnchorNodes.add("3C:71:BF:9E:20:6A");
        _defaultAnchorNodes.add("3C:71:BF:AA:68:92");
        // TODO: add the rest

        // Hash the defined devices which are stored in the database
        IndoorLocalizationDatabase database = IndoorLocalizationDatabase.getAppDatabase(_activity);
        List<DefinedDevice> definedDevicesDbList = database.definedDeviceDao().getAll();
        IndoorLocalizationDatabase.destroyInstance();
        for (DefinedDevice definedDevice : definedDevicesDbList) {
            String macAddr = definedDevice.getId();

            // Either anchor node defined coordinates or beacon's calculated coordinates and distance
            BLEPosition position = new BLEPosition(
                    definedDevice.getAnchorCoordinateX(),
                    definedDevice.getAnchorCoordinateY(),
                    definedDevice.getAnchorCoordinateZ(),
                    0);

            _definedDevicesHashMap.put(
                    macAddr,
                    new DiscoveredDeviceInfo(
                            null,
                            macAddr,
                            0,
                            definedDevice.getDeviceName(),
                            definedDevice.getDeviceType(),
                            position
                    )
            );

            _definedDevicesList.add(macAddr);
        }

        // Define default anchor nodes
        for (String anchorAddress : _defaultAnchorNodes) {
            _definedDevicesList.add(anchorAddress);
        }

        this.addAnchorNodesToScannedDevices();
    }

    public HashMap<String, DiscoveredDeviceInfo> getDefinedDevicesHashMap() {
        return _definedDevicesHashMap;
    }

    public boolean isScanning() {
        return _isScanning;
    }

    public HashMap<String, BLEDevice> getScannedDevices() {
        return _devices;
    }

    /**
     * This needs to be done because if configuring ESP32 to
     * use WiFi then it does not broadcasting the bluetooth signals anymore.
     * The scanned list should contain anchor addresses so that they could be defined.
     */
    public void addAnchorNodesToScannedDevices() {
        for (String anchorAddress : _defaultAnchorNodes) {
            String name = "";
            String type = "";
            if (_definedDevicesHashMap.containsKey(anchorAddress)) {
                name = _definedDevicesHashMap.get(anchorAddress).getDefinedDeviceName();
                type = _definedDevicesHashMap.get(anchorAddress).getDefinedDeviceType();
            }

            BLEDevice device = new BLEDevice(null, true);
            device.setMacAddress(anchorAddress);
            device.setDeviceName(name);
            device.setDeviceType(type);
            _devices.put(anchorAddress, device);
        }
    }

    public List<String> getDefaultAnchorNodes() {
        return _defaultAnchorNodes;
    }

    /**
     * Store additional data about scanned device
     * for easy access by given device mac address.
     * @param macAddress
     * @param deviceInfo
     */
    public void addOrUpdateDevice(String macAddress, BLEDevice deviceInfo) {
        // Updates the modification time before adding this item to hash map
        deviceInfo.updateModificationTime();

        // Adds or updates the existing key value
        _devices.put(macAddress, deviceInfo);
    }

    public boolean isBluetoothSupported() {
        return _activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isCoarseLocationEnabled() {
        return _activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void enableCoarseLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setTitle("This app needs location access");
        builder.setMessage("Please grant location access so this app can detect peripherals.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                _activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }

    public boolean checkBluetooth() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (_bluetoothAdapter == null || !_bluetoothAdapter.isEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    public void requestUserBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        _activity.startActivityForResult(enableBtIntent, this.REQUEST_ENABLE_BT);
    }

    public void startBluetoothScanner(BluetoothAdapter.LeScanCallback callback) {
        if (!this.checkBluetooth()) {
            this.requestUserBluetooth();
        } else {
            if (!_isScanning) {
                this.scanLeDevice(true, callback);
            }
        }
    }

    public void stopBluetoothScanner(BluetoothAdapter.LeScanCallback callback) {
        if (_isScanning) {
            this.scanLeDevice(false, callback);
        }
    }

    private void scanLeDevice(final boolean enable, BluetoothAdapter.LeScanCallback callback) {
        if (enable) {
            _isScanning = true;
            _bluetoothAdapter.startLeScan(callback);
        } else {
            _isScanning = false;
            _bluetoothAdapter.stopLeScan(callback);
        }
    }
}

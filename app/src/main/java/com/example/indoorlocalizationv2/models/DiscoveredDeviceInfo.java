package com.example.indoorlocalizationv2.models;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class DiscoveredDeviceInfo {
    private String _macAddress;
    private int _rssi;
    private String _definedDeviceName;
    private String _definedDeviceType;
    private BluetoothDevice _bluetoothDevice;
    private BluetoothGatt _bluetoothGattService;
    private BLEPosition _position;

    public DiscoveredDeviceInfo(
            BluetoothDevice device,
            String macAddress,
            int rssi,
            String definedDeviceName,
            String definedDeviceType,
            BLEPosition position) {

        _bluetoothDevice = device;
        _macAddress = macAddress;
        _rssi = rssi;
        _definedDeviceName = definedDeviceName;
        _definedDeviceType = definedDeviceType;
        _bluetoothGattService = null;
        _position = position;
    }

    public String getMacAddress(){
        return _macAddress;
    }

    public int getRSSIToPhone(){
        return _rssi;
    }

    public void setRSSIToPhone(int rssi) {
        _rssi = rssi;
    }

    public String getDefinedDeviceName() {
        return _definedDeviceName;
    }

    public void setDefinedDeviceName(String definedDeviceName) {
        _definedDeviceName = definedDeviceName;
    }

    public String getDefinedDeviceType() {
        return _definedDeviceType;
    }

    public void setDefinedDeviceType(String definedDeviceType) {
        _definedDeviceType = definedDeviceType;
    }

    public BluetoothDevice getBluetoothDevice() {
        return _bluetoothDevice;
    }

    public BluetoothGatt getBluetoothGattService() {
        return _bluetoothGattService;
    }

    public void setBluetoothGattService(BluetoothGatt gatt) {
        _bluetoothGattService = gatt;
    }

    public BLEPosition getPosition() {
        return _position;
    }

    public void setPosition(BLEPosition position) {
        _position = position;
    }
}

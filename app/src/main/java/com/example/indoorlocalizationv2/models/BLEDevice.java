package com.example.indoorlocalizationv2.models;

import android.bluetooth.BluetoothDevice;
import java.util.Date;
import java.util.UUID;

public class BLEDevice {
    private final int deviceInactiveTimeoutInSeconds = 20;
    private String _deviceName;
    private int _rssi;
    private String _macAddress;
    private String _deviceType;
    private UUID _deviceUUID;
    private Date _lastModifiedDateTime;
    private int _posX;
    private int _posY;
    private int _posZ;
    private BluetoothDevice _bluetoothDevice;
    private boolean _isUserDefinedDevice;

    /**
     * Constructor
     * @param device
     */
    public BLEDevice(BluetoothDevice device, boolean isUserDefinedDevice) {
        _bluetoothDevice = device;
        _isUserDefinedDevice = isUserDefinedDevice;
        _lastModifiedDateTime = new Date(System.currentTimeMillis());
    }

    /**
     * Checks whether device is active or not by comparing
     * last data modification time to time border value.
     * @return
     */
    public boolean isDeviceActive() {
        Date currentTime = new Date(System.currentTimeMillis());
        return (currentTime.getTime() - _lastModifiedDateTime.getTime()) / 1000 <= deviceInactiveTimeoutInSeconds;
    }

    /**
     * Updates this device modification time to datetime now.
     */
    public void updateModificationTime() {
        _lastModifiedDateTime = new Date(System.currentTimeMillis());
    }

    /**
     * Checks whether this device is user defined (name and type is given).
     * @return
     */
    public boolean isUserDefinedDevice() {
        return _isUserDefinedDevice;
    }

    public BluetoothDevice getBluetoothDeviceInstance(){
        return _bluetoothDevice;
    }

    public String getDeviceName(){
        return _deviceName;
    }

    public void setDeviceName(String name) {
        _deviceName = name;
    }

    public int getRssi(){
        return _rssi;
    }

    public void setRssi(int rssi) {
        _rssi = rssi;
    }

    public String getMacAddress() {
        return _macAddress;
    }

    public void setMacAddress(String macAddress) {
        _macAddress = macAddress;
    }

    public String getDeviceType() {
        return _deviceType;
    }

    public UUID getDeviceUUID() {
        return _deviceUUID;
    }

    public void setDeviceUUID(UUID deviceUUID) {
        _deviceUUID = deviceUUID;
    }

    public void setDeviceType(String deviceType) {
        _deviceType = deviceType;
    }

    public int getPosX(){
        return _posX;
    }

    public void setPosX(int posX){
        _posX = posX;
    }

    public int getPosY(){
        return _posY;
    }

    public void setPosY(int posY){
        _posY = posY;
    }

    public int getPosZ(){
        return _posZ;
    }

    public void setPosZ(int posZ){
        _posZ = posZ;
    }

    public void setPosition(int x, int y, int z) {
        _posX = x;
        _posY = y;
        _posZ = z;
    }
}

package com.example.indoorlocalizationv2.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "device_log")
public class DeviceLog {

    /**
     * Device mac address
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "log_id")
    private String _id;

    @ColumnInfo(name = "log_mac_address")
    private String _macAddress;

    @ColumnInfo(name = "log_device_name")
    private String _deviceName;

    @ColumnInfo(name = "log_device_type")
    private String _deviceType;

    @ColumnInfo(name = "log_flag")
    private String _flag;

    @ColumnInfo(name = "log_rssi")
    private int _rssi;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getDeviceName() {
        return _deviceName;
    }

    public void setDeviceName(String deviceName) {
        _deviceName = deviceName;
    }

    public String getDeviceType() {
        return _deviceType;
    }

    public void setDeviceType(String deviceType) {
        _deviceType = deviceType;
    }

    public String getMacAddress() {
        return _macAddress;
    }

    public void setMacAddress(String macAddress) {
        _macAddress = macAddress;
    }

    public String getFlag(){
        return _flag;
    }

    public void set_flag(String flag) {
        _flag = flag;
    }

    public int getRssi() {
        return _rssi;
    }

    public void setRssi(int rssi) {
        _rssi = rssi;
    }
}

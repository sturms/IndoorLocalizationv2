package com.example.indoorlocalizationv2.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "defined_device")
public class DefinedDevice {

    /**
     * Device mac address
     */
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "defined_device_id")
    private String _id;

    @ColumnInfo(name = "defined_device_name")
    private String _deviceName;

    @ColumnInfo(name = "defined_device_type")
    private String _deviceType;

    @ColumnInfo(name = "anchor_coordinate_x")
    private float _anchorCoordinateX;

    @ColumnInfo(name = "anchor_coordinate_y")
    private float _anchorCoordinateY;

    @ColumnInfo(name = "anchor_coordinate_z")
    private float _anchorCoordinateZ;

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

    public float getAnchorCoordinateX() {
        return _anchorCoordinateX;
    }

    public void setAnchorCoordinateX(float coordinateX) {
        _anchorCoordinateX = coordinateX;
    }

    public float getAnchorCoordinateY() {
        return _anchorCoordinateY;
    }

    public void setAnchorCoordinateY(float coordinateY) {
        _anchorCoordinateY = coordinateY;
    }

    public float getAnchorCoordinateZ() {
        return _anchorCoordinateZ;
    }

    public void setAnchorCoordinateZ(float coordinateZ) {
        _anchorCoordinateZ = coordinateZ;
    }
}

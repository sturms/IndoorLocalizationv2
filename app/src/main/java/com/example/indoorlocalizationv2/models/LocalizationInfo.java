package com.example.indoorlocalizationv2.models;

public class LocalizationInfo {
    private String _deviceRelationshipDisplayText;
    private int _rssi;
    private float _distance;
    private String _anchorAddress;
    private String _beaconAddress;
    private float _positionX;
    private float _positionY;
    private float _positionZ;

    public LocalizationInfo(
            String deviceRelationshipDisplayText,
            int rssi,
            float distance,
            String anchorAddress,
            String beaconAddress,
            float positionX,
            float positionY,
            float positionZ) {

        _deviceRelationshipDisplayText = deviceRelationshipDisplayText;
        _rssi = rssi;
        _distance = distance;
        _anchorAddress = anchorAddress;
        _beaconAddress = beaconAddress;
        _positionX = positionX;
        _positionY = positionY;
        _positionZ = positionZ;
    }

    public String getDeviceRelationshipDisplayText() {
        return _deviceRelationshipDisplayText;
    }

    public void setDeviceRelationshipDisplayText(String displayText) {
        _deviceRelationshipDisplayText = displayText;
    }

    public int getRSSI() {
        return _rssi;
    }

    public float getDistance() {
        return _distance;
    }

    public String getBeaconAddress() {
        return _beaconAddress;
    }

    public float getPositionX() {
        return _positionX;
    }

    public float getPositionY() {
        return _positionY;
    }

    public float getPositionZ() {
        return _positionZ;
    }

    public String getAnchorAddress() {
        return _anchorAddress;
    }

    public void setRSSI(int rssi) {
        _rssi = rssi;
    }

    public void setDistance(float distance) {
        _distance = distance;
    }

    public void setAnchorAddress(String anchorAddress) {
        _anchorAddress = anchorAddress;
    }

    public void setBeaconAddress(String beaconAddress) {
        _beaconAddress = beaconAddress;
    }

    public void setPositionX(float positionX) {
        _positionX = positionX;
    }

    public void setPositionY(float positionY) {
        _positionY = positionY;
    }

    public void setPositionZ(float positionZ) {
        _positionZ = positionZ;
    }
}

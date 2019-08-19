package com.example.indoorlocalizationv2.models;

public class RSSITableValue {
    private double _realDistance;
    private int _rssi;

    public RSSITableValue(double distance, int rssi) {
        _realDistance = distance;
        _rssi = rssi;
    }

    public double getDistance() {
        return _realDistance;
    }

    public void setDistance(double distance) {
        _realDistance = distance;
    }

    public int getRssi() {
        return _rssi;
    }

    public void setRssi(int rssi) {
        _rssi = rssi;
    }
}

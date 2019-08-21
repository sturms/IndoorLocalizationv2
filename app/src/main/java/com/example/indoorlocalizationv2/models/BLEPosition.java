package com.example.indoorlocalizationv2.models;

public class BLEPosition {
    private float x;
    private float y;
    private float z;
    private float distance;

    // Some optional fields
    private String name;
    private String macAddress;
    private int rssi;

    public BLEPosition() {}

    public BLEPosition(float x, float y, float z, float distance) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.distance = distance;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ(){
        return this.z;
    }

    public float getDistance() {
        return this.distance;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return this.macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public int getRSSI() {
        return this.rssi;
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }
}

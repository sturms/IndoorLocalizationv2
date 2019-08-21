package com.example.indoorlocalizationv2.models;

public class LocalizationInfo {

    private BLEPosition beacon;
    private BLEPosition leftAnchor;
    private BLEPosition frontAnchor;
    private BLEPosition rightAnchor;
    private BLEPosition topAnchor;

    public LocalizationInfo(
            BLEPosition beacon,
            BLEPosition leftAnchor,
            BLEPosition frontAnchor,
            BLEPosition rightAnchor,
            BLEPosition topAnchor) {

        this.beacon = beacon;
        this.leftAnchor = leftAnchor;
        this.frontAnchor = frontAnchor;
        this.rightAnchor = rightAnchor;
        this.topAnchor = topAnchor;
    }

    public BLEPosition getBeacon() {
        return beacon;
    }

    public void setBeacon(BLEPosition beacon) {
        this.beacon = beacon;
    }

    public BLEPosition getLeftAnchor() {
        return leftAnchor;
    }

    public void setLeftAnchor(BLEPosition leftAnchor) {
        this.leftAnchor = leftAnchor;
    }

    public BLEPosition getFrontAnchor() {
        return frontAnchor;
    }

    public void setFrontAnchor(BLEPosition frontAnchor) {
        this.frontAnchor = frontAnchor;
    }

    public BLEPosition getRightAnchor() {
        return rightAnchor;
    }

    public void setRightAnchor(BLEPosition rightAnchor) {
        this.rightAnchor = rightAnchor;
    }

    public BLEPosition getTopAnchor() {
        return topAnchor;
    }

    public void setTopAnchor(BLEPosition topAnchor) {
        this.topAnchor = topAnchor;
    }
}

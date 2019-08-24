package com.example.indoorlocalizationv2.models.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "box_item")
public class BoxItem {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "box_item_id")
    private int id;

    @ColumnInfo(name = "beacon_mac_address")
    private String beaconMacAddress;

    @ColumnInfo(name = "box_item_name")
    private String boxItemName;

    @ColumnInfo(name = "box_item_description")
    private String boxItemDescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeaconMacAddress() {
        return beaconMacAddress;
    }

    public void setBeaconMacAddress(String beaconMacAddress) {
        this.beaconMacAddress = beaconMacAddress;
    }

    public String getBoxItemName() {
        return boxItemName;
    }

    public void setBoxItemName(String boxItemName) {
        this.boxItemName = boxItemName;
    }

    public String getBoxItemDescription() {
        return boxItemDescription;
    }

    public void setBoxItemDescription(String boxItemDescription) {
        this.boxItemDescription = boxItemDescription;
    }
}

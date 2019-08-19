package com.example.indoorlocalizationv2.logic.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.indoorlocalizationv2.models.entities.DeviceLog;

import java.util.List;

@Dao
public interface DeviceLogDao {

    @Insert
    void insert(DeviceLog logToInsert);

    @Query("SELECT * FROM device_log")
    List<DeviceLog> getAll();

    @Query("DELETE FROM device_log")
    void clearAllData();
}

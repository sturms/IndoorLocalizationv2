package com.example.indoorlocalizationv2.logic.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import com.example.indoorlocalizationv2.models.entities.DefinedDevice;

@Dao
public interface DefinedDeviceDao {

    @Insert
    void insert(DefinedDevice deviceToInsert);

    @Query("SELECT * FROM defined_device")
    List<DefinedDevice> getAll();

    @Query("SELECT * FROM defined_device WHERE defined_device_id =:id")
    DefinedDevice getById(String id);

    @Update
    void update(DefinedDevice deviceToUpdate);

    @Query("DELETE FROM defined_device")
    void clearAllData();
}
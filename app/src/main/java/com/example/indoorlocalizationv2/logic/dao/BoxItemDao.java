package com.example.indoorlocalizationv2.logic.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

import com.example.indoorlocalizationv2.models.entities.BoxItem;

@Dao
public interface BoxItemDao {

    @Insert
    void insert(BoxItem itemToInsert);

    @Query("SELECT * FROM box_item")
    List<BoxItem> getAll();

    @Query("SELECT * FROM box_item WHERE box_item_id =:id")
    BoxItem getById(int id);

    @Update
    void update(BoxItem itemToUpdate);

    @Query("DELETE FROM box_item")
    void clearAllData();
}

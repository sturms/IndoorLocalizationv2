package com.example.indoorlocalizationv2.logic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.indoorlocalizationv2.logic.dao.BoxItemDao;
import com.example.indoorlocalizationv2.logic.dao.DefinedDeviceDao;
import com.example.indoorlocalizationv2.logic.dao.DeviceLogDao;
import com.example.indoorlocalizationv2.models.entities.BoxItem;
import com.example.indoorlocalizationv2.models.entities.DefinedDevice;
import com.example.indoorlocalizationv2.models.entities.DeviceLog;

@Database(entities = {DefinedDevice.class, DeviceLog.class, BoxItem.class}, version = 4)
public abstract class IndoorLocalizationDatabase extends RoomDatabase {

    private static final String databaseFileName = "indoor_localization_database";
    private static IndoorLocalizationDatabase INSTANCE;

    public static IndoorLocalizationDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), IndoorLocalizationDatabase.class, databaseFileName)
                    .allowMainThreadQueries()
                    // .fallbackToDestructiveMigration() // Uncomment if made changes in table structure
                    .build();
        }
        return INSTANCE;
    }

    public abstract DefinedDeviceDao definedDeviceDao();

    public abstract DeviceLogDao deviceLogDao();

    public abstract BoxItemDao boxItemDao();

    public static void destroyInstance() {
        INSTANCE = null;
    }
}

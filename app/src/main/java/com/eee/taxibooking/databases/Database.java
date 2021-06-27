package com.eee.taxibooking.databases;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Address.class}, version  = 1,exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract AddressDao addressDao();

    private static Database INSTANCE;

    public static Database getDbInstance(Context context) {

        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "ADDRESSES")
                    .allowMainThreadQueries()
                    .build();

        }
        return INSTANCE;
    }
}
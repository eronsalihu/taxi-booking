package com.eee.taxibooking.databases;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "addresses.db";
    public static final String TABLE_NAME = "address_table";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String ADDRESS = "ADDRESS";
    public static final String CITY = "CITY";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, ADDRESS TEXT, CITY TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name, String address, String city){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,name);
        contentValues.put(ADDRESS,address);
        contentValues.put(CITY,city);

        long result = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        if (result  == -1){
            return false;
        }
        else {
            return true;
        }
    }
}

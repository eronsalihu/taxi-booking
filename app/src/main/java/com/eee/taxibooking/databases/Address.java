package com.eee.taxibooking.databases;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Address {

    @PrimaryKey(autoGenerate = true)
    public int ID;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "city")
    public String city;

}

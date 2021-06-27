package com.eee.taxibooking.databases;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AddressDao {
    @Query("SELECT * FROM address")
    List<Address> getAllAddresses();

    @Insert
    void insertAddress(Address... addresses);

    @Delete
    void delete(Address address);
}


package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.geostat.census_2024.data.local.entities.User;

@Dao
public interface UserDao {
    @Insert
    Long insert(User user);

    @Query("select * from users where userName = :userName")
    User find(String userName);
}

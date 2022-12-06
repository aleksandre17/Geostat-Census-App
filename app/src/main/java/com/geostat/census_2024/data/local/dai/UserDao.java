package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.geostat.census_2024.data.local.entities.UserEntity;

@Dao
public interface UserDao {
    @Insert
    Long insert(UserEntity userEntity);

    @Query("select * from users where userName = :userName")
    UserEntity find(String userName);
}

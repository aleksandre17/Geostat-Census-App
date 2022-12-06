package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Query;

import com.geostat.census_2024.data.local.entities.LivingStatusEntity;

import java.util.List;

@Dao
public interface LivingStatusDao {

    @Query("SELECT * FROM cl_living_statuses")
    List<LivingStatusEntity> getAllBuildingTypes();
}

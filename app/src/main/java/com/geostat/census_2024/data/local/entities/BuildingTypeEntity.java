package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.geostat.census_2024.data.inter.ISpinner;

@Entity(tableName = "cl_building_types")
public class BuildingTypeEntity implements ISpinner {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @NonNull
    private String name;

    public BuildingTypeEntity(@NonNull Integer id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public Integer getId() {
        return id;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

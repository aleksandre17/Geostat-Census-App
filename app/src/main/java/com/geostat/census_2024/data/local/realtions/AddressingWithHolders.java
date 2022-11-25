package com.geostat.census_2024.data.local.realtions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.Holder;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;

import java.util.List;


public class AddressingWithHolders {

    @Embedded
    public Addressing addressing;

    @Relation(parentColumn = "id", entityColumn = "addressing_id")
    public SupervisionEntity supervisionEntity;

    @Relation(parentColumn = "buildingType", entityColumn = "id")
    public BuildingType buildingType;

    @Relation(parentColumn = "id", entityColumn = "addressingId")
    public List<Holder> holder;
}

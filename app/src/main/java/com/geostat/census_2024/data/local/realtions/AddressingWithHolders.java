package com.geostat.census_2024.data.local.realtions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.geostat.census_2024.data.local.entities.InquireActivityV1DateStatusEntity;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.entities.BuildingTypeEntity;
import com.geostat.census_2024.data.local.entities.InquireV1HolderEntity;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;

import java.util.List;


public class AddressingWithHolders {

    @Embedded
    public InquireV1Entity inquireV1Entity;

    @Relation(parentColumn = "id", entityColumn = "addressing_id")
    public SupervisionEntity supervisionEntity;

    @Relation(parentColumn = "buildingType", entityColumn = "id")
    public BuildingTypeEntity buildingTypeEntity;

    @Relation(parentColumn = "id", entityColumn = "addressingId")
    public List<InquireV1HolderEntity> inquireV1HolderEntity;

    @Relation(parentColumn = "id", entityColumn = "addressing_id")
    public List<InquireActivityV1DateStatusEntity> dateStatues;
}

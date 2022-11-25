package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cl_address")
public class Address {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @Nullable
    private Integer parentId;
    @Nullable
    private String locationCode;
    @Nullable
    private String locationName;
    @Nullable
    private Integer locationTypeId;
    @Nullable
    private Integer urbanTypeId;
    @Nullable
    private Boolean inactive;
    @Nullable
    private Integer recUserId;
    @Nullable
    private String recDate;
    @Nullable
    private String recType;
    @Nullable
    private Integer level;
    @Nullable
    private Integer locationRootId;
    @Nullable
    private Integer locationMunicId;

    public Address(@NonNull Integer id, @Nullable Integer parentId, @Nullable String locationCode, @Nullable String locationName, @Nullable Integer locationTypeId, @Nullable Integer urbanTypeId, @Nullable Boolean inactive, @Nullable Integer recUserId, @Nullable String recDate, @Nullable String recType, @Nullable Integer level, @Nullable Integer locationRootId, @Nullable Integer locationMunicId) {
        this.id = id;
        this.parentId = parentId;
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.locationTypeId = locationTypeId;
        this.urbanTypeId = urbanTypeId;
        this.inactive = inactive;
        this.recUserId = recUserId;
        this.recDate = recDate;
        this.recType = recType;
        this.level = level;
        this.locationRootId = locationRootId;
        this.locationMunicId = locationMunicId;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    @Nullable
    public Integer getParentId() {
        return parentId;
    }

    @Nullable
    public String getLocationCode() {
        return locationCode;
    }

    @Nullable
    public String getLocationName() {
        return locationName;
    }

    @Nullable
    public Integer getLocationTypeId() {
        return locationTypeId;
    }

    @Nullable
    public Integer getUrbanTypeId() {
        return urbanTypeId;
    }

    @Nullable
    public Boolean getInactive() {
        return inactive;
    }

    @Nullable
    public Integer getRecUserId() {
        return recUserId;
    }

    @Nullable
    public String getRecDate() {
        return recDate;
    }

    @Nullable
    public String getRecType() {
        return recType;
    }

    @Nullable
    public Integer getLevel() {
        return level;
    }

    @Nullable
    public Integer getLocationRootId() {
        return locationRootId;
    }

    @Nullable
    public Integer getLocationMunicId() {
        return locationMunicId;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", locationCode='" + locationCode + '\'' +
                ", locationName='" + locationName + '\'' +
                ", locationTypeId=" + locationTypeId +
                ", urbanTypeId=" + urbanTypeId +
                ", inactive=" + inactive +
                ", recUserId=" + recUserId +
                ", recDate='" + recDate + '\'' +
                ", recType='" + recType + '\'' +
                ", level=" + level +
                ", locationRootId=" + locationRootId +
                ", locationMunicId=" + locationMunicId +
                '}';
    }
}


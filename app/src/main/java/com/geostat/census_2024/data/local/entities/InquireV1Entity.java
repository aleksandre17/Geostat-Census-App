package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.geostat.census_2024.data.local.modificator.DateInserter;

import java.util.Date;

@Entity(tableName = "addressings", indices = { @Index(value = "id", unique = true) })
public class InquireV1Entity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @NonNull Integer index;

    @NonNull
    private String uuid;

    @ColumnInfo(name = "house_code")
    @NonNull private String houseCode;

    @NonNull
    private Integer regionId;
    @NonNull
    private Integer municipalId;

    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer cityId;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer unityId;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer villageId;

    @Nullable
    @ColumnInfo(defaultValue = "")
    private String district;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String mr;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String quarter;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String street;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String building;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String corpus;

    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer buildingType;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer flatNum;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer livingStatus;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private String institutionName;
    @Nullable
    @ColumnInfo(defaultValue = "")
    private Integer institutionSpaceNum;

    @Nullable
    @ColumnInfo(defaultValue = "")
    private String comment;

    @ColumnInfo(name = "user_id")
    private Integer userId;

    @ColumnInfo(name = "status", defaultValue = "1")
    @NonNull private Integer status;

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateInserter.class)
    @Nullable private Date createdAt;

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateInserter.class)
    @Nullable private Date updatedAt;

    @Nullable
    private String rollbackComment;

    public InquireV1Entity(@NonNull Integer regionId, @NonNull Integer municipalId, @Nullable Integer cityId, @Nullable Integer unityId, @Nullable Integer villageId, @Nullable String district, @Nullable String mr, @Nullable String quarter, @Nullable String street, @Nullable String building, @Nullable String corpus, @Nullable Integer flatNum, @Nullable Integer buildingType, @Nullable String institutionName, @Nullable Integer institutionSpaceNum, @Nullable Integer livingStatus, @Nullable String comment) {
        this.regionId = regionId;
        this.municipalId = municipalId;
        this.cityId = cityId;
        this.unityId = unityId;
        this.villageId = villageId;
        this.district = district;
        this.mr = mr;
        this.quarter = quarter;
        this.street = street;
        this.building = building;
        this.corpus = corpus;
        this.flatNum = flatNum;
        this.buildingType = buildingType;
        this.institutionName = institutionName;
        this.comment = comment;
        this.institutionSpaceNum = institutionSpaceNum;
        this.livingStatus = livingStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(@NonNull String houseCode) {
        this.houseCode = houseCode;
    }

    @NonNull
    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(@NonNull Integer regionId) {
        this.regionId = regionId;
    }

    @NonNull
    public Integer getMunicipalId() {
        return municipalId;
    }

    public void setMunicipalId(@NonNull Integer municipalId) {
        this.municipalId = municipalId;
    }

    @Nullable
    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(@Nullable Integer cityId) {
        this.cityId = cityId;
    }

    @Nullable
    public Integer getUnityId() {
        return unityId;
    }

    public void setUnityId(@Nullable Integer unityId) {
        this.unityId = unityId;
    }

    @Nullable
    public Integer getVillageId() {
        return villageId;
    }

    public void setVillageId(@Nullable Integer villageId) {
        this.villageId = villageId;
    }

    @Nullable
    public String getDistrict() {
        return district;
    }

    public void setDistrict(@Nullable String district) {
        this.district = district;
    }

    @Nullable
    public String getMr() {
        return mr;
    }

    public void setMr(@Nullable String mr) {
        this.mr = mr;
    }

    @Nullable
    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(@Nullable String quarter) {
        this.quarter = quarter;
    }

    @Nullable
    public String getStreet() {
        return street;
    }

    public void setStreet(@Nullable String street) {
        this.street = street;
    }

    @Nullable
    public String getBuilding() {
        return building;
    }

    public void setBuilding(@Nullable String building) {
        this.building = building;
    }

    @Nullable
    public String getCorpus() {
        return corpus;
    }

    public void setCorpus(@Nullable String corpus) {
        this.corpus = corpus;
    }

    @Nullable
    public Integer getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(@Nullable Integer buildingType) {
        this.buildingType = buildingType;
    }

    @Nullable
    public Integer getFlatNum() {
        return flatNum;
    }

    public void setFlatNum(@Nullable Integer flatNum) {
        this.flatNum = flatNum;
    }

    @Nullable
    public Integer getLivingStatus() {
        return livingStatus;
    }

    public void setLivingStatus(@Nullable Integer livingStatus) {
        this.livingStatus = livingStatus;
    }

    @Nullable
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(@Nullable String institutionName) {
        this.institutionName = institutionName;
    }

    @Nullable
    public Integer getInstitutionSpaceNum() {
        return institutionSpaceNum;
    }

    public void setInstitutionSpaceNum(@Nullable Integer institutionSpaceNum) {
        this.institutionSpaceNum = institutionSpaceNum;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Integer getStatus() {
        return status;
    }

    public void setStatus(@NonNull Integer status) {
        this.status = status;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Nullable
    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    public Integer getIndex() {
        return index;
    }

    public void setIndex(@NonNull Integer index) {
        this.index = index;
    }

    @Nullable
    public String getRollbackComment() {
        return rollbackComment;
    }

    public void setRollbackComment(@Nullable String rollbackComment) {
        this.rollbackComment = rollbackComment;
    }

    @Override
    public String toString() {
        return "Addressing{" +
                "id=" + id +
                ", regionId=" + regionId +
                ", municipalId=" + municipalId +
                ", cityId=" + cityId +
                ", unityId=" + unityId +
                ", villageId=" + villageId +
                ", district='" + district + '\'' +
                ", mr='" + mr + '\'' +
                ", quarter='" + quarter + '\'' +
                ", street='" + street + '\'' +
                ", building='" + building + '\'' +
                ", corpus='" + corpus + '\'' +
                ", buildingType=" + buildingType +
                ", flatNum=" + flatNum +
                ", livingStatus=" + livingStatus +
                ", institutionName='" + institutionName + '\'' +
                ", institutionSpaceNum=" + institutionSpaceNum +
                '}';
    }
}

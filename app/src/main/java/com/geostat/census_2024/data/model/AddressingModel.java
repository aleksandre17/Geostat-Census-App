package com.geostat.census_2024.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.room.TypeConverters;

import com.geostat.census_2024.BR;
import com.geostat.census_2024.data.local.modificator.DateInserter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddressingModel extends BaseObservable implements Serializable {

    private Integer id;

    @NonNull
    private String houseCode;

    @NonNull Integer index;

    private String uuid;

    private Integer regionId;
    private Integer municipalId;

    @Nullable
    private Integer cityId;
    @Nullable
    private Integer unityId;
    @Nullable
    private Integer villageId;

    @Nullable
    private String district;
    @Nullable
    private String mr;
    @Nullable
    private String quarter;
    @Nullable
    private String street;
    @Nullable
    private String building;
    @Nullable
    private String corpus;

    @Nullable
    private Integer buildingType;
    @Nullable
    private Integer flatNum;
    @Nullable
    private Integer livingStatus;
    @Nullable
    private String institutionName;
    @Nullable
    private Integer institutionSpaceNum;

    @Nullable
    private String comment;

    private Integer userId;

    @TypeConverters(DateInserter.class)
    @Nullable private Date createdAt;

    @TypeConverters(DateInserter.class)
    @Nullable private Date updatedAt;

    @Nullable
    private String rollbackComment;

    private ObservableList<HouseHoldModel> houseHold;

    public AddressingModel(Integer id, Integer regionId, Integer municipalId, @Nullable Integer cityId, @Nullable Integer unityId, @Nullable Integer villageId, @Nullable String district, @Nullable String mr, @Nullable String quarter, @Nullable String street, @Nullable String building, @Nullable String corpus, @Nullable Integer flatNum, @Nullable Integer buildingType, @Nullable String institutionName, @Nullable Integer institutionSpaceNum, @Nullable Integer livingStatus, @Nullable String comment) {
        this.id = id;
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

    public AddressingModel() {
    }

    @Bindable
    public String getCorpus() {
        return corpus;
    }

    public void setCorpus(String corpus) {
        if (corpus.isEmpty() && this.corpus != null && !this.corpus.isEmpty()){
            this.corpus = null;
            notifyPropertyChanged(BR.corpus);
        } else if (corpus.isEmpty()) {
            this.corpus = null;
        } else {
            if (!Objects.equals(this.corpus, corpus)) {
                this.corpus = corpus;
                notifyPropertyChanged(BR.corpus);
            }
        }
    }

    @Bindable
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public String getHouseCode() {
        return houseCode;
    }

    public void setHouseCode(@NonNull String houseCode) {
        this.houseCode = houseCode;
    }

    public void setHouseHold(ObservableList<HouseHoldModel> houseHold) {
        this.houseHold = houseHold;
    }

    @Bindable
    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        if (regionId == null && this.regionId != null && this.regionId != 0){
            this.regionId = null;
            notifyPropertyChanged(BR.regionId);
        } else if (regionId == null || regionId == 0) {
            this.regionId = null;
        } else {

            if (!Objects.equals(this.regionId, regionId)) {
                this.setMunicipalId(null);
                this.setCityId(null);
                this.setUnityId(null);
                this.setVillageId(null);

                this.regionId = regionId;
                notifyPropertyChanged(BR.regionId);
            }
        }
    }

    @Bindable
    public Integer getMunicipalId() {
        return municipalId;
    }

    public void setMunicipalId(Integer municipalId) {
        if (municipalId == null && this.municipalId != null && this.municipalId != 0){
            this.municipalId = null;
            notifyPropertyChanged(BR.municipalId);
        } else if (municipalId == null || municipalId == 0) {
            this.municipalId = null;
        } else {

            if (!Objects.equals(this.municipalId, municipalId)) {

                this.setCityId(null);
                this.setUnityId(null);
                this.setVillageId(null);

                this.municipalId = municipalId;
                notifyPropertyChanged(BR.municipalId);


            }
        }
    }

    @Bindable
    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        if (cityId == null && this.cityId != null && this.cityId != 0){
            this.cityId = null;
            notifyPropertyChanged(BR.cityId);
        } else if (cityId == null || cityId == 0) {
            this.cityId = null;
        } else {

            if (!Objects.equals(this.cityId, cityId)) {

                this.setUnityId(null);
                this.setVillageId(null);


                this.cityId = cityId;
                notifyPropertyChanged(BR.cityId);
            }
        }
    }

    @Bindable
    public Integer getUnityId() {
        return unityId;
    }

    public void setUnityId(Integer unityId) {
        if (unityId == null && this.unityId != null && this.unityId != 0){
            this.unityId = null;
            notifyPropertyChanged(BR.unityId);
        } else if (unityId == null || unityId == 0) {
            this.unityId = null;
        } else {

            if (!Objects.equals(this.unityId, unityId)) {
                this.setVillageId(null);

                this.unityId = unityId;
                notifyPropertyChanged(BR.unityId);
            }

        }
    }

    @Bindable
    public Integer getVillageId() {
        return villageId;
    }

    public void setVillageId(Integer villageId) {
        if (villageId == null && this.villageId != null && this.villageId != 0){
            this.villageId = null;
            notifyPropertyChanged(BR.villageId);
        } else if (villageId == null || villageId == 0) {
            this.villageId = null;
        } else {
            if (!Objects.equals(this.villageId, villageId)) {
                this.villageId = villageId;
                notifyPropertyChanged(BR.villageId);
            }
        }
    }

    @Nullable
    @Bindable
    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        if (district.isEmpty() && this.district != null && !this.district.isEmpty()){
            this.district = null;
            notifyPropertyChanged(BR.district);
        } else if (district.isEmpty()) {
            this.district = null;
        } else {
            this.district = district;
            notifyPropertyChanged(BR.district);
        }
    }

    @Nullable
    @Bindable
    public String getMr() {
        return mr;
    }

    public void setMr(String mr) {
        if (mr.isEmpty() && this.mr != null && !this.mr.isEmpty()){
            this.mr = null;
            notifyPropertyChanged(BR.mr);
        } else if (mr.isEmpty()) {
            this.mr = null;
        } else {
            this.mr = mr;
            notifyPropertyChanged(BR.mr);
        }
    }

    @Nullable
    @Bindable
    public String getQuarter() {
        return quarter;
    }

    public void setQuarter(String quarter) {
        if (quarter.isEmpty() && this.quarter != null && !this.quarter.isEmpty()){
            this.quarter = null;
            notifyPropertyChanged(BR.quarter);
        } else if (quarter.isEmpty()) {
            this.quarter = null;
        } else {
            this.quarter = quarter;
            notifyPropertyChanged(BR.quarter);
        }
    }

    @Nullable
    @Bindable
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        if (street.isEmpty() && this.street != null && !this.street.isEmpty()){
            this.street = null;
            notifyPropertyChanged(BR.street);
        } else if (street.isEmpty()) {
            this.street = null;
        } else {
            this.street = street;
            notifyPropertyChanged(BR.street);
        }
    }

    @Bindable
    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
        notifyPropertyChanged(BR.building);
    }

    @Nullable
    @Bindable
    public Integer getFlatNum() {
        return flatNum;
    }

    public void setFlatNum(Integer flatNum) {
        if (flatNum == null && this.flatNum != null && this.flatNum != 0){
            this.flatNum = null;
            notifyPropertyChanged(BR.flatNum);
        } else if (flatNum == null || flatNum == 0) {
            this.flatNum = null;
        } else {
            this.flatNum = flatNum;
            notifyPropertyChanged(BR.flatNum);
        }
    }

    @Nullable
    @Bindable
    public Integer getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Integer buildingType) {
        if (buildingType == null && this.buildingType != null && this.buildingType != 0){
            this.buildingType = null;
            notifyPropertyChanged(BR.buildingType);
        } else if (buildingType == null || buildingType == 0) {
            this.buildingType = null;
        } else {
            this.buildingType = buildingType;
            notifyPropertyChanged(BR.buildingType);
        }

        setLivingStatus(null);
        setFlatNum(null);
        setInstitutionName(null);
        setInstitutionSpaceNum(null);

        if (Arrays.asList(8, 3, 5).contains(buildingType)) {
            setLivingStatus(1);

            HouseHoldModel m = new HouseHoldModel();
            m.setId(0);
            setHouseHold(0, m);

        } else {
            setLivingStatus(null);
            this.houseHold = null;
        }


//        if (Arrays.asList(1, 2, 5, 6, 3).contains(buildingType)) {
//            setInstitutionName(null);
//            setInstitutionSpaceNum(null);
//            setFlatNum(null);
//        }
//
//        if (Arrays.asList(5, 6, 4, 3).contains(buildingType)) {
//           setFlatNum(null);
//        }
//
//        if (buildingType.equals(7) || buildingType.equals(8)) {
//            setHouseHoldS(new ArrayList<>());
//        }
    }

    @Nullable
    @Bindable
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(@Nullable String institutionName) {

        if ((institutionName == null || institutionName.isEmpty()) && this.institutionName != null && !this.institutionName.isEmpty()){
            this.institutionName = null;
            notifyPropertyChanged(BR.institutionName);
        } else if (institutionName == null || institutionName.isEmpty()) {
            this.institutionName = null;
        } else {
            this.institutionName = institutionName;
            notifyPropertyChanged(BR.institutionName);
        }

        if (Objects.equals(getBuildingType(), 8)) {
            if (institutionName != null) {
                setLivingStatus(1);
            } else {
                setLivingStatus(null);
            }
        }
    }

    @Nullable
    @Bindable
    public Integer getInstitutionSpaceNum() {
        return institutionSpaceNum;
    }

    public void setInstitutionSpaceNum(@Nullable Integer institutionSpaceNum) {

        if (institutionSpaceNum == null && this.institutionSpaceNum != null && this.institutionSpaceNum != 0){
            this.institutionSpaceNum = null;
            notifyPropertyChanged(BR.institutionSpaceNum);
        } else if (institutionSpaceNum == null || institutionSpaceNum == 0) {
            this.institutionSpaceNum = null;
        } else {
            this.institutionSpaceNum = institutionSpaceNum;
            notifyPropertyChanged(BR.institutionSpaceNum);
        }

        //        this.institutionSpaceNum = institutionSpaceNum;
        //        notifyPropertyChanged(BR.institutionSpaceNum);
    }

    @Nullable
    @Bindable
    public Integer getLivingStatus() {
        return livingStatus;
    }

    public void setLivingStatus(Integer livingStatus) {
        if (livingStatus == null && this.livingStatus != null && this.livingStatus != 0){
            this.livingStatus = null;
            notifyPropertyChanged(BR.livingStatus);
        } else if (livingStatus == null || livingStatus == 0) {
            this.livingStatus = null;
        } else {
            this.livingStatus = livingStatus;
            notifyPropertyChanged(BR.livingStatus);
        }

        if (Arrays.asList(1, 2).contains(getBuildingType()) && getHouseHold().isEmpty() && Objects.equals(getLivingStatus(), 1)) {

            HouseHoldModel m = new HouseHoldModel();
            m.setId(0);
            setHouseHold(0, m);
        } else {
            this.houseHold = null;
        }
    }

    @Bindable
    public ObservableList<HouseHoldModel> getHouseHold() {
        if (houseHold == null) {
            houseHold = new ObservableArrayList<>();
        }
        return houseHold;
    }

    public void setHouseHoldS (List<HouseHoldModel> holdModels) {
        this.houseHold = new ObservableArrayList<>();
        houseHold.addAll(holdModels);
    }

    public void setHouseHold(int index, HouseHoldModel m) {
        if (this.houseHold == null) {
            this.houseHold = new ObservableArrayList<>();
        }

        if (houseHold.size() > 0) {
            this.houseHold.set(index, m);
        } else {
            this.houseHold.add(index, m);
        }

        notifyPropertyChanged(BR.houseHold);
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    public void remItem(int index) {

        this.houseHold.remove(this.houseHold.get(index));
        notifyPropertyChanged(BR.houseHold);
    }

    public void trigger (int index, HouseHoldModel m) {

        ///
        if ((Objects.equals(getBuildingType(), 7) || Objects.equals(getBuildingType(), 8)) && Objects.equals(getLivingStatus(), null)) {
            setLivingStatus(1);
        }

        if (this.houseHold == null) {
            this.houseHold = new ObservableArrayList<>();
            notifyPropertyChanged(BR.houseHold);
        }

        this.houseHold.add(index, m);
    }

    @Nullable
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable Date createdAt) {
        this.createdAt = createdAt;
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
        return "AddressingModel{" +
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
                ", flatNum=" + flatNum +
                ", buildingType=" + buildingType +
                ", institutionName='" + institutionName + '\'' +
                ", institutionSpaceNum=" + institutionSpaceNum +
                ", livingStatus=" + livingStatus +
                ", houseHold=" + houseHold +
                '}';
    }
}

package com.geostat.census_2024.ui.address;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.geostat.census_2024.data.local.entities.AddressEntity;
import com.geostat.census_2024.data.local.entities.BuildingTypeEntity;
import com.geostat.census_2024.data.local.entities.LivingStatusEntity;
import com.geostat.census_2024.data.local.entities.TagEntity;
import com.geostat.census_2024.data.repository.AddressRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddressViewModel extends AndroidViewModel{

    AddressRepository addressRepository;

    private final LiveData<List<AddressEntity>> addresses;

    private List<BuildingTypeEntity> buildingTypeEntities;
    private List<LivingStatusEntity> livingStatusEntities;
    private List<TagEntity> tagEntities;


    public AddressViewModel(@NonNull Application application) {
        super(application);

        addressRepository = new AddressRepository(application);

        addresses = addressRepository.getAllAddress();
    }

    public AddressEntity findMunicipalityByLocationCode (String locationCode) {
        AddressEntity addressEntity = null;
        try {
            addressEntity = addressRepository.findMunicipalityByLocationCode(locationCode);
        } catch (Exception e) {
            e.getMessage();
        }
        return addressEntity;
    }

    public LiveData<List<AddressEntity>> getAddresses() {
        return addresses;
    }

    public LiveData<List<AddressEntity>> getRegions(int parentId, int length) {
        return addressRepository.getRegions(parentId, length);
    }

    public AddressEntity getAddressById(int id) throws ExecutionException, InterruptedException {
        return addressRepository.getAddressById(id);
    }


    public LiveData<List<AddressEntity>> getRegionsAlter(int parentId, int urbanTypeId) {
        return addressRepository.getRegionsAlter(parentId, urbanTypeId);
    }

    public List<BuildingTypeEntity> getAllBuildingType () throws ExecutionException, InterruptedException {
        return addressRepository.getAllBuildingType();
    }

    public List<LivingStatusEntity> getAllLivingStatus() throws ExecutionException, InterruptedException {
        return addressRepository.getAllLivingStatus();
    }

    public void setBuildingTypes(List<BuildingTypeEntity> buildingTypeEntities) {
        this.buildingTypeEntities = buildingTypeEntities;
    }

    public void setLivingStatuses(List<LivingStatusEntity> livingStatusEntities) {
        this.livingStatusEntities = livingStatusEntities;
    }

    public void setTags (List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
    }

    public void setTag (TagEntity tagEntity, int type) {
        this.addressRepository.setTag(tagEntity, type);
    }

    public List<TagEntity> getTags() throws ExecutionException, InterruptedException {
        return addressRepository.getTags();
    }

    public int removeTag(TagEntity tagEntity) throws ExecutionException, InterruptedException {
        return addressRepository.removeTag(tagEntity);
    }
}

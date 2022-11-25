package com.geostat.census_2024.ui.address;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.LivingStatus;
import com.geostat.census_2024.data.local.entities.Tag;
import com.geostat.census_2024.data.repository.AddressRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddressViewModel extends AndroidViewModel{

    AddressRepository addressRepository;

    private final LiveData<List<Address>> addresses;

    private List<BuildingType> buildingTypes;
    private List<LivingStatus> livingStatuses;
    private List<Tag> tags;


    public AddressViewModel(@NonNull Application application) {
        super(application);

        addressRepository = new AddressRepository(application);

        addresses = addressRepository.getAllAddress();
    }

    public Address findMunicipalityByLocationCode (String locationCode) {
        Address address = null;
        try {
            address = addressRepository.findMunicipalityByLocationCode(locationCode);
        } catch (Exception e) {
            e.getMessage();
        }
        return address;
    }

    public LiveData<List<Address>> getAddresses() {
        return addresses;
    }

    public LiveData<List<Address>> getRegions(int parentId, int length) {
        return addressRepository.getRegions(parentId, length);
    }

    public Address getAddressById(int id) throws ExecutionException, InterruptedException {
        return addressRepository.getAddressById(id);
    }


    public LiveData<List<Address>> getRegionsAlter(int parentId, int urbanTypeId) {
        return addressRepository.getRegionsAlter(parentId, urbanTypeId);
    }

    public List<BuildingType> getAllBuildingType () throws ExecutionException, InterruptedException {
        return addressRepository.getAllBuildingType();
    }

    public List<LivingStatus> getAllLivingStatus() throws ExecutionException, InterruptedException {
        return addressRepository.getAllLivingStatus();
    }

    public void setBuildingTypes(List<BuildingType> buildingTypes) {
        this.buildingTypes = buildingTypes;
    }

    public void setLivingStatuses(List<LivingStatus> livingStatuses) {
        this.livingStatuses = livingStatuses;
    }

    public void setTags (List<Tag> tags) {
        this.tags = tags;
    }

    public void setTag (Tag tag, int type) {
        this.addressRepository.setTag(tag, type);
    }

    public List<Tag> getTags() throws ExecutionException, InterruptedException {
        return addressRepository.getTags();
    }

    public int removeTag(Tag tag) throws ExecutionException, InterruptedException {
        return addressRepository.removeTag(tag);
    }
}

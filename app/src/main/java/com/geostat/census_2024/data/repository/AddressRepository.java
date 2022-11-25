package com.geostat.census_2024.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.AddressDao;
import com.geostat.census_2024.data.local.dai.BuildingTypeDao;
import com.geostat.census_2024.data.local.dai.LivingStatusDao;
import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.LivingStatus;
import com.geostat.census_2024.data.local.entities.Tag;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class AddressRepository {

    public CensusDatabase db;
    private AddressDao addressDao;
    private BuildingTypeDao buildingTypeDao;
    private LivingStatusDao livingStatusDao;
    private LiveData<List<Address>> allAddress;

    public AddressRepository(Application application) {
        db = CensusDatabase.getDatabase(application);

        addressDao = db.addressDao();
        allAddress = addressDao.getAllAddress();

        buildingTypeDao = db.buildingTypeDao();
        livingStatusDao = db.livingStatusDao();

    }

    public Address findMunicipalityByLocationCode(String locationCode) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<Address>() {
            @Override
            public Address call() throws Exception {
                return addressDao.findMunicipalityByLocationCode(locationCode);
            }
        }).get();
    }

    public LiveData<List<Address>> getAllAddress() {
        return allAddress;
    }

    public LiveData<List<Address>> getRegions(int parentId, int length) {
        return addressDao.getRegions(parentId, length);
    }


    public LiveData<List<Address>> getRegionsAlter(int parentId, int urbanTypeId) {
        return addressDao.getRegionsAlter(parentId, urbanTypeId);
    }

    public Address getAddressById(int id) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<Address>() {
            @Override
            public Address call() throws Exception {
                return addressDao.getAddressById(id);
            }
        }).get();
    }


    public void deleteAddress(Address address) {
        db.censusWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                addressDao.deleteAddress(address.getId());
            }
        });
    }


    public List<BuildingType> getAllBuildingType () throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<BuildingType>>() {
            @Override
            public List<BuildingType> call() throws Exception {
                return buildingTypeDao.getAllBuildingTypes();
            }
        }).get();
    }

    public List<LivingStatus> getAllLivingStatus() throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<LivingStatus>>(){

            @Override
            public List<LivingStatus> call() throws Exception {
                return livingStatusDao.getAllBuildingTypes();
            }
        }).get();
    }


    public List<Tag> getTags() throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<Tag>>(){

            @Override
            public List<Tag> call() throws Exception {
                return addressDao.getTags();
            }
        }).get();
    }

    public void setTag(Tag tag, int type) {
        db.censusWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                tag.setType(type);
                addressDao.insertTag(tag);
            }
        });
    }

    public int removeTag(Tag tag) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return addressDao.deleteTag(tag.getId());
            }
        }).get();
    }
}

package com.geostat.census_2024.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.AddressDao;
import com.geostat.census_2024.data.local.dai.BuildingTypeDao;
import com.geostat.census_2024.data.local.dai.LivingStatusDao;
import com.geostat.census_2024.data.local.entities.AddressEntity;
import com.geostat.census_2024.data.local.entities.BuildingTypeEntity;
import com.geostat.census_2024.data.local.entities.LivingStatusEntity;
import com.geostat.census_2024.data.local.entities.TagEntity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class AddressRepository {

    public CensusDatabase db;
    private AddressDao addressDao;
    private BuildingTypeDao buildingTypeDao;
    private LivingStatusDao livingStatusDao;
    private LiveData<List<AddressEntity>> allAddress;

    public AddressRepository(Application application) {
        db = CensusDatabase.getDatabase(application);

        addressDao = db.addressDao();
        allAddress = addressDao.getAllAddress();

        buildingTypeDao = db.buildingTypeDao();
        livingStatusDao = db.livingStatusDao();

    }

    public AddressEntity findMunicipalityByLocationCode(String locationCode) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<AddressEntity>() {
            @Override
            public AddressEntity call() throws Exception {
                return addressDao.findMunicipalityByLocationCode(locationCode);
            }
        }).get();
    }

    public LiveData<List<AddressEntity>> getAllAddress() {
        return allAddress;
    }

    public LiveData<List<AddressEntity>> getRegions(int parentId, int length) {
        return addressDao.getRegions(parentId, length);
    }


    public LiveData<List<AddressEntity>> getRegionsAlter(int parentId, int urbanTypeId) {
        return addressDao.getRegionsAlter(parentId, urbanTypeId);
    }

    public AddressEntity getAddressById(int id) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<AddressEntity>() {
            @Override
            public AddressEntity call() throws Exception {
                return addressDao.getAddressById(id);
            }
        }).get();
    }


    public void deleteAddress(AddressEntity addressEntity) {
        db.censusWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                addressDao.deleteAddress(addressEntity.getId());
            }
        });
    }


    public List<BuildingTypeEntity> getAllBuildingType () throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<BuildingTypeEntity>>() {
            @Override
            public List<BuildingTypeEntity> call() throws Exception {
                return buildingTypeDao.getAllBuildingTypes();
            }
        }).get();
    }

    public List<LivingStatusEntity> getAllLivingStatus() throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<LivingStatusEntity>>(){

            @Override
            public List<LivingStatusEntity> call() throws Exception {
                return livingStatusDao.getAllBuildingTypes();
            }
        }).get();
    }


    public List<TagEntity> getTags() throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<List<TagEntity>>(){

            @Override
            public List<TagEntity> call() throws Exception {
                return addressDao.getTags();
            }
        }).get();
    }

    public void setTag(TagEntity tagEntity, int type) {
        db.censusWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                tagEntity.setType(type);
                addressDao.insertTag(tagEntity);
            }
        });
    }

    public int removeTag(TagEntity tagEntity) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return addressDao.deleteTag(tagEntity.getId());
            }
        }).get();
    }
}

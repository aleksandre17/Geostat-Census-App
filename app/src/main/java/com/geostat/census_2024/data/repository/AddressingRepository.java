package com.geostat.census_2024.data.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.room.Transaction;

import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.AddressingDao;
import com.geostat.census_2024.data.local.entities.InquireActivityV1DateStatusEntity;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.entities.InquireV1HolderEntity;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AddressingRepository {

    public CensusDatabase db;
    private final AddressingDao addressingDao;

    public AddressingRepository(Application application) {
        db = CensusDatabase.getDatabase(application);

        addressingDao = db.addressingDao();
    }

    @Transaction
    public Long insertAddressingWithHolders(InquireV1Entity inquireV1Entity, List<InquireV1HolderEntity> inquireV1HolderEntities, @Nullable SupervisionEntity supervisionEntity) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> db.runInTransaction(() -> {
            Long insertAddressing = addressingDao.insertAddressing(inquireV1Entity);
            inquireV1HolderEntities.parallelStream().forEach(holder -> holder.setAddressingId(Math.toIntExact(insertAddressing)));
            addressingDao.insertHolders(inquireV1HolderEntities);
            if (supervisionEntity != null) {
                supervisionEntity.setAddressingId(Math.toIntExact(insertAddressing));
                insertSupervision(supervisionEntity);
            }
            addressingDao.insertAddressingDateStatuses(new InquireActivityV1DateStatusEntity(Math.toIntExact(insertAddressing), 1, new Date()));
            return insertAddressing;
        })).get();
    }

    @Transaction
    public void insertSupervision(SupervisionEntity supervisionEntity) {
        db.runInTransaction(() -> addressingDao.insertSupervision(supervisionEntity));
    }

    public Integer updateAddressingStatus(List<String> houseUuids, final int setStatus, @Nullable List<InquireV1Entity> inquireV1Entities) {
        return db.runInTransaction(() -> {
            if (setStatus == 2 && inquireV1Entities != null) inquireV1Entities.forEach(addressing -> addressingDao.updateAddressingRollbackComment(addressing.getRollbackComment(), addressing.getUuid()));
            return addressingDao.updateAddressingStatus(houseUuids, setStatus);
        });
    }

    public void removeAddressing(List<String> uuids) {
        db.runInTransaction(() -> addressingDao.removeAddressing(uuids));
    }

    public List<AddressingWithHolders> fetchByHouseCodes(List<String> houseCodes) {
        List<AddressingWithHolders> addressing = null;

//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        ScheduledFuture<List<AddressingWithHolders>> scheduledFuture = executorService.schedule(new Callable<List<AddressingWithHolders>>() {
//
//            @Override
//            public List<AddressingWithHolders> call() throws Exception {
//                return addressingDao.fetch();
//            }
//        }, 10, TimeUnit.SECONDS);

        try {
            Future<List<AddressingWithHolders>> fetch = Executors.newSingleThreadExecutor().submit(() -> addressingDao.fetchByHouseCodes(houseCodes));
            addressing = fetch.get();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return addressing;
    }

    public List<AddressingWithHolders> fetch (String id) {

        List<AddressingWithHolders> addressing = null;

//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        ScheduledFuture<List<AddressingWithHolders>> scheduledFuture = executorService.schedule(new Callable<List<AddressingWithHolders>>() {
//
//            @Override
//            public List<AddressingWithHolders> call() throws Exception {
//                return addressingDao.fetch();
//            }
//        }, 10, TimeUnit.SECONDS);

        try {
            Future<List<AddressingWithHolders>> fetch = Executors.newSingleThreadExecutor().submit(() -> addressingDao.fetch(id));
            addressing = fetch.get();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return addressing;
    }


    public AddressingWithHolders getAddressingById (Integer id) throws ExecutionException, InterruptedException {

        Future<AddressingWithHolders> fetch = Executors.newSingleThreadExecutor().submit(() -> addressingDao.getAddressingById(id));

        return fetch.get();
    }

    public List<AddressingWithHolders> fetchAll () {
        List<AddressingWithHolders> addressing = null;

        try {
            Future<List<AddressingWithHolders>> fetch = Executors.newSingleThreadExecutor().submit(addressingDao::fetchAll);
            addressing = fetch.get();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return addressing;
    }


    public AddressingWithHolders getById (int id) throws ExecutionException, InterruptedException {

        Future<AddressingWithHolders> fetch = Executors.newSingleThreadExecutor().submit(new Callable<AddressingWithHolders>() {

            @Override
            public AddressingWithHolders call() throws Exception {
                return addressingDao.getById(id);
            }
        });

        return fetch.get();
    }

    public Integer removeAddress(int id) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return addressingDao.deleteAddressById(id);
            }
        }).get();
    }


    public List<AddressingWithHolders> getRollbackAddressings () throws ExecutionException, InterruptedException {
        Future<List<AddressingWithHolders>> fetch = Executors.newSingleThreadExecutor().submit(new Callable<List<AddressingWithHolders>>() {

            @Override
            public List<AddressingWithHolders> call() throws Exception {
                return addressingDao.fetchRollbackAddressings();
            }
        });

        return fetch.get();
    }


    public InquireV1Entity findNewestR(String id) {
        InquireV1Entity inquireV1Entity = null;
        try {
            inquireV1Entity = db.censusWriterExecutor.submit(new Callable<InquireV1Entity>() {

                @Override
                public InquireV1Entity call() throws Exception {
                    return addressingDao.findNewestR(id);
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return inquireV1Entity;
    }

}

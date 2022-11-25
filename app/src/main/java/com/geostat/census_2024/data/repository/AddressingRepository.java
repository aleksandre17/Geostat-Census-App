package com.geostat.census_2024.data.repository;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.room.Transaction;

import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.AddressingDao;
import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.local.entities.Holder;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;

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
    public Long insertAddressingWithHolders(Addressing addressing, List<Holder> holders, @Nullable SupervisionEntity supervisionEntity) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> db.runInTransaction(() -> {
            Long insertAddressing = addressingDao.insertAddressing(addressing);
            holders.parallelStream().forEach(holder -> holder.setAddressingId(Math.toIntExact(insertAddressing)));
            addressingDao.insertHolders(holders);
            if (supervisionEntity != null) {
                supervisionEntity.setAddressingId(Math.toIntExact(insertAddressing));
                insertSupervision(supervisionEntity);
            }
            return insertAddressing;
        })).get();
    }

    @Transaction
    public void insertSupervision(SupervisionEntity supervisionEntity) {
        db.runInTransaction(() -> addressingDao.insertSupervision(supervisionEntity));
    }

    public Integer updateAddressingStatus(List<Addressing> addressings) {
        return db.runInTransaction(() -> {
            List<String> houseUuids = addressings.stream().filter(addressing -> addressing.getStatus().equals(2)).map(Addressing::getUuid).collect(Collectors.toList());
            addressings.forEach(addressing -> addressingDao.updateAddressingRollbackComment(addressing.getRollbackComment(), addressing.getUuid()));
            return addressingDao.updateAddressingStatus(houseUuids);
        });
    }

    public void removeAddressing(String houseCode) {
        db.runInTransaction(() -> addressingDao.removeAddressing(houseCode));
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


    public Addressing findNewestR(String id) {
        Addressing addressing = null;
        try {
            addressing = db.censusWriterExecutor.submit(new Callable<Addressing>() {

                @Override
                public Addressing call() throws Exception {
                    return addressingDao.findNewestR(id);
                }
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return addressing;
    }

}

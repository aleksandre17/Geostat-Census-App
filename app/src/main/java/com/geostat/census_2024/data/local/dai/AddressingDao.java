package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.geostat.census_2024.data.local.entities.InquireActivityV1DateStatusEntity;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.entities.InquireV1HolderEntity;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;

import java.util.List;

@Dao
public abstract class AddressingDao extends BaseDai<InquireV1Entity> {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertAddressing(InquireV1Entity inquireV1Entity);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertHolders(List<InquireV1HolderEntity> inquireV1HolderEntities);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertSupervision(SupervisionEntity supervisionEntity);


    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertAddressingDateStatuses(InquireActivityV1DateStatusEntity inquireActivityV1DateStatusEntity);

    @Transaction
    @Query("UPDATE addressings SET status = :setStatus where uuid in (:houseCodes)")
    public abstract Integer updateAddressingStatus(List<String> houseCodes, int setStatus);

    @Transaction
    @Query("UPDATE addressings SET rollbackComment = :rollbackComment where uuid = :uuid")
    public abstract void updateAddressingRollbackComment(String rollbackComment, String uuid);

    @Transaction
    @Query("DELETE FROM addressings where uuid in (:uuids)")
    public abstract void removeAddressing(List<String> uuids);

    @Transaction
    @Query("SELECT * FROM addressings where house_code in (:houseCodes)")
    public abstract List<AddressingWithHolders> fetchByHouseCodes(List<String> houseCodes);

    @Transaction
    @Query("SELECT * FROM addressings where house_code = :id order by created_at desc")
    public abstract List<AddressingWithHolders> fetch(String id);

    @Transaction
    @Query("SELECT * FROM addressings order by created_at")
    public abstract List<AddressingWithHolders> fetchAll();

    @Transaction
    @Query("SELECT * FROM addressings where id = :id")
    public abstract AddressingWithHolders getAddressingById(Integer id);

    @Transaction
    @Query("SELECT * FROM addressings where status = 2 order by created_at desc")
    public abstract List<AddressingWithHolders> fetchRollbackAddressings();

    @Transaction
    @Query("SELECT * FROM addressings where id = :id")
    public abstract AddressingWithHolders getById(int id);

    @Query("UPDATE addressings SET status = 5 where id = :id")
    public abstract Integer deleteAddressById(int id);

    @Query("SELECT * from addressings where house_code = :id order by created_at desc LIMIT 1")
    public abstract InquireV1Entity findNewestR(String id);

}

package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.local.entities.Holder;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;

import java.util.List;

@Dao
public abstract class AddressingDao extends BaseDai<Addressing> {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertAddressing(Addressing addressing);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertHolders(List<Holder> holders);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertSupervision(SupervisionEntity supervisionEntity);

    @Transaction
    @Query("UPDATE addressings SET status = 2 where uuid in (:houseCodes)")
    public abstract Integer updateAddressingStatus(List<String> houseCodes);

    @Transaction
    @Query("UPDATE addressings SET rollbackComment = :rollbackComment where uuid = :uuid")
    public abstract void updateAddressingRollbackComment(String rollbackComment, String uuid);

    @Transaction
    @Query("DELETE FROM addressings where house_code = :houseCode")
    public abstract void removeAddressing(String houseCode);

    @Transaction
    @Query("SELECT * FROM addressings where house_code = :id order by created_at desc")
    public abstract List<AddressingWithHolders> fetch(String id);

    @Transaction
    @Query("SELECT * FROM addressings order by created_at")
    public abstract List<AddressingWithHolders> fetchAll();

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
    public abstract Addressing findNewestR(String id);

}

package com.geostat.census_2024.data.local.dai;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.geostat.census_2024.data.local.entities.AddressEntity;
import com.geostat.census_2024.data.local.entities.TagEntity;

import java.util.List;

@Dao
public interface AddressDao {
    @Insert
    void insert(AddressEntity addressEntity);

    @Query("DELETE FROM cl_address WHERE id = :id")
    int deleteAddress(int id);

    @Query("SELECT * FROM cl_address WHERE id = :id")
    AddressEntity getAddressById(int id);

    @Query("SELECT * FROM cl_address WHERE locationCode = :locationCode")
    AddressEntity findMunicipalityByLocationCode(String locationCode);

    @Query("SELECT * FROM cl_address")
    LiveData<List<AddressEntity>> getAllAddress();

    @Query("SELECT * FROM cl_address WHERE parentId = :parentId AND level = :length AND (level > 3 AND IFNULL(urbanTypeId, 2) = 2 OR level < 4)")
    LiveData<List<AddressEntity>> getRegions(int parentId, int length);


    @Query("SELECT * FROM cl_address WHERE locationMunicId = :parentId AND level = 5 AND urbanTypeId in (1, :urbanId) order by urbanTypeId")
    LiveData<List<AddressEntity>> getRegionsAlter(int parentId, int urbanId);

    @Query("SELECT * FROM cl_tags")
    List<TagEntity> getTags ();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTag(TagEntity tagEntity);

    @Query("DELETE FROM cl_tags WHERE id = :id")
    int deleteTag(int id);
}

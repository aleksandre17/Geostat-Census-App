package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.geostat.census_2024.data.local.modificator.DateInserter;

import java.util.Date;

@Entity(
        tableName = "addressing_supervision",
        foreignKeys = {
            @ForeignKey(entity = InquireV1Entity.class, parentColumns = { "id" }, childColumns = { "addressing_id" }, onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE)
        },
        indices = { @Index("addressing_id"), @Index(value = "addressing_uuid", unique = true), @Index(value = "id", unique = true) }
)
public class SupervisionEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @ColumnInfo(name = "addressing_id")
    private Integer addressingId;

    @ColumnInfo(name = "addressing_uuid")
    private String addressing_uuid;

    private Double latitude;

    private Double longitude;

    @ColumnInfo(name = "start_time", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateInserter.class)
    @Nullable private Date startTime;

    @ColumnInfo(name = "end_time", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateInserter.class)
    @Nullable private Date endTime;


    public SupervisionEntity(String addressing_uuid, @Nullable Date startTime, @Nullable Date endTime) {
        this.addressing_uuid = addressing_uuid;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public Integer getAddressingId() {
        return addressingId;
    }

    public String getAddressing_uuid() {
        return addressing_uuid;
    }

    public void setAddressing_uuid(String addressing_uuid) {
        this.addressing_uuid = addressing_uuid;
    }

    public void setAddressingId(Integer addressingId) {
        this.addressingId = addressingId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Nullable
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(@Nullable Date startTime) {
        this.startTime = startTime;
    }

    @Nullable
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(@Nullable Date endTime) {
        this.endTime = endTime;
    }
}

package com.geostat.census_2024.data.local.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Addressing.class, parentColumns = { "id" }, childColumns = { "addressingId" }, onUpdate = ForeignKey.CASCADE, onDelete = ForeignKey.CASCADE) },
        tableName = "addressing_holders", indices = { @Index("addressingId"), @Index(value = "id", unique = true) }
)
public class Holder {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @ColumnInfo(defaultValue = "")
    private Integer addressingId;

    @ColumnInfo(defaultValue = "")
    @Nullable
    private String firstName;

    @ColumnInfo(defaultValue = "")
    @Nullable
    private String lastName;

    @ColumnInfo(defaultValue = "")
    @Nullable
    private String fatherName;

    @ColumnInfo(defaultValue = "")
    private Integer membersNum;

    @ColumnInfo(defaultValue = "")
    @Nullable
    private String mobileNum;


    public Holder(@NonNull String firstName, @NonNull String lastName, @NonNull String fatherName, @NonNull Integer membersNum, @NonNull String mobileNum) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.membersNum = membersNum;
        this.mobileNum = mobileNum;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAddressingId() {
        return addressingId;
    }

    public void setAddressingId(@NonNull Integer addressingId) {
        this.addressingId = addressingId;
    }

    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NonNull String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@NonNull String lastName) {
        this.lastName = lastName;
    }

    @Nullable
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(@NonNull String fatherName) {
        this.fatherName = fatherName;
    }

    @NonNull
    public Integer getMembersNum() {
        return membersNum;
    }

    public void setMembersNum(@NonNull Integer membersNum) {
        this.membersNum = membersNum;
    }

    @Nullable
    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(@NonNull String mobileNum) {
        this.mobileNum = mobileNum;
    }

    @Override
    public String toString() {
        return "Holder{" +
                "id=" + id +
                ", addressingId=" + addressingId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", membersNum=" + membersNum +
                ", mobileNum='" + mobileNum + '\'' +
                '}';
    }
}

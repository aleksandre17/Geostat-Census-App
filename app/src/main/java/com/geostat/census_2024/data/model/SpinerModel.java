package com.geostat.census_2024.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Objects;

public class SpinerModel implements Parcelable {
    private Integer key;
    private String value;

    @Nullable
    private Integer urbanTypeId;
    @Nullable
    private Integer locationTypeId;

    protected SpinerModel(Parcel in) {
        if (in.readByte() == 0) {
            key = null;
        } else {
            key = in.readInt();
        }
        value = in.readString();
        if (in.readByte() == 0) {
            urbanTypeId = null;
        } else {
            urbanTypeId = in.readInt();
        }
        if (in.readByte() == 0) {
            locationTypeId = null;
        } else {
            locationTypeId = in.readInt();
        }
    }

    public static final Creator<SpinerModel> CREATOR = new Creator<SpinerModel>() {
        @Override
        public SpinerModel createFromParcel(Parcel in) {
            return new SpinerModel(in);
        }

        @Override
        public SpinerModel[] newArray(int size) {
            return new SpinerModel[size];
        }
    };

    @Nullable
    public Integer getLocationTypeId() {
        return locationTypeId;
    }

    public void setLocationTypeId(@Nullable Integer locationTypeId) {
        this.locationTypeId = locationTypeId;
    }

    @Nullable
    public Integer getUrbanTypeId() {
        return urbanTypeId;
    }

    public void setUrbanTypeId(@Nullable Integer urbanTypeId) {
        this.urbanTypeId = urbanTypeId;
    }

    public SpinerModel(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object i) {
        if (i instanceof SpinerModel) {
            SpinerModel that = (SpinerModel) i;
            if (that.getValue().equals(value) && that.getKey() == key) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (key == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(key);
        }
        parcel.writeString(value);
        if (urbanTypeId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(urbanTypeId);
        }
        if (locationTypeId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(locationTypeId);
        }
    }
}

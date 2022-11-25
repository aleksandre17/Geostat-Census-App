package com.geostat.census_2024.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class LayerModel implements Parcelable {

    private HashMap<String, Object> properties;

    public LayerModel() {

        properties = new HashMap<>();
    }

    //Create object with properties
    public LayerModel(HashMap<String, Object> properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    protected LayerModel(Parcel in) {
        this.properties = (HashMap<String, Object>) in.readSerializable();
    }

    public static final Creator<LayerModel> CREATOR = new Creator<LayerModel>() {
        @Override
        public LayerModel createFromParcel(Parcel in) {
            return new LayerModel(in);
        }

        @Override
        public LayerModel[] newArray(int size) {
            return new LayerModel[size];
        }
    };

    //Set properties
    public <T> void setProperty(String key, Object value, Class<T> type) {
        this.properties.put(key, type.cast(value.toString())); //Returns old value if existing
    }

    //Get properties
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) this.properties.getOrDefault(key, null);
    }

    public HashMap<String, Object> getAll() {
        return properties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.properties);
    }

    public HashMap<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public String toString() {
        return "LayerModel{" +
                "properties=" + properties.toString() +
                '}';
    }
}

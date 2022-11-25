package com.geostat.census_2024.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class User implements Parcelable {

    private int id;
    private String userName;
    private String password;
    private String token;

    private Object districtNum;
    private String district;

    private final HashMap<String, Object> properties = new HashMap<>();

    public User() {}

    public User(int id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    protected User(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        password = in.readString();
        token = in.readString();
        district = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userName);
        dest.writeString(password);
        dest.writeString(token);
        dest.writeString(district);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setDistrictNum(Object districtNum) {
        this.districtNum = districtNum;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public Object getDistrictNum() {
        return districtNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //Set properties
    public void setProperty(String key, Object value) {
        this.properties.put(key, value);
    }

    //Get properties
    @SuppressWarnings("unchecked")
    public <T> T getProperty(T key) {
        return (T) this.properties.getOrDefault(key.toString(), null);
    }
    public HashMap<String, Object> getProperties() {
        return this.properties;
    }
}

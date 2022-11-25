package com.geostat.census_2024.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.PropertyChangeRegistry;


import com.geostat.census_2024.BR;

import java.util.Objects;

public class HouseHoldModel extends BaseObservable implements Cloneable {

    private Integer id;
    @Nullable
    private String firstName;
    @Nullable
    private String lastName;
    @Nullable
    private String fatherName;
    @Nullable
    private Integer membersNum;
    @Nullable
    private String mobileNum;

    public HouseHoldModel(int id, @Nullable String firstName, @Nullable String lastName, @Nullable String fatherName, @Nullable Integer membersNum, @Nullable String mobileNum) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
        this.membersNum = membersNum;
        this.mobileNum = mobileNum;
    }

    public HouseHoldModel() {
    }

    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.fatherName);
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    @Bindable
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;

        notifyPropertyChanged(BR.fatherName);

    }

    @Bindable
    public Integer getMembersNum() {
        return membersNum;
    }

    public void setMembersNum(Integer membersNum) {
        if (Objects.equals(membersNum, this.membersNum)) return;
        this.membersNum = membersNum;
        notifyPropertyChanged(BR.membersNum);
    }

    @Bindable
    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String  mobileNum) {
        this.mobileNum = mobileNum;
        notifyPropertyChanged(BR.mobileNum);
    }

    @Override
    public String toString() {
        return "HouseHoldModel{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fatherName='" + fatherName + '\'' +
                ", membersNum=" + membersNum +
                ", mobileNum='" + mobileNum + '\'' +
                '}';
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void addOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        if (callbacks.isEmpty()) {
            callbacks.add(callback);
        }
    }

    @Override
    public void removeOnPropertyChangedCallback(@NonNull OnPropertyChangedCallback callback) {
        callbacks.remove(callback);
    }


    public void notifyChange() {
        callbacks.notifyCallbacks(this, 0, null);
    }

    public void notifyPropertyChanged(int fieldId) {
        callbacks.notifyCallbacks(this, fieldId, null);
    }
}

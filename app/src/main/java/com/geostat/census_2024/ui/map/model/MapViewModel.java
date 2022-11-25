package com.geostat.census_2024.ui.map.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.repository.AddressingRepository;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public class MapViewModel extends AndroidViewModel {

    private MutableLiveData<Boolean> isLoadPkg;
    private MutableLiveData<Boolean> isLoadRaster;

    private Geometry mapClickedArea = null;
    private final MutableLiveData<FeatureLayer> touchableLayer = new MutableLiveData<>();
    private Feature selectedFeatureFromMapClickedArea = null;
    private Feature userArea = null;

    AddressingRepository addressingRepository;

    ///
    private UserAreaModel userAreaModel = null;

    public MapViewModel(@NonNull Application application) {
        super(application);

        addressingRepository = new AddressingRepository(application);
    }

    public LiveData<FeatureLayer> getTouchableLayer() {
        return touchableLayer;
    }

    public void setTouchableLayer(FeatureLayer touchableLayer) {
        this.touchableLayer.setValue(touchableLayer);
    }

    public void setTouchableLayer(FeatureLayer touchableLayer, Integer alter) {
        if (alter != null) this.touchableLayer.postValue(touchableLayer);
    }

    public Geometry getMapClickedArea() {
        return mapClickedArea;
    }

    public void setMapClickedArea(Geometry mapClickedArea) {
        this.mapClickedArea = mapClickedArea;
    }

    public Feature getSelectedFeatureFromMapClickedArea() {
        return selectedFeatureFromMapClickedArea;
    }

    public void setSelectedFeatureFromMapClickedArea(Feature selectedFeatureFromMapClickedArea) {
        this.selectedFeatureFromMapClickedArea = selectedFeatureFromMapClickedArea;
    }

    public LiveData<Boolean> getIsLoadRaster() {
        if (isLoadRaster == null) isLoadRaster = new MutableLiveData<>();
        return isLoadRaster;
    }

    public void setIsLoadRaster(Boolean raster) {
        if (isLoadRaster == null) isLoadRaster = new MutableLiveData<>();
        this.isLoadRaster.postValue(raster);
    }

    public LiveData<Boolean> getIsLoadPkg() {
        if (isLoadPkg == null) isLoadPkg = new MutableLiveData<>();
        return isLoadPkg;
    }

    public void setIsLoadPkg(Boolean pkg) {
        if (isLoadPkg == null) isLoadPkg = new MutableLiveData<>();
        this.isLoadPkg.postValue(pkg);
    }

    public Feature getUserArea() {
        return userArea;
    }

    public UserAreaModel getUserAreaModel() {
        return userAreaModel;
    }

    public void setUserAreaModel(UserAreaModel userAreaModel) {
        this.userAreaModel = userAreaModel;
    }

    public void setUserArea(Feature userArea) {
        this.userArea = userArea;

        Map<String, Object> attr = userArea.getAttributes();
        Gson gson = new Gson();

        UserAreaModel userAreaModel = gson.fromJson(gson.toJson(attr), UserAreaModel.class);
        setUserAreaModel(userAreaModel);
    }

    public Integer updateAddressingStatus (List<Addressing> addressings) {
        return addressingRepository.updateAddressingStatus(addressings);
    }

    public void removeAddressing(String houseCode) {
        addressingRepository.removeAddressing(houseCode);
    }
}

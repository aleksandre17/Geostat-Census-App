package com.geostat.census_2024.ui.map.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.repository.AddressingRepository;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MapViewModel extends AndroidViewModel {

    private Geometry mapClickedArea = null;
    private final MutableLiveData<FeatureLayer> touchableLayer = new MutableLiveData<>();
    private Feature selectedFeatureFromMapClickedArea = null;
    private Feature userArea = null;

    private final MutableLiveData<Boolean> isLoadPkg = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadRaster = new MutableLiveData<>(false);

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
        return isLoadRaster;
    }

    public void setIsLoadRaster(Boolean raster) {
        this.isLoadRaster.postValue(raster);
    }

    public LiveData<Boolean> getIsLoadPkg() {
        return isLoadPkg;
    }

    public void setIsLoadPkg(Boolean pkg) {
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

    public Integer updateAddressingStatus (List<InquireV1Entity> inquireV1Entities, int setStatus) {
        List<String> houseUuids = inquireV1Entities.parallelStream().filter(addressing -> addressing.getStatus().equals(2)).map(InquireV1Entity::getUuid).collect(Collectors.toList());
        return addressingRepository.updateAddressingStatus(houseUuids, setStatus, inquireV1Entities);
    }

    public void updateAddressingStatus (Feature feature, int setStatus) throws ExecutionException, InterruptedException {
        List<String> houseUuids = addressingRepository.fetch(Objects.requireNonNull(feature.getAttributes().get("house_code")).toString())
                .parallelStream().map(addressing -> addressing.inquireV1Entity.getUuid()).collect(Collectors.toList());
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> serviceFuture = service.submit(() -> addressingRepository.updateAddressingStatus(houseUuids, setStatus, null));
        serviceFuture.get();
    }

    public void removeAddressing(List<String> houseCodes) {
        List<String> houseUuids = addressingRepository.fetchByHouseCodes(houseCodes).stream().map(addressing -> addressing.inquireV1Entity.getUuid()).collect(Collectors.toList());
        addressingRepository.removeAddressing(houseUuids);
    }
}

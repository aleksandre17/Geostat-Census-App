package com.geostat.census_2024.ui.addressing.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.data.repository.AddressingRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddressingViewModel extends AndroidViewModel {

    AddressingRepository addressingRepository;
    LayerModel layerModel;

    public AddressingViewModel(@NonNull Application application) {
        super(application);

        addressingRepository = new AddressingRepository(application);
    }

    public LayerModel getLayerModel() {
        return layerModel;
    }

    public void setLayerModel(LayerModel layerModel) {
        this.layerModel = layerModel;
    }

    public List<AddressingWithHolders> fetchAddressing(String id) {
        return addressingRepository.fetch(id);
    }

    public AddressingWithHolders getAddressingById(Integer id) throws ExecutionException, InterruptedException {
        return addressingRepository.getAddressingById(id);
    }

    public Integer removeAddress(int id) throws ExecutionException, InterruptedException {
        return addressingRepository.removeAddress(id);
    }

    public List<AddressingWithHolders> getRollbackAddressings() throws ExecutionException, InterruptedException {
        return addressingRepository.getRollbackAddressings();
    }

    public InquireV1Entity findNewestR (String id) {
        return addressingRepository.findNewestR(id);
    }
}

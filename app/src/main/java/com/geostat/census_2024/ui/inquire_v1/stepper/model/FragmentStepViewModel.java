package com.geostat.census_2024.ui.inquire_v1.stepper.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.entities.InquireV1HolderEntity;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.data.model.HouseHoldModel;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.data.repository.AddressingRepository;
import com.geostat.census_2024.ui.custom.MutableLivedata;
import com.geostat.census_2024.utility.LocationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FragmentStepViewModel extends AndroidViewModel {

//    private ObservableList<MutableLivedata<HouseHoldModel>> tmpHouseHolder;

    AddressingRepository addressingRepository;

    private final MutableLiveData<List<KeyPairBoolData>> regionsEntries;
    private final MutableLiveData<List<KeyPairBoolData>> municipalEntries;
    private final MutableLiveData<List<KeyPairBoolData>> cityEntries;
    private final MutableLiveData<List<KeyPairBoolData>> unityEntries;
    private final MutableLiveData<List<KeyPairBoolData>> villageEntries;

    private final MutableLivedata<AddressingModel> vm;
    private final MutableLiveData<Boolean> flag = new MutableLiveData<>(true);
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(0);

    private UserModel userModel;


    {
        regionsEntries = new MutableLiveData<>();
        municipalEntries = new MutableLiveData<>();
        cityEntries =  new MutableLiveData<>();
        unityEntries = new MutableLiveData<>();
        villageEntries = new MutableLiveData<>();

        vm = new MutableLivedata<>();
        vm.setValue(new AddressingModel());

    }

    public UserModel getUser() {
        return userModel;
    }

    public void setUser(UserModel userModel) {
        this.userModel = userModel;
    }

    public FragmentStepViewModel(@NonNull Application application, SavedStateHandle savedStateHandle) {
        super(application);

        addressingRepository = new AddressingRepository(application);

    }

    public Long insert(AddressingModel addressingModel, int questionId, Date startTime) throws ExecutionException, InterruptedException {

        Gson gson = new GsonBuilder().serializeNulls().create();
        InquireV1Entity inquireV1Entity = gson.fromJson(gson.toJson(addressingModel), InquireV1Entity.class);
        if (questionId == 0) inquireV1Entity.setCreatedAt(new Date());
        inquireV1Entity.setUpdatedAt(new Date());

        List<InquireV1HolderEntity> inquireV1HolderEntities = addressingModel.getHouseHold().parallelStream().map(houseHoldModel -> {
            InquireV1HolderEntity inquireV1HolderEntity = gson.fromJson(gson.toJson(houseHoldModel), InquireV1HolderEntity.class);
            inquireV1HolderEntity.setId((questionId != 0) ? houseHoldModel.getId() : null);
            return inquireV1HolderEntity;
        }).collect(Collectors.toList());

        SupervisionEntity supervisionEntity = new SupervisionEntity(inquireV1Entity.getUuid(), startTime, new Date());
        
        if (questionId == 0) {
            LocationFeature.getInstance(getApplication()).start((latt, longg) -> {
                supervisionEntity.setLatitude(latt);
                supervisionEntity.setLongitude(longg);
            });
        }

        return addressingRepository.insertAddressingWithHolders(inquireV1Entity, inquireV1HolderEntities, (questionId == 0) ? null : supervisionEntity);
    }


    public AddressingWithHolders getById (int id) throws ExecutionException, InterruptedException {
        return addressingRepository.getById(id);
    }

    public MutableLiveData<List<KeyPairBoolData>> getRegionsEntries() {
        return regionsEntries;
    }

    public void setRegionsEntries(List<KeyPairBoolData> regionsEntries) {
        this.regionsEntries.setValue(regionsEntries);
    }

    public void setRegionsEntriesR (@Nullable Integer perm) {

        AddressingModel addressingModel = getVm().getValue();

        if (perm != null && perm.equals(1)) {
            if (regionsEntries.getValue() != null && regionsEntries.getValue().size() > 0) {
                addressingModel.setRegionId(null);
            }
        }

        if (municipalEntries.getValue() != null && municipalEntries.getValue().size() > 0) {
            municipalEntries.setValue(new ArrayList<>());
            addressingModel.setMunicipalId(null);
        }
        if (cityEntries.getValue() != null && cityEntries.getValue().size() > 0) {
            cityEntries.setValue(new ArrayList<>());
            addressingModel.setCityId(null);
        }
        if (unityEntries.getValue() != null && unityEntries.getValue().size() > 0) {
            unityEntries.setValue(new ArrayList<>());
            addressingModel.setUnityId(null);
        }

        if (villageEntries.getValue() != null && villageEntries.getValue().size() > 0) {
            villageEntries.setValue(new ArrayList<>());
            addressingModel.setVillageId(null);
        }
    }

    ///

    public MutableLiveData<List<KeyPairBoolData>> getMunicipalEntries() {
        return municipalEntries;
    }

    public void setMunicipalEntries(List<KeyPairBoolData> municipalEntries) {
        this.municipalEntries.setValue(municipalEntries);
    }

    public void setMunicipalEntriesR (@Nullable Integer perm) {

        AddressingModel addressingModel = getVm().getValue();
        if (perm != null && perm.equals(1)) {
            if (municipalEntries.getValue() != null && municipalEntries.getValue().size() > 0) {
                addressingModel.setMunicipalId(null);
            }
        }

        if (cityEntries.getValue() != null && cityEntries.getValue().size() > 0) {
            cityEntries.setValue(new ArrayList<>());
            addressingModel.setCityId(null);
        }
        if (unityEntries.getValue() != null && unityEntries.getValue().size() > 0) {
            unityEntries.setValue(new ArrayList<>());
            addressingModel.setUnityId(null);
        }

        if (villageEntries.getValue() != null && villageEntries.getValue().size() > 0) {
            villageEntries.setValue(new ArrayList<>());
            addressingModel.setVillageId(null);
        }
    }

    ///

    public MutableLiveData<List<KeyPairBoolData>> getCityEntries() {
        return cityEntries;
    }

    public void setCityEntries(List<KeyPairBoolData> cityEntries) {
        this.cityEntries.setValue(cityEntries);
    }

    public void setCityEntriesR (@Nullable Integer perm) {

        AddressingModel addressingModel = getVm().getValue();

        if (perm != null && perm.equals(1)) {
            if (cityEntries.getValue() != null && cityEntries.getValue().size() > 0) {
                addressingModel.setCityId(null);
            }
        }

        if (unityEntries.getValue() != null && unityEntries.getValue().size() > 0) {
            // unityEntries.setValue(new ArrayList<>());
            addressingModel.setUnityId(null);
        }

        if (villageEntries.getValue() != null && villageEntries.getValue().size() > 0) {
            // villageEntries.setValue(new ArrayList<>());
            addressingModel.setVillageId(null);
        }
    }

    //


    public MutableLiveData<List<KeyPairBoolData>> getUnityEntries() {
        return unityEntries;
    }

    public void setUnityEntries(List<KeyPairBoolData> unityEntries) {
        this.unityEntries.setValue(unityEntries);
    }

    public void setUnityEntriesR (@Nullable Integer perm) {

        AddressingModel addressingModel = getVm().getValue();

        if (perm != null && perm.equals(1)) {
            if (unityEntries.getValue() != null && unityEntries.getValue().size() > 0) {
                addressingModel.setUnityId(null);
            }
        }

        if (villageEntries.getValue() != null && villageEntries.getValue().size() > 0) {

            villageEntries.setValue(new ArrayList<>());
            addressingModel.setVillageId(null);
        }
    }

    public MutableLiveData<List<KeyPairBoolData>> getVillageEntries() {
        return villageEntries;
    }

    public void setVillageEntries(List<KeyPairBoolData> villageEntries) {
        this.villageEntries.setValue(villageEntries);
    }

    public void setVillageEntriesR (@Nullable Integer perm) {

        AddressingModel addressingModel = getVm().getValue();

        if (perm != null) {
            if (villageEntries.getValue() != null && villageEntries.getValue().size() > 0) {
                addressingModel.setVillageId(null);
            }
        }
    }

    //

    public MutableLiveData<Boolean> getFlag() {
        return flag;
    }

    public LiveData<AddressingModel> getVm () {
//        if (vm == null) {
//            vm = new MutableLivedata<>();
//            vm.setValue(new AddressingModel());
//        }
        return this.vm;
    }

    public void setFlag (Boolean flag) {
        this.flag.setValue(flag);
    }

    public void setVm (AddressingModel m) {
//        if (vm == null) {
//            vm = new MutableLivedata<>();
//        }
        Log.d(getClass().getName(), "setVm: " + m.toString());
        this.vm.setValue(m);
    }

    public void setHouseHold(int index, HouseHoldModel m) {
        Objects.requireNonNull(this.getVm().getValue()).setHouseHold(index, m);
        this.setFlag(false);
    }

    public void createHouseHolder (int index, int id) {

        HouseHoldModel m = new HouseHoldModel();
        // m.setFatherName(UUID.randomUUID().toString());
        m.setId(id);

        Objects.requireNonNull(getVm().getValue()).setHouseHold(index, m);

    }

    public void remItem (int index) {
        Objects.requireNonNull(getVm().getValue()).remItem(index);
    }

    public void trigger (int index, int id) {

        HouseHoldModel m = new HouseHoldModel();
        // m.setFatherName(UUID.randomUUID().toString());
        m.setId(id);

        Objects.requireNonNull(getVm().getValue()).trigger(index, m); setFlag(false);

        ///
        this.setVm(this.getVm().getValue());
    }

    public MutableLiveData<Integer> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer step) {
        this.currentStep.setValue(step);
    }

    //    public void addTrigger(int index, MutableLivedata<HouseHoldModel> houseHoldModel) {
//
//        this.getVm().observeForever(new Observer<AddressingModel>() {
//            @Override
//            public void onChanged(AddressingModel addressingModel) {
//                addressingModel.setHouseHold(index, houseHoldModel);
//                FragmentStepViewModel.this.getVm().removeObserver(this);
//            }
//        });
//    }



//    private PropertyChangeRegistry callbacks = new PropertyChangeRegistry();
//
//    @Override
//    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//        Log.d("WHAT", "addOnPropertyChangedCallback: " + callback.toString());
//
//        callbacks.add(callback);
//    }
//
//    @Override
//    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
//        Log.d("WHATISISI", "addOnPropertyChangedCallback: " + callback.toString());
//
//        callbacks.remove(callback);
//    }
//
//    void notifyChange() {
//        callbacks.notifyCallbacks(this, 0, null);
//    }
//
//    void notifyPropertyChanged(int fieldId) {
//        callbacks.notifyCallbacks(this, fieldId, null);
//    }

//    public ObservableList<MutableLivedata<HouseHoldModel>> getTmpHouseHolder() {
//        if (tmpHouseHolder == null) {
//            tmpHouseHolder = new ObservableArrayList<>();
//        }
//        return tmpHouseHolder;
//    }
//
//    public void setTmpHouseHolder(ObservableList<MutableLivedata<HouseHoldModel>> _houseHolderr) {
//        if (tmpHouseHolder == null) {
//            tmpHouseHolder = new ObservableArrayList<>();
//        }
//        //ObservableList<MutableLivedata<HouseHoldModel>> tmp = new ObservableArrayList<>(_houseHolderr);
//
//        this.tmpHouseHolder.clear();
//        this.tmpHouseHolder.addAll(_houseHolderr);
//    }
//
//    public void setTmpHouseHolderItem (MutableLivedata<HouseHoldModel> houseHoldModel) {
//        if (tmpHouseHolder == null) {
//            tmpHouseHolder = new ObservableArrayList<>();
//        }
//        tmpHouseHolder.add(houseHoldModel);
//    }
//
//    public void removeTmpHouseHolderItem (int i) {
//        tmpHouseHolder.remove(i);
//    }
}

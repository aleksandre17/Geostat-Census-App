package com.geostat.census_2024.ui.fragment.stepper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.geostat.census_2024.R;
import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.LoginRepository;
import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.LivingStatus;
import com.geostat.census_2024.data.local.entities.Tag;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.data.model.HouseHoldModel;
import com.geostat.census_2024.data.model.User;
import com.geostat.census_2024.databinding.ActivityMainBinding;
import com.geostat.census_2024.inter.IfFragmentMoveListener;
import com.geostat.census_2024.ui.address.AddressViewModel;
import com.geostat.census_2024.ui.feature.AlertFeature;
import com.geostat.census_2024.ui.fragment.stepper.adapter.StepperAdapter;
import com.geostat.census_2024.ui.fragment.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.fragment.stepper.model.FragmentStepViewModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements IfFragmentMoveListener, StepperLayout.StepperListener {

    ActivityMainBinding binding;
    StepperAdapter stepperAdapter;
    AddressViewModel addressViewModel;
    StepperLayout stepperLayout;
    StepFactory stepFactory;
    private FragmentStepViewModel fragmentStepViewModel;

    public Date startTime = new Date();
    public int thatPosition = 0;
    public int fromRollback = 0;
    public int addressingIndex = 1;

    public int questionId = 0;
    public int houseStatus = 0;
    private Integer saveActivityState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle intent = getIntent().getExtras();

        stepperLayout = binding.stepperLayout;
        stepperLayout.setListener(this);

        addressViewModel = new ViewModelProvider(MainActivity.this).get(AddressViewModel.class);
        StepFactory.setAddressViewModel(addressViewModel);

        fragmentStepViewModel = new ViewModelProvider(MainActivity.this).get(FragmentStepViewModel.class);

        if (intent != null) {
            if (intent.getInt("fromRollback") != 0) fromRollback = 1;
            if (intent.getInt("house-status") != 0) houseStatus = intent.getInt("house-status");
            if (intent.getInt("question-id") != 0) questionId = intent.getInt("question-id");
            if (intent.getInt("addressing-index") != 0) addressingIndex = intent.getInt("addressing-index");
        }

        User user = LoginRepository.getInstance(new LoginDataSource(getApplication())).getUser();
        fragmentStepViewModel.setUser(user);

        try {
            AddressingModel addressingModel = getAddressingModel(savedInstanceState, intent, user);
            fragmentStepViewModel.setVm(addressingModel);
            new Thread(() -> initStartReg(addressingModel.getRegionId())).start();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        StepFactory.setStepViewModel(fragmentStepViewModel);

        stepperAdapter = new StepperAdapter(getSupportFragmentManager(), MainActivity.this, addressViewModel, stepFactory);

        stepperLayout.setAdapter(stepperAdapter);
        stepperLayout.setOffscreenPageLimit(1);

        // setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("კითხვარი");

        if (savedInstanceState != null && savedInstanceState.get("index") != null) {
            stepperLayout.setCurrentStepPosition(savedInstanceState.getInt("index"));
        }
    }


    private AddressingModel getAddressingModel(Bundle savedInstanceState, Bundle intent, User user) throws ExecutionException, InterruptedException {
        Gson json = new GsonBuilder().serializeNulls().create();

        AddressingModel addressingModel = new AddressingModel();

        if (savedInstanceState != null && savedInstanceState.getSerializable("question") != null) {
            addressingModel = (AddressingModel) savedInstanceState.getSerializable("question");
            if (savedInstanceState.getInt("questionId") != 0) addressingModel.setId(savedInstanceState.getInt("questionId"));
            questionId = savedInstanceState.getInt("questionId");
        } else if (intent != null && intent.getInt("question-id") != 0) {

            AddressingWithHolders addressing = fragmentStepViewModel.getById(questionId);
            Addressing res = addressing.addressing;

            addressingModel = json.fromJson(json.toJson(res), AddressingModel.class);
            List<HouseHoldModel> houseHoldModels = addressing.holder.stream().map(holder -> json.fromJson(json.toJson(holder), HouseHoldModel.class)).collect(Collectors.toList());
            addressingModel.setHouseHoldS(houseHoldModels);

        } else {
            Address findMunicipalityByLocationCode = addressViewModel.findMunicipalityByLocationCode(user.getProperty("region_id") + " " + user.getProperty("munic_id"));

            addressingModel.setRegionId(findMunicipalityByLocationCode.getParentId());
            addressingModel.setMunicipalId(findMunicipalityByLocationCode.getId());

            addressingModel.setHouseCode(getIntent().getExtras().getString("mapId"));
            addressingModel.setUuid(java.util.UUID.randomUUID().toString());
            addressingModel.setIndex(addressingIndex);
            addressingModel.setCreatedAt(startTime);
        }

        return addressingModel;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveActivityState = 1;
        outState.putInt("index", stepperLayout.getCurrentStepPosition());
        outState.putInt("questionId", questionId);
        outState.putSerializable("question", fragmentStepViewModel.getVm().getValue());

        if (AlertFeature.isShowing()) { outState.putInt("restore-alert", 1);
            AlertFeature.getAlerter().cancel();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt("restore-alert") > 0 && AlertFeature.getAlerter() != null) {
            AlertFeature.show();
        }
    }

    @Override
    public void moveSetTitle(String title) {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (stepperLayout.getCurrentStepPosition() > 0) {
            stepperLayout.setCurrentStepPosition(stepperLayout.getCurrentStepPosition() - 1);
        } else {
            AlertFeature.getInstance(MainActivity.this)
                    .setTitle("შეტყობინება")
                    .setText("ნამდვილად გსურთ კითხვარზე მუშაობის შეწყვეტა?")
                    .setNegativeButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("დიახ", (dialogInterface, i) -> {
                        //                    finish();
                        //                    MainActivity.super.onBackPressed();
                        Intent up = NavUtils.getParentActivityIntent(MainActivity.this);
                        assert up != null;
                        up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        NavUtils.navigateUpTo(MainActivity.this, up);
                        finish();
                    }).init();

            AlertFeature.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (saveActivityState == null) {
            stepperAdapter.clear();
            addressViewModel = null;
            fragmentStepViewModel = null;
            AlertFeature.clear();
        }
    }

    public void initStartReg(@Nullable Integer selectedReg) {

        new Thread(() -> runOnUiThread(() -> {

            try {
                List<Tag> tags = addressViewModel.getTags();
                if (!tags.isEmpty()) {
                    addressViewModel.setTags(tags);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            List<KeyPairBoolData> regionsEntrie = new ArrayList<>();
            LiveData<List<Address>> listLiveData = addressViewModel.getRegions(1, 2);
            listLiveData.observe(MainActivity.this, addresses -> {


                for (Address address : addresses) {
                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(address.getId());
                    h.setName(address.getLocationName());
                    h.setObject(address.getLocationTypeId());

                    if (address.getId().equals(selectedReg)) {
                        // Log.d("FINDFIND_YES_YES", "onChanged: " + selectedReg);
                        h.setSelected(true);
                    }
                    regionsEntrie.add(h);

                }

                fragmentStepViewModel.setRegionsEntries(regionsEntrie);
            });
        })).start();

        new Thread(() -> {
            try {
                List<BuildingType> buildingTypes = addressViewModel.getAllBuildingType();
                List<LivingStatus> livingStatuses = addressViewModel.getAllLivingStatus();

                runOnUiThread(() -> {
                    addressViewModel.setBuildingTypes(buildingTypes);
                    addressViewModel.setLivingStatuses(livingStatuses);
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onCompleted(View completeButton) {

        if (stepperLayout.getCurrentStepPosition() == 3) {

            if (houseStatus == 2) {

                Intent intent = new Intent();
                if (fromRollback == 0) {
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_FIRST_USER, intent);
                }
                finish();

            } else {

                sure(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AddressingModel addressingModel = fragmentStepViewModel.getVm().getValue();
                        assert addressingModel != null;
                        if (addressingModel.getStreet() != null) {
                            String tagName = addressingModel.getStreet();
                            Tag tag = new Tag(1, tagName);
                            addressViewModel.setTag(tag, 1);
                        }

                        try {
                            Long save = fragmentStepViewModel.insert(addressingModel, questionId, startTime);
                            if (save != null) {
                                addressingModel.setId(Math.toIntExact(save));
                                questionId = Math.toIntExact(save);
                                alert(save);
                            } else {
                                Alerter.create(MainActivity.this)
                                        .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("კითხვარი არ აიტვირთა!").setDuration(5000).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Alerter.create(MainActivity.this)
                                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText(Objects.requireNonNull(e.getMessage())).setDuration(5000).show();
                        }
                    }
                });
            }
        }
    }

    public void sure (DialogInterface.OnClickListener y) {

        AlertFeature.getInstance(MainActivity.this)
                .setTitle("შეტყობინება")
                .setText("ნამდვილად გსურთ შენობაზე მუშაობის დასრულება?")
                .setNeutralButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("დიახ", y).init();
        AlertFeature.show();
    }

    public void alert (Long save) {

        Integer pasAddressingId = Math.toIntExact((questionId != 0) ? questionId : save);
        AlertFeature.getInstance(MainActivity.this)
                .setTitle("შეტყობინება")
                .setText("კითხვარი წარმატებით აიტვირთა, აირჩიეთ შემდეგი მოქმედება")
                .setNeutralButton("კითხვარები", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    stepperAdapter.clear();

                    Intent intent = new Intent();
                    intent.putExtra("addressing-id", pasAddressingId);
                    setResult(RESULT_CANCELED, intent);
                    finish();
                })
                .setPositiveButton("რუკა", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    stepperAdapter.clear();

                    if (fromRollback == 0) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        setResult(RESULT_FIRST_USER, intent);
                        finish();
                    }
                }).init();

        AlertFeature.show();
    }

    @Override
    public void onError(VerificationError verificationError) {
    }

    @Override
    public void onStepSelected(int newStepPosition) {

        int prev = thatPosition;
        AddressingModel m = fragmentStepViewModel.getVm().getValue();

        if (newStepPosition == 2 && m != null) {
            if (Objects.equals(m.getBuildingType(), 4) || (Arrays.asList(1, 2).contains(m.getBuildingType()) && !Objects.equals(m.getLivingStatus(), 1))) {
                skip(prev, newStepPosition);
            }
        }

        thatPosition = newStepPosition;
    }

    @Override
    public void onReturn() {

    }

    @Override
    public boolean isDestroyed() {
        StepFactory.clear();
        return super.isDestroyed();
    }

    private void skip (int prev, int newStepPosition) {
        if (prev >= newStepPosition) {
            new Handler().post(() -> stepperLayout.setCurrentStepPosition(newStepPosition - 1));
        } else {
            new Handler().post(() -> stepperLayout.setCurrentStepPosition(newStepPosition + 1));
        }
    }
}


//        stepFactory = new StepFactory(addressViewModel);
//
//        StepFactory.Init.create("first", 0);

/// Steps
//        HomeStepFragment homeStepFragment = HomeStepFragment.newInstance(0, addressViewModel);
//        FirstStepFragment firstStepFragment = FirstStepFragment.newInstance(1, addressViewModel);
//        SecondStepFragment secondStepFragment = SecondStepFragment.newInstance(2);
//        EndStepFragment endStepFragment = EndStepFragment.newInstance(3, addressViewModel);

//        stepperAdapter.addFragment(homeStepFragment, 0);
//        stepperAdapter.addFragment(firstStepFragment, 1);
//        stepperAdapter.addFragment(secondStepFragment, 2);
//        stepperAdapter.addFragment(endStepFragment, 3);


//    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = preferences.edit();
//                editor.putBoolean("newStep", true);
//                        editor.apply();


//
//if ((Objects.equals(fragmentStepViewModel.getVm().getValue().getBuildingType(), 5) || !Objects.equals(fragmentStepViewModel.getVm().getValue().getLivingStatus(), 1))) {
//        if (prev >= newStepPosition) {
//        new Handler().post(new Runnable() {
//@Override
//public void run() {
//        stepperLayout.setCurrentStepPosition(newStepPosition - 1);
//        }
//        });
//        } else {
//        new Handler().post(new Runnable() {
//@Override
//public void run() {
//        stepperLayout.setCurrentStepPosition(newStepPosition + 1);
//        }
//        });
//        }
//        }


//            if (fromRollback == 0) {
//
//                    Intent intent = new Intent();
//                    setResult(RESULT_OK, intent);
//                    finish();
//
////                Intent up = new Intent(MainActivity.this, MapActivity.class);
////                up.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_HISTORY);
////                startActivity(up);
////                finishAffinity();
//                    } else {
//
//                    Intent intent = new Intent();
//                    setResult(RESULT_FIRST_USER, intent);
//                    finish();
//
////                Intent up = new Intent(MainActivity.this, RollbackActivity.class);
////                up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                startActivity(up);
////                finish();
//                    }
package com.geostat.census_2024.ui.inquire_v1.stepper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.geostat.census_2024.R;
import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.repository.LoginRepository;
import com.geostat.census_2024.data.local.entities.AddressEntity;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.entities.BuildingTypeEntity;
import com.geostat.census_2024.data.local.entities.LivingStatusEntity;
import com.geostat.census_2024.data.local.entities.TagEntity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.data.model.HouseHoldModel;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.databinding.ActivityInquireV1Binding;
import com.geostat.census_2024.architecture.inter.IfFragmentMoveListener;
import com.geostat.census_2024.ui.address.AddressViewModel;
import com.geostat.census_2024.architecture.widjet.AlertFeature;
import com.geostat.census_2024.ui.inquire_v1.stepper.adapter.StepperAdapter;
import com.geostat.census_2024.ui.inquire_v1.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.inquire_v1.stepper.model.FragmentStepViewModel;
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

public class InquireActivityV1 extends AppCompatActivity implements IfFragmentMoveListener, StepperLayout.StepperListener {

    ActivityInquireV1Binding binding;
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

        AlertDialog dialog = alert(); dialog.show();
        binding = ActivityInquireV1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle intent = getIntent().getExtras();

        stepperLayout = binding.stepperLayout;
        stepperLayout.setListener(this);

        addressViewModel = new ViewModelProvider(InquireActivityV1.this).get(AddressViewModel.class);
        StepFactory.setAddressViewModel(addressViewModel);

        fragmentStepViewModel = new ViewModelProvider(InquireActivityV1.this).get(FragmentStepViewModel.class);

        if (intent != null) {
            if (intent.getInt("fromRollback") != 0) fromRollback = 1;
            if (intent.getInt("house-status") != 0) houseStatus = intent.getInt("house-status");
            if (intent.getInt("question-id") != 0) questionId = intent.getInt("question-id");
            if (intent.getInt("addressing-index") != 0) addressingIndex = intent.getInt("addressing-index");
        }

        UserModel userModel = LoginRepository.getInstance(new LoginDataSource(getApplication())).getUser();
        fragmentStepViewModel.setUser(userModel);

        try {
            AddressingModel addressingModel = getAddressingModel(savedInstanceState, intent, userModel);
            fragmentStepViewModel.setVm(addressingModel);
            new Thread(() -> initStartReg(addressingModel.getRegionId())).start();

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        StepFactory.setStepViewModel(fragmentStepViewModel);

        new Handler().postDelayed(() -> {
            stepperAdapter = new StepperAdapter(getSupportFragmentManager(), InquireActivityV1.this, addressViewModel, stepFactory);
            stepperLayout.setAdapter(stepperAdapter);
            stepperLayout.setOffscreenPageLimit(1);
            dialog.dismiss();
        }, 0);

        // setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("კითხვარი");

        if (savedInstanceState != null && savedInstanceState.get("index") != null) {
            stepperLayout.setCurrentStepPosition(savedInstanceState.getInt("index"));
        }
    }


    private AddressingModel getAddressingModel(Bundle savedInstanceState, Bundle intent, UserModel userModel) throws ExecutionException, InterruptedException {
        Gson json = new GsonBuilder().serializeNulls().create();

        AddressingModel addressingModel = new AddressingModel();

        if (savedInstanceState != null && savedInstanceState.getSerializable("question") != null) {
            addressingModel = (AddressingModel) savedInstanceState.getSerializable("question");
            if (savedInstanceState.getInt("questionId") != 0) addressingModel.setId(savedInstanceState.getInt("questionId"));
            questionId = savedInstanceState.getInt("questionId");
        } else if (intent != null && intent.getInt("question-id") != 0) {

            AddressingWithHolders addressing = fragmentStepViewModel.getById(questionId);
            InquireV1Entity res = addressing.inquireV1Entity;

            addressingModel = json.fromJson(json.toJson(res), AddressingModel.class);
            List<HouseHoldModel> houseHoldModels = addressing.inquireV1HolderEntity.stream().map(holder -> json.fromJson(json.toJson(holder), HouseHoldModel.class)).collect(Collectors.toList());
            addressingModel.setHouseHoldS(houseHoldModels);

        } else {
            AddressEntity findMunicipalityByLocationCode = addressViewModel.findMunicipalityByLocationCode(userModel.getProperty("region_id") + " " + userModel.getProperty("munic_id"));

            addressingModel.setRegionId(findMunicipalityByLocationCode.getParentId());
            addressingModel.setMunicipalId(findMunicipalityByLocationCode.getId());

            addressingModel.setHouseCode(getIntent().getExtras().getString("mapId"));
            addressingModel.setUuid(java.util.UUID.randomUUID().toString());
            addressingModel.setIndex(addressingIndex);
            addressingModel.setCreatedAt(startTime);
        }

        return addressingModel;
    }

    private AlertDialog alert (){
        AlertDialog.Builder builder = new AlertDialog.Builder(InquireActivityV1.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        return builder.create();
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
            AlertFeature.getInstance(InquireActivityV1.this)
                    .setTitle("შეტყობინება")
                    .setText("ნამდვილად გსურთ კითხვარზე მუშაობის შეწყვეტა?")
                    .setNegativeButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("დიახ", (dialogInterface, i) -> {
                        //                    finish();
                        //                    MainActivity.super.onBackPressed();
                        Intent up = NavUtils.getParentActivityIntent(InquireActivityV1.this);
                        assert up != null;
                        up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        NavUtils.navigateUpTo(InquireActivityV1.this, up);
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
                List<TagEntity> tagEntities = addressViewModel.getTags();
                if (!tagEntities.isEmpty()) {
                    addressViewModel.setTags(tagEntities);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            List<KeyPairBoolData> regionsEntrie = new ArrayList<>();
            LiveData<List<AddressEntity>> listLiveData = addressViewModel.getRegions(1, 2);
            listLiveData.observe(InquireActivityV1.this, addresses -> {


                for (AddressEntity addressEntity : addresses) {
                    KeyPairBoolData h = new KeyPairBoolData();
                    h.setId(addressEntity.getId());
                    h.setName(addressEntity.getLocationName());
                    h.setObject(addressEntity.getLocationTypeId());

                    if (addressEntity.getId().equals(selectedReg)) {
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
                List<BuildingTypeEntity> buildingTypeEntities = addressViewModel.getAllBuildingType();
                List<LivingStatusEntity> livingStatusEntities = addressViewModel.getAllLivingStatus();

                runOnUiThread(() -> {
                    addressViewModel.setBuildingTypes(buildingTypeEntities);
                    addressViewModel.setLivingStatuses(livingStatusEntities);
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

                sure((dialogInterface, i) -> {

                    AddressingModel addressingModel = fragmentStepViewModel.getVm().getValue();
                    assert addressingModel != null;
                    if (addressingModel.getStreet() != null) {
                        String tagName = addressingModel.getStreet();
                        TagEntity tagEntity = new TagEntity(1, tagName);
                        addressViewModel.setTag(tagEntity, 1);
                    }

                    try {
                        Long save = fragmentStepViewModel.insert(addressingModel, questionId, startTime);
                        if (save != null) {
                            addressingModel.setId(Math.toIntExact(save));
                            questionId = Math.toIntExact(save);
                            alert(save);
                        } else {
                            Alerter.create(InquireActivityV1.this)
                                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("კითხვარი არ აიტვირთა!").setDuration(5000).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Alerter.create(InquireActivityV1.this)
                                .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText(Objects.requireNonNull(e.getMessage())).setDuration(5000).show();
                    }
                });
            }
        }
    }

    public void sure (DialogInterface.OnClickListener y) {

        AlertFeature.getInstance(InquireActivityV1.this)
                .setTitle("შეტყობინება")
                .setText("ნამდვილად გსურთ შენობაზე მუშაობის დასრულება?")
                .setNeutralButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("დიახ", y).init();
        AlertFeature.show();
    }

    public void alert (Long save) {

        Integer pasAddressingId = Math.toIntExact((questionId != 0) ? questionId : save);
        AlertFeature.getInstance(InquireActivityV1.this)
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

        if (newStepPosition > 0) stepperLayout.setOffscreenPageLimit(3);

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] sc = new int[2];
            view.getLocationOnScreen(sc);
            float x = ev.getRawX() + view.getLeft() - sc[0];
            float y = ev.getRawY() + view.getTop() - sc[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
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
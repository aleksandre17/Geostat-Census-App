package com.geostat.census_2024.ui.addressing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.architecture.inter.handler.IndexAdapterItemClickHandlers;
import com.geostat.census_2024.ui.addressing.adapter.AddressingAdapter;
import com.geostat.census_2024.ui.addressing.model.AddressingViewModel;
import com.geostat.census_2024.architecture.widjet.AlertFeature;
import com.geostat.census_2024.ui.inquire_v1.stepper.InquireActivityV1;
import com.geostat.census_2024.ui.map.MapActivity;
import com.geostat.census_2024.utility.SharedPref;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AddressingActivity extends AppCompatActivity {

    public Integer itemIndex;
    public int houseStatus;

    AddressingViewModel addressingViewModel;
    AddressingAdapter addressingAdapter;
    String mapId;

    private Integer saveActivityState;

    private final ActivityResultLauncher<Intent> AddressingActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_CANCELED && result.getData() != null) {

                if (((RecyclerView) findViewById(R.id.addressingView)).getAdapter() instanceof AddressingAdapter) {

                    int callbackAddressingId = result.getData().getExtras().getInt("addressing-id");

                    if (callbackAddressingId != 0) {
                        try {
                            AddressingWithHolders addressing = addressingViewModel.getAddressingById(callbackAddressingId);
                            if (addressing != null) {
                                if (itemIndex == null) {
                                    addressingAdapter.add(addressing);
                                    ((RecyclerView) findViewById(R.id.addressingView)).smoothScrollToPosition(0);
                                } else {
                                    addressingAdapter.updateItem(itemIndex, addressing);
                                }
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (result.getResultCode() == RESULT_OK) {

                checkHouseStatus();

                Intent up = new Intent(AddressingActivity.this, MapActivity.class);
                up.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(up);
                finishAffinity();

            }
        }
    });

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressing);

        houseStatus = getIntent().getExtras().getInt("house-status");

        LayerModel m = getIntent().getExtras().getParcelable("m"); mapId = m.getProperty("house_code");
        addressingViewModel = new ViewModelProvider(AddressingActivity.this).get(AddressingViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.addressingView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressingAdapter = new AddressingAdapter(new ArrayList<>(), new IndexAdapterItemClickHandlers() {
            @Override
            public void btnClickListener(int id, int index) {
                itemIndex = index;

                Intent intent = new Intent(getApplicationContext(), InquireActivityV1.class);
                intent.putExtra("question-id", id);
                intent.putExtra("mapId", mapId);
                intent.putExtra("house-status", houseStatus);
                AddressingActivityResult.launch(intent);
            }

            @Override
            public void removeClickListener(int id, int index) {

                AlertFeature.getInstance(AddressingActivity.this)
                        .setTitle("შეტყობინება")
                        .setText("გსურთ კითხვარის წაშლა?")
                        .setNeutralButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton("დიახ", (dialogInterface, i) -> {
                            try {
                                Integer clear = addressingViewModel.removeAddress(id);
                                if (clear.equals(1)) {
                                    addressingAdapter.remove(index);
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).init();
                AlertFeature.show();
            }
        }, "addressing", houseStatus);

        recyclerView.setAdapter(addressingAdapter);


        new Handler().postDelayed(() -> {
            List<AddressingWithHolders> addressingWithHoldersSelect = addressingViewModel.fetchAddressing(mapId);
            addressingAdapter.start(addressingWithHoldersSelect.stream()
                    .filter(addressing -> !addressing.inquireV1Entity.getStatus().equals(5)).collect(Collectors.toList()));
        }, 0);

        Button createNew = findViewById(R.id.createNew);
        createNew.setOnClickListener(view -> {

            if (houseStatus == 2) {
                Alerter.create(this)
                        .setTitle("შეტყობინება")
                        .setBackgroundColorRes(R.color.yellow)
                        .setText("ახალი კითხვარის დასამატებლად დააჭირეთ ‘’შენობაზე მუშაობის გაგრძელებას’’, ხოლო შემდეგ დაამატეთ დაამატეთ ახალი კითხვარი").setDuration(5000).show();
                return;
            }

            itemIndex = null;
            InquireV1Entity findNewestInquireV1Entity = addressingViewModel.findNewestR(mapId);

            Intent intent = new Intent(getApplicationContext(), InquireActivityV1.class);
            intent.putExtra("mapId", mapId);
            intent.putExtra("addressing-index", (findNewestInquireV1Entity != null) ? findNewestInquireV1Entity.getIndex() + 1 : 1);
            AddressingActivityResult.launch(intent);
        });

        Button end = findViewById(R.id.end);
        if (houseStatus == 2) {
            end.setText("შენობაზე მუშაობის გაგრძელება");
        }

        end.setOnClickListener(view -> {
            String houseChangeText = (houseStatus == 2) ? "ნამდვილად გსურთ შენობაზე მუშაობის გაგრძელება?" : "ნამდვილად გსურთ შენობაზე მუშაობის დასრულება?’";
            AlertFeature.getInstance(AddressingActivity.this)
                    .setTitle("შეტყობინება")
                    .setText(houseChangeText)
                    .setNeutralButton("არა", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("დიახ", (dialogInterface, i) -> { dialogInterface.dismiss();
                        if (addressingAdapter.getItemCount() == 0) {

                            Alerter.create(this)
                                    .setTitle("შეტყობინება")
                                    .setBackgroundColorRes(R.color.yellow)
                                    .setText("შენობაზე კითხვარის დასასრულებლად საჭიროა შეივსოს მინიმუმ ერთი კითხვარი").setDuration(5000).show();

                        } else if (addressingAdapter.getResponse().stream().anyMatch(addressing -> addressing.inquireV1Entity.getStatus().equals(2))) {

                            Alerter.create(this)
                                    .setTitle("შეტყობინება")
                                    .setBackgroundColorRes(R.color.yellow)
                                    .setText("შენობაზე კითხვარის დასასრულებლად საჭიროა დაბრუნებული კითხვარების რედაკტირება").setDuration(5000).show();
                        } else {

                            Intent up = new Intent(AddressingActivity.this, MapActivity.class);
                            SharedPref.write("is_end", (houseStatus == 2) ? 1 : 2);
                            up.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(up);
                            finishAffinity();
                        }
                    }).init();
            AlertFeature.show();
        });


        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("კითხვარები");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // finish(); return true;
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            checkHouseStatus();

            Intent up = NavUtils.getParentActivityIntent(this);
            assert up != null;
            up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, up);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkHouseStatus() {
        // addressingAdapter.getResponse().size() > 0 && houseStatus != 2 && houseStatus != 3
        if (addressingAdapter.getResponse().size() > 0 && houseStatus == 0) SharedPref.write("house-start", true);
        else if (addressingAdapter.getResponse().size() == 0 && !Arrays.asList(0, 4, 7).contains(houseStatus)) SharedPref.write("house-clear", true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        saveActivityState = 1;
        if (AlertFeature.isShowing()) { outState.putInt("restore-alert", 1); AlertFeature.getAlerter().cancel(); }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getInt("restore-alert") > 0 && AlertFeature.getAlerter() != null)  AlertFeature.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveActivityState == null) AlertFeature.clear();
    }
}
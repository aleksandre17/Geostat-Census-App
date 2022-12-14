package com.geostat.census_2024.ui.rollback;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.architecture.inter.handler.IndexAdapterItemClickHandlersWithMap;
import com.geostat.census_2024.ui.addressing.adapter.AddressingAdapter;
import com.geostat.census_2024.ui.addressing.model.AddressingViewModel;
import com.geostat.census_2024.ui.inquire_v1.stepper.InquireActivityV1;
import com.geostat.census_2024.ui.map.MapActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class RollbackActivity extends AppCompatActivity {

    public Integer itemIndex;

    AddressingViewModel addressingViewModel;
    AddressingAdapter rollbackAddressingAdapter;

    private final ActivityResultLauncher<Intent> rollbackActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
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
                                    rollbackAddressingAdapter.add(addressing);
                                    ((RecyclerView) findViewById(R.id.addressingView)).smoothScrollToPosition(0);
                                } else {
                                    rollbackAddressingAdapter.updateItem(itemIndex, addressing);
                                }
                            }

                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (result.getResultCode() == RESULT_OK) {

                Intent up = new Intent(RollbackActivity.this, MapActivity.class);
                up.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(up);
                finishAffinity();

            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollback);

        addressingViewModel = new ViewModelProvider(RollbackActivity.this).get(AddressingViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.addressingView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rollbackAddressingAdapter = new AddressingAdapter(new ArrayList<>(), new IndexAdapterItemClickHandlersWithMap() {

            @Override
            public void btnClickListener(int id, int index, String houseCode) {
                itemIndex = index;

                Intent intent = new Intent(getApplicationContext(), InquireActivityV1.class);

                intent.putExtra("question-id", id);
                intent.putExtra("mapId", houseCode);
                intent.putExtra("house-status", 3);

                rollbackActivityResult.launch(intent);
            }

            @Override
            public void removeClickListener(int id, int index) {

            }

            @Override
            public void mapClickListener(String houseCode) {
                Intent intent = new Intent();
                intent.putExtra("house_code", houseCode);
                setResult(RESULT_OK, intent);
                finish();
            }
        }, "rollback", 3);

        recyclerView.setAdapter(rollbackAddressingAdapter);

        new Handler().postDelayed(() -> {
            List<AddressingWithHolders> rollbackAddressings = addressingViewModel.getRollbackAddressings();
            rollbackAddressingAdapter.start(rollbackAddressings);
        }, 0);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("??????????????????????????????");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent up = NavUtils.getParentActivityIntent(RollbackActivity.this);
            assert up != null;
            up.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(RollbackActivity.this, up);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
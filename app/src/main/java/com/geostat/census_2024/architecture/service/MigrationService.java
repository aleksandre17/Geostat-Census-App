package com.geostat.census_2024.architecture.service;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.geostat.census_2024.architecture.manager.ArcgisManager;
import com.geostat.census_2024.data.request.model.UserRequestModel;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MigrationService {

    private List<UserRequestModel> migrateUsers(ArcgisManager arcgisController) {

        ListenableFuture<FeatureQueryResult> futureReg = arcgisController.getMapService().queryResult("saregistracio", "1=1");
        ListenableFuture<FeatureQueryResult> future = arcgisController.getMapService().queryResult("sazedamxedvelo", "1=1");

        List<UserRequestModel> userRequestModels = new ArrayList<>();
        try {
            futureReg.get().forEach(feature -> userRequestModels.add(new UserRequestModel(Objects.requireNonNull(feature.getAttributes().get("distr_code")).toString(), Objects.requireNonNull(feature.getAttributes().get("distr_code")).toString())));

            future.get().forEach(feature -> userRequestModels.add(new UserRequestModel(Objects.requireNonNull(feature.getAttributes().get("instr_code")).toString(), Objects.requireNonNull(feature.getAttributes().get("instr_code")).toString())));

            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            Type type = new TypeToken<List<UserRequestModel>>(){}.getType();
            String districtArealJson = new GsonBuilder().serializeNulls().create().toJson(userRequestModels, type);

            RequestBody body = RequestBody.create(JSON, districtArealJson);

            Request request = new Request.Builder()
                    .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSIsImV4cCI6MTY2Njk1MDE1NX0.u6wD6MmqnZD3HdtKGk_8qg5Qr9HOGdp3iikyGgZnoMDHexk2W171beuKeuRE9JNu5YDrG0DvBCcp-n_7sD_5tQ")
                    .url("http://192.168.1.58:8080/users/create-users")
                    .post(body)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {

                }
            });

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return userRequestModels;
    }
}

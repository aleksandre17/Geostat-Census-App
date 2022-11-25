package com.geostat.census_2024.service;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.App;
import com.geostat.census_2024.architecture.future.CallbackFuture;
import com.geostat.census_2024.architecture.module.LoaderModule;
import com.geostat.census_2024.controller.ArcgisController;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.DistrictAreal;
import com.geostat.census_2024.data.model.House;
import com.geostat.census_2024.data.model.User;
import com.geostat.census_2024.data.repository.AddressingRepository;
import com.geostat.census_2024.data.response.ResponseHouseModel;
import com.geostat.census_2024.inter.ThatActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tapadoo.alerter.Alerter;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.NotLinkException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncService {

    private static volatile SyncService INSTANCE;
    private final LoaderModule loaderModule;
    private final ThatActivity<AppCompatActivity> activity;

    public SyncService(ThatActivity<AppCompatActivity> activity) {
        this.activity = activity;
        loaderModule = new LoaderModule(activity);
    }

    public static SyncService getInstance(final ThatActivity<AppCompatActivity> activity) {
        if (INSTANCE == null) { synchronized (SyncService.class) { if (INSTANCE == null) { INSTANCE = new SyncService(activity); } } }
        return INSTANCE;
    }

    public void sync () {
        if (isNetworkAvailable()) {
            loaderModule.showProgressingView();

            try {
                final ArcgisController arcgisController = activity.getArcgisController();
                final User user = activity.getUser();
                if (user == null) throw new UserNotAuthenticatedException("კიდევ სცადეთ!");
                else if (arcgisController == null) throw new NotLinkException("დაელოდეთ რუკის ჩატვირთვას!");
                else {

                    final Application application = ((Activity) activity).getApplication();
                    final AddressingRepository addressingRepository = new AddressingRepository(application);
                    final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
                    final ListenableFuture<FeatureQueryResult> queryResult = arcgisController.queryResult("saregistracio", "fid = " + String.valueOf(user.getProperty("fid")));

                    FeatureQueryResult result = queryResult.get();
                    Feature districtFeature = result.iterator().next();

                    if (districtFeature != null) {

                        Map<String, Object> distAttributes = districtFeature.getAttributes();
                        DistrictAreal districtAreal = gson.fromJson(gson.toJson(distAttributes), DistrictAreal.class);
                        districtAreal.setGeometry(districtFeature.getGeometry().toJson());

                        /// Houses

                        final ListenableFuture<FeatureQueryResult> queryResultForHouse = arcgisController.queryResult("house_point", "distr_id = '" + distAttributes.get("distr_id") + "' AND instr_id = '" + distAttributes.get("instr_id") + "' AND (status=1 OR ((status = 2 OR status = 7 OR status = 4) AND sent is null) Or status = 3)");
                        FeatureQueryResult HouseResult = queryResultForHouse.get();

                        Map<String, Feature> houseResults = getFeatures(HouseResult);
                        List<House> housesArray = houseResults.values().parallelStream().map(feature -> {

                            House house = gson.fromJson(gson.toJson(feature.getAttributes()), House.class);
                            house.setGeometry(feature.getGeometry().toJson());
                            house.setDistr_code(districtAreal.getDistr_code());

                            List<AddressingWithHolders> addressing = addressingRepository.fetch(house.getHouse_code());
                            house.setAddressing(addressing);

                            return house;
                        }).collect(Collectors.toList());

                        districtAreal.setHouses(housesArray);
                        OkHttpClient client = new OkHttpClient();
                        CallbackFuture future = new CallbackFuture();
                        client.newCall(sentReq(districtAreal, application, user)).enqueue(future);

                        Response response = future.get();
                        receiverRes(response, houseResults);

                    }
                }
            } catch (NotLinkException | UserNotAuthenticatedException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                loaderModule.hideProgressingView();

                String text = ((Exception) e).getMessage();
                Alerter.create(activity.init())
                        .setTitle("შეტყობინება")
                        .setBackgroundColorRes(R.color.red).setText(Objects.requireNonNull(text)).setDuration(5000).show();
            }
        } else {
            Alerter.create(activity.init())
                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("სინქრონიზაციის შესასრულებლად დაუკავშრდით ინტერნეტს!").setDuration(5000).show();
        }
    }

    private Request sentReq(DistrictAreal districtAreal, Application application, User user) {
        final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

        String districtArealJson = gson.toJson(districtAreal);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, districtArealJson);

        return new Request.Builder().header("Authorization", "Bearer " + user.getToken()).url(((App) application).SYNC_URL).post(body).build();
    }

    private void receiverRes (Response response, Map<String, Feature> houseResults) {
        if (response.code() == 200) {
            final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

            FeatureTable featureTable = Objects.requireNonNull(activity.getMapViewModel().getTouchableLayer().getValue()).getFeatureTable();
            Handler handler = new Handler();

            int[] index = new int[]{1};
            houseResults.entrySet().parallelStream().forEach(entry -> {
                if (Arrays.asList(2, 7, 4).contains(Integer.parseInt(Objects.requireNonNullElse(Objects.requireNonNull(houseResults.get(entry.getKey())).getAttributes().get("status"), 0).toString())) &&
                        Objects.requireNonNull(houseResults.get(entry.getKey())).getAttributes().get("sent") == null) {

                    handler.postDelayed(() -> {
                        Objects.requireNonNull(houseResults.get(entry.getKey())).getAttributes().put("sent", 1);
                        featureTable.updateFeatureAsync(houseResults.get(entry.getKey()));
                    }, 50L * index[0]);

                }
                index[0]++;
            });

            Executor executor = Executors.newFixedThreadPool(3);
            executor.execute(() -> {

                try {

                    List<ResponseHouseModel> callbackHouses = gson.fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<List<ResponseHouseModel>>() {}.getType());

                    callbackHouses.parallelStream().forEach(house -> {

                        Feature feature = houseResults.get(house.getHouse_code());

                        if (feature != null) {

                            if (house.getStatus().equals(5)) {
//                                                featureTable.deleteFeatureAsync(feature).addDoneListener(() -> {});
//                                                getActivity().getMapViewModel().removeAddressing(house.getHouse_code());
                                feature.getAttributes().put("sent", 1);
                            } else if (house.getStatus().equals(6)) {
                                feature.getAttributes().put("status", 0);
                                feature.getAttributes().put("sent", null);
                            } else if (house.getStatus().equals(3)) {
                                feature.getAttributes().put("status", 3);

                                try {
                                    Integer l = activity.getMapViewModel().updateAddressingStatus(house.getAddresses());
                                    featureTable.updateFeatureAsync(feature).addDoneListener(() -> {});
                                } catch (Exception e) {
                                    Log.d("TAG", "e: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                        }
                    });


                    (activity.init()).runOnUiThread(() -> new Handler().postDelayed(() -> {
                        loaderModule.hideProgressingView();
                        Alerter.create(activity.init())
                                .setTitle("შეტყობინება").setBackgroundColorRes(R.color.green).setText("სინქრონიზაცია შესრულდა წარმატებით").setDuration(5000).show();
                    }, 2000));

                } catch (IOException e) {
                    e.printStackTrace();

                    String text = (e instanceof SocketTimeoutException) ? "სცადეთ სხვა ინტენეტთად დაკავშრება" : e.getMessage();
                    activity.init().runOnUiThread(() -> Alerter.create(activity.init())
                            .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText(Objects.requireNonNull(text)).setDuration(5000).show());
                }
            });

        } else {
            loaderModule.hideProgressingView();
            Alerter.create(activity.init()).setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("შეცდომა სინქრონიზაციის დროს").setDuration(5000).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager internet = (ConnectivityManager) activity.init().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = internet.getActiveNetwork();
        return network != null;
    }


    public Map<String, Feature> getFeatures (FeatureQueryResult HouseResult) {
        Map<String, Feature> features = new HashMap<>();
        for (Feature feature : HouseResult) {
            features.put(Objects.requireNonNull(feature.getAttributes().get("house_code")).toString(), feature);
        }
        return features;
    }

    public static void clear () {
        INSTANCE = null;
    }
}

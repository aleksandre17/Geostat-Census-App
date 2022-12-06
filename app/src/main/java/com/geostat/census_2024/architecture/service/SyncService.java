package com.geostat.census_2024.architecture.service;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.App;
import com.geostat.census_2024.architecture.future.CallbackFuture;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.geostat.census_2024.architecture.manager.ArcgisManager;
import com.geostat.census_2024.architecture.module.LoaderModule;
import com.geostat.census_2024.data.local.entities.InquireV1Entity;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.data.model.DistrictAreal;
import com.geostat.census_2024.data.model.HouseModel;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.data.repository.AddressingRepository;
import com.geostat.census_2024.data.response.model.ResponseHouseModel;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyncService {

    private static volatile SyncService INSTANCE;

    private final LoaderModule loaderModule;
    private final AddressingRepository addressingRepository;
    private final UserModel userModel;
    private final ThatActivity<AppCompatActivity> activity;

    public SyncService(ThatActivity<AppCompatActivity> activity) {
        loaderModule = new LoaderModule(activity);
        this.activity = activity;
        this.userModel = activity.getUser();
        this.addressingRepository = new AddressingRepository(((Activity) activity).getApplication());
    }

    public static SyncService getInstance(final ThatActivity<AppCompatActivity> activity) {
        if (INSTANCE == null) { synchronized (SyncService.class) { if (INSTANCE == null) { INSTANCE = new SyncService(activity); } } }
        return INSTANCE;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager internet = (ConnectivityManager) activity.init().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = internet.getActiveNetwork();
        return network != null;
    }

    public void sync () {
        if (isNetworkAvailable()) {
            loaderModule.showProgressingView();

            try {
                final ArcgisManager arcgisController = activity.getArcgisController();
                if (userModel == null) throw new UserNotAuthenticatedException("კიდევ სცადეთ!");
                else if (arcgisController == null) throw new NotLinkException("დაელოდეთ რუკის ჩატვირთვას!");
                else {

                    final Application application = ((Activity) activity).getApplication();
                    final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    final ListenableFuture<FeatureQueryResult> queryResult = arcgisController.getMapService().queryResult("saregistracio", "fid = " + String.valueOf(userModel.getProperty("fid")));

                    FeatureQueryResult result = queryResult.get();
                    Feature districtFeature = result.iterator().next();

                    if (districtFeature != null) {

                        Map<String, Object> distAttributes = districtFeature.getAttributes();
                        DistrictAreal districtAreal = gson.fromJson(gson.toJson(distAttributes), DistrictAreal.class);
                        districtAreal.setGeometry(districtFeature.getGeometry().toJson());

                        /// Houses

                        final ListenableFuture<FeatureQueryResult> queryResultForHouse = arcgisController.getMapService().queryResult("house_point", "distr_id = '" + distAttributes.get("distr_id") + "' AND instr_id = '" + distAttributes.get("instr_id") + "' AND (status=1 OR ((status = 2 OR status = 7 OR status = 4) AND (sent is null Or sent = 0)) Or status = 3)");
                        FeatureQueryResult HouseResult = queryResultForHouse.get();
                        Map<String, Feature> houseMap = getFeatures(HouseResult);
                        houseMap.entrySet().parallelStream().forEach(e -> Log.d(getClass().getName(), "sync: " + e.getValue().getAttributes().get("status") + " " + e.getValue().getAttributes().get("sent")));
                        districtAreal.setHouses(getHousesForDistrictAreal(houseMap, districtAreal.getDistr_code()));

                        OkHttpClient client = new OkHttpClient();
                        CallbackFuture future = new CallbackFuture();
                        client.newCall(sentReq(districtAreal, application)).enqueue(future);

                        Response response = future.get();
                        receiverRes(response, districtAreal, houseMap);

                    }
                }
            } catch (NotLinkException | UserNotAuthenticatedException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
                loaderModule.hideProgressingView();

                String text = e.getMessage();
                Alerter.create(activity.init())
                        .setTitle("შეტყობინება")
                        .setBackgroundColorRes(R.color.red).setText(Objects.requireNonNull(text)).setDuration(5000).show();
            }
        } else {
            Alerter.create(activity.init())
                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("სინქრონიზაციის შესასრულებლად დაუკავშრდით ინტერნეტს!").setDuration(5000).show();
        }
    }

    private Request sentReq(DistrictAreal districtAreal, Application application) {
        final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String districtArealJson = gson.toJson(districtAreal);
        districtAreal.getHouses().forEach(new Consumer<HouseModel>() {
            @Override
            public void accept(HouseModel houseModel) {
                Log.d(getClass().getName(), "sentReq: getStatus " + houseModel.getStatus());
            }
        });
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, districtArealJson);

        return new Request.Builder().header("Authorization", "Bearer " + userModel.getToken()).url(((App) application).SYNC_URL).post(body).build();
    }

    private Request sentHouseRemoveReq(List<String> houseCodes, Application application) {
        final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String districtArealJson = gson.toJson(houseCodes);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, districtArealJson);

        return new Request.Builder().header("Authorization", "Bearer " + userModel.getToken()).url(((App) application).HOUSES_REMOVE_URL).post(body).build();
    }

    private void receiverRes (Response response, DistrictAreal districtAreal, Map<String, Feature> houseMap) {
        if (response.code() == 200) {
            final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            final ArcgisManager arcgisController = activity.getArcgisController();

            FeatureTable featureTable = Objects.requireNonNull(activity.getMapViewModel().getTouchableLayer().getValue()).getFeatureTable();
            Handler handler = new Handler();

            int[] index = new int[]{1};
            districtAreal.getHouses().parallelStream().forEach(entry -> {
                Integer status = Integer.parseInt(Objects.requireNonNullElse(Objects.requireNonNull(houseMap.get(entry.getHouse_code())).getAttributes().get("status"), 0).toString());
                Integer sent = Integer.parseInt(Objects.requireNonNullElse(Objects.requireNonNull(houseMap.get(entry.getHouse_code())).getAttributes().get("sent"), 0).toString());

                if (Arrays.asList(2, 7, 4).contains(status) && sent.equals(0)) {
                    handler.postDelayed(() -> {
                        Feature feature = Objects.requireNonNull(houseMap.get(entry.getHouse_code()));
                        feature.getAttributes().put("sent", 1);
                        // if (Objects.requireNonNull(feature.getAttributes().get("status")).equals(4)) feature.getAttributes().put("status", 5);
                        featureTable.updateFeatureAsync(houseMap.get(entry.getHouse_code()));
                    }, 50L * index[0]);

                }
                index[0]++;
            });

            Executor executor = Executors.newFixedThreadPool(3);
            executor.execute(() -> {

                try {

                    final ListenableFuture<FeatureQueryResult> queryResultForHouse = arcgisController.getMapService().queryResult("house_point", "distr_id = '" + districtAreal.getDistr_id() + "' AND instr_id = '" + districtAreal.getInstr_id() + "' AND (status=1 OR status=2 OR status=4 OR status=7 OR status = 3)");
                    final FeatureQueryResult featuresForFilter = queryResultForHouse.get();
                    Map<String, Feature> featuresMap = getFeatures(featuresForFilter);

                    List<ResponseHouseModel> callbackHouses = gson.fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<List<ResponseHouseModel>>() {}.getType());

//                    Map<Integer, Map<Integer, List<ResponseHouseModel>>> listMap = callbackHouses.parallelStream().map((e) -> { e.setFeature(featuresMap.get(e.getHouse_code())); return e;}).collect(Collectors.groupingBy(r -> {
//                        if (r.getAddresses().isEmpty() || r.getAddresses() == null) {
//                            return 0;
//                        } else {
//                            return 1;
//                        }
//                    }, Collectors.groupingBy(ResponseHouseModel::getStatus)));
//
//                    listMap.forEach((key, value) -> {
//                        Log.d("learning", "getKey: 1 --- " + key);
//                        value.entrySet().parallelStream().forEach(integerListEntry -> {
//                            Log.d("learning", "getKey: 2 --- " + integerListEntry.getKey());
//                            Log.d("learning", "accept: " + new GsonBuilder().serializeNulls().create().toJson(integerListEntry.getValue()));
//
//                            List<Feature> features = integerListEntry.getValue().stream().map(ResponseHouseModel::getFeature).filter(Objects::nonNull).collect(Collectors.toList());
//
//                            if (integerListEntry.getKey().equals(3) && key.equals(1)) {
//                                List<InquireV1Entity> inquireV1Entities = integerListEntry.getValue().stream().flatMap(i -> i.getAddresses().stream()).collect(Collectors.toList());
//                                features.forEach(feature -> {
//                                    feature.getAttributes().put("status", 3);
//                                    feature.getAttributes().put("sent", null);
//                                });
//                                try {
//                                    Integer l = activity.getMapViewModel().updateAddressingStatus(inquireV1Entities, 2);
//                                    featureTable.updateFeaturesAsync(features).addDoneListener(() -> {
//                                    });
//                                } catch (Exception e) {
//                                    Log.d("TAG", "e: " + e.getMessage());
//                                    e.printStackTrace();
//                                }
//
//                            } else if (integerListEntry.getKey().equals(5)) {
//                                features.forEach(feature -> feature.getAttributes().put("status", 5));
//                                if (key.equals(0)) {
//                                    featureTable.updateFeaturesAsync(features).addDoneListener(() -> {
//                                    });
//                                } else if (key.equals(1)) {
//                                    Log.d("REMOVING", "accept: ");
//                                    List<String> houseCodes = integerListEntry.getValue().stream().map(ResponseHouseModel::getHouse_code).collect(Collectors.toList());
//                                    activity.getMapViewModel().removeAddressing(houseCodes);
//                                    featureTable.deleteFeaturesAsync(features).addDoneListener(() -> {
//                                    });
//                                }
//                            }
//                        });
//                    });

                    callbackHouses.parallelStream().forEach(house -> {

                        Log.d(getClass().getName(), "receiverRes: house" + gson.toJson(house));

                        Feature feature = featuresMap.get(house.getHouse_code());

                        if (feature != null) {

                            if (house.getStatus().equals(5)) {
                                feature.getAttributes().put("sent", 1);
                                if (house.getAddresses().isEmpty()) {
                                    feature.getAttributes().put("status", 5);
                                    featureTable.updateFeatureAsync(feature).addDoneListener(() -> {});
                                } else {
                                    // activity.getMapViewModel().removeAddressing(feature);
                                    featureTable.deleteFeatureAsync(feature).addDoneListener(() -> {});
                                }
                            }else if (house.getStatus().equals(3)) {
                                feature.getAttributes().put("status", 3);
                                feature.getAttributes().put("sent", null);
                                try {
                                    Integer l = activity.getMapViewModel().updateAddressingStatus(house.getAddresses(), 2);
                                    featureTable.updateFeatureAsync(feature).addDoneListener(() -> {});
                                } catch (Exception e) {
                                    Log.d("TAG", "e: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                    if (callbackHouses.stream().anyMatch(responseHouseModel -> !responseHouseModel.getAddresses().isEmpty())) {
                        List<String> uuids = callbackHouses.stream().filter(house -> !house.getAddresses().isEmpty()).map(ResponseHouseModel::getHouse_code).collect(Collectors.toList());
                        OkHttpClient client = new OkHttpClient();
                        //CallbackFuture future = new CallbackFuture();
                        Request request = sentHouseRemoveReq(uuids, ((Activity) activity).getApplication());
                        //client.newCall(request).enqueue(future);

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, IOException e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                try {
                                    int num = Integer.parseInt(response.body().string());
                                    Log.d(getClass().getName(), "onResponse: " + num);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }


                    (activity.init()).runOnUiThread(() -> new Handler().postDelayed(() -> {
                        loaderModule.hideProgressingView();
                        Alerter.create(activity.init())
                                .setTitle("შეტყობინება").setBackgroundColorRes(R.color.green).setText("სინქრონიზაცია შესრულდა წარმატებით").setDuration(5000).show();
                    }, 2000));

                } catch (IOException | ExecutionException | InterruptedException e) {
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

    public List<HouseModel> getHousesForDistrictAreal (Map<String, Feature> houseMap, String district) {

        final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        return houseMap.values().parallelStream().map(feature -> {
            HouseModel houseModel = gson.fromJson(gson.toJson(feature.getAttributes()), HouseModel.class);
            houseModel.setGeometry(feature.getGeometry().toJson());
            houseModel.setDistr_code(district);
            List<AddressingWithHolders> addressing = addressingRepository.fetch(houseModel.getHouse_code());
            houseModel.setAddressing(addressing);
            return houseModel;
        }).collect(Collectors.toList());
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


//                        Map<String, Feature> tmp = houseResults.entrySet().stream().filter((a) -> {
//                            Integer status = Integer.parseInt(Objects.requireNonNullElse(a.getValue().getAttributes().get("status"), 0).toString());
//                            Integer sent = Integer.parseInt(Objects.requireNonNullElse(a.getValue().getAttributes().get("sent"), 0).toString());
//                            return Arrays.asList(1,3).contains(status) || (Arrays.asList(2,4,7).contains(status) && sent.equals(0));
//                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
package com.geostat.census_2024.architecture.service;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Handler;
import android.security.keystore.UserNotAuthenticatedException;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final AddressingRepository addressingRepository;
    private final UserModel userModel;
    private final ThatActivity<AppCompatActivity> activity;
    private ExecutorService service;
    private LoaderModule loaderModule;

    public SyncService(ThatActivity<AppCompatActivity> activity) {
        service = Executors.newCachedThreadPool();
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
            activity.init().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            service.submit(() -> {
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
                            districtAreal.setHouses(getHousesForDistrictAreal(houseMap, districtAreal.getDistr_code()));

                            OkHttpClient client = new OkHttpClient();
                            CallbackFuture future = new CallbackFuture();
                            client.newCall(sentReq(districtAreal, application)).enqueue(future);

                            Response response = future.get();
                            receiverRes(response, districtAreal, houseMap);

                        } else {
                            sayHayMainThread("სააღწერო უბანი არ მოიძებნა!", R.color.yellow, null);
                        }
                    }
                } catch (IOException | UserNotAuthenticatedException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    sayHayMainThread("", R.color.red, e);
                }
            });
        } else {
            Alerter.create(activity.init())
                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("სინქრონიზაციის შესასრულებლად დაუკავშრდით ინტერნეტს!").setDuration(5000).show();
        }
    }

    private Request sentReq(DistrictAreal districtAreal, Application application) {
        final Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        String districtArealJson = gson.toJson(districtAreal);
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
            final FeatureTable featureTable = Objects.requireNonNull(activity.getMapViewModel().getTouchableLayer().getValue()).getFeatureTable();

            service.submit(() -> {
                try {
                    final ListenableFuture<FeatureQueryResult> queryResultForHouse = arcgisController.getMapService().queryResult("house_point", "distr_id = '" + districtAreal.getDistr_id() + "' AND instr_id = '" + districtAreal.getInstr_id() + "' AND (status=1 OR status=2 OR status=4 OR status=7 OR status = 3)");
                    final FeatureQueryResult featuresForFilter = queryResultForHouse.get();
                    final Map<String, Feature> featuresMap = getFeatures(featuresForFilter);
                    final List<ResponseHouseModel> callbackHouses = gson.fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<List<ResponseHouseModel>>() {}.getType());

                    Map<Integer, Map<Integer, List<ResponseHouseModel>>> filteredFeatures = callbackHouses.parallelStream().map((e) -> { e.setFeature(featuresMap.get(e.getHouse_code())); return e;})
                            .collect(Collectors.groupingBy(r -> (r.getAddresses().isEmpty() || r.getAddresses() == null) ? 0 : 1, Collectors.groupingBy(ResponseHouseModel::getStatus)));

                    filteredFeatures.forEach((key, value) -> value.entrySet().parallelStream().forEach(integerListEntry -> {
                        List<Feature> features = integerListEntry.getValue().stream().map(ResponseHouseModel::getFeature).filter(Objects::nonNull).collect(Collectors.toList());

                        if (integerListEntry.getKey().equals(3) && key.equals(1)) {
                            List<InquireV1Entity> inquireV1Entities = integerListEntry.getValue().stream().flatMap(i -> i.getAddresses().stream()).collect(Collectors.toList());
                            features.forEach(feature -> { feature.getAttributes().put("status", 3); feature.getAttributes().put("sent", null); });

                            Integer l = activity.getMapViewModel().updateAddressingStatus(inquireV1Entities, 2);
                            featureTable.updateFeaturesAsync(features).addDoneListener(() -> {});

                        } else if (integerListEntry.getKey().equals(5)) {
                            features.forEach(feature -> feature.getAttributes().put("status", 5));
                            if (key.equals(0)) {
                                featureTable.updateFeaturesAsync(features).addDoneListener(() -> {});
                            } else if (key.equals(1)) {
                                List<String> houseCodes = integerListEntry.getValue().stream().map(ResponseHouseModel::getHouse_code).collect(Collectors.toList());
                                activity.getMapViewModel().removeAddressing(houseCodes);
                                featureTable.deleteFeaturesAsync(features).addDoneListener(() -> {});
                            }
                        }
                    }));

                    if (callbackHouses.stream().anyMatch(responseHouseModel -> !responseHouseModel.getAddresses().isEmpty())) {
                        List<String> uuids = callbackHouses.stream().filter(house -> !house.getAddresses().isEmpty()).map(ResponseHouseModel::getHouse_code).collect(Collectors.toList());
                        OkHttpClient client = new OkHttpClient();
                        Request request = sentHouseRemoveReq(uuids, ((Activity) activity).getApplication());
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                e.printStackTrace();
                            }
                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response1) {
                                prevent(districtAreal, houseMap, featureTable, callbackHouses);
                            }
                        });
                    } else {
                        prevent(districtAreal, houseMap, featureTable, callbackHouses);
                    }
                    sayHayMainThread("სინქრონიზაცია შესრულდა წარმატებით", R.color.green, null);

                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    String text = (e instanceof SocketTimeoutException) ? "სცადეთ სხვა ინტენეტთად დაკავშრება" : e.getMessage();
                    sayHayMainThread(text, R.color.red, null);
                }
            });

        } else {
            sayHayMainThread("შეცდომა სინქრონიზაციის დროს", R.color.red, null);
        }
    }

    private void sayHayMainThread (String t, Integer c, @Nullable Exception e) {
        String text = e != null ? e.getMessage() : t;
        activity.init().runOnUiThread(() -> new Handler().post(() -> {
            loaderModule.hideProgressingView();
            activity.init().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Alerter.create(activity.init()).setTitle("შეტყობინება").setBackgroundColorRes(c).setText(Objects.requireNonNull(text)).setDuration(5000).show();
        }));
    }

    private void prevent (DistrictAreal districtAreal, Map<String, Feature> houseMap, FeatureTable featureTable, List<ResponseHouseModel> callbackHouses) {
        final List<String> callbackHouseCodes = callbackHouses.stream().map(ResponseHouseModel::getHouse_code).collect(Collectors.toList());

        service.submit(() -> {
            final List<Feature> lFeatures = districtAreal.getHouses()
                    .stream()
                    .filter(houseModel -> Arrays.asList(2, 7, 4).contains(houseModel.getStatus()) && !callbackHouseCodes.contains(houseModel.getHouse_code()))
                    .map(houseModel -> houseMap.get(houseModel.getHouse_code()))
                    .filter(Objects::nonNull)
                    .filter(feature -> feature != null && feature.getAttributes() != null && (feature.getAttributes().get("sent") == null || Objects.equals(feature.getAttributes().get("sent"), 0))).collect(Collectors.toList());
            lFeatures.forEach(i -> i.getAttributes().put("sent", 1));
            if (!lFeatures.isEmpty()) featureTable.updateFeaturesAsync(lFeatures);
        });
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
        if (INSTANCE != null) {
            if (INSTANCE.service != null && !INSTANCE.service.isTerminated()) {
                INSTANCE.service.shutdown();
                INSTANCE.service.shutdownNow();
            }
            INSTANCE.service = null; INSTANCE.loaderModule = null; INSTANCE = null;
        }
    }
}


//                        Map<String, Feature> tmp = houseResults.entrySet().stream().filter((a) -> {
//                            Integer status = Integer.parseInt(Objects.requireNonNullElse(a.getValue().getAttributes().get("status"), 0).toString());
//                            Integer sent = Integer.parseInt(Objects.requireNonNullElse(a.getValue().getAttributes().get("sent"), 0).toString());
//                            return Arrays.asList(1,3).contains(status) || (Arrays.asList(2,4,7).contains(status) && sent.equals(0));
//                        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
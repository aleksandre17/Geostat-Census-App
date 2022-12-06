package com.geostat.census_2024.data;

import android.app.Application;

import com.geostat.census_2024.architecture.App;
import com.geostat.census_2024.architecture.future.CallbackFuture;
import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.UserDao;
import com.geostat.census_2024.data.local.entities.UserEntity;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.data.request.model.UserRequestModel;
import com.geostat.census_2024.data.response.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private final String lgnUrl;
    public CensusDatabase db;
    private final UserDao userDao;

    public LoginDataSource(Application application) {
        db = CensusDatabase.getDatabase(application);
        userDao = db.userDao();
        lgnUrl = ((App) application).LGN_URL;
    }

    public Long insert(UserEntity userEntity) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> userDao.insert(userEntity)).get();
    }

    @SuppressWarnings("unchecked")
    public Result<UserModel> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            UserEntity localeUserEntity = localeAuth(username);
            if (localeUserEntity != null) {

                UserModel userModel = new UserModel();
                userModel.setId(localeUserEntity.getId());
                userModel.setUserName(localeUserEntity.getUserName());
                userModel.setPassword(localeUserEntity.getPassword());
                userModel.setToken(localeUserEntity.getToken());
                userModel.setDistrictNum(localeUserEntity.getUserName());

                return new Result.Success<UserModel>(userModel, "locale");

            } else {

                Response response = serverAuth(username, password);

                if (response.code() == 200) {
                    Gson gson = new Gson();
                    String userJson = response.header("user");

                    UserModel userModel = gson.fromJson(userJson, UserModel.class);
                    userModel.setToken(response.header("token"));
                    userModel.setPassword(password);
                    userModel.setDistrictNum(userModel.getUserName());

                    return new Result.Success<UserModel>(userModel, "server");
                } else {
                    return new Result.Error<Exception>(new IOException("Error logging in"));
                }
            }

        } catch (Exception e) {
            return new Result.Error<Exception>(new IOException("Error logging in", e));

        }
    }

    public UserEntity localeAuth (String username) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> userDao.find(username)).get();
    }

    public Response serverAuth (String username, String password) throws ExecutionException, InterruptedException {

        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(new UserRequestModel(username, password));

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody req = RequestBody.create(JSON, json);

        Request request = new Request.Builder().url(lgnUrl).post(req).build();

        CallbackFuture future = new CallbackFuture();
        client.newCall(request).enqueue(future);

        return future.get();
    }

    public void logout() {
        // db = null;
        // TODO: revoke authentication
    }
}
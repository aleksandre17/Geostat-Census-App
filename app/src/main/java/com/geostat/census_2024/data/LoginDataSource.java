package com.geostat.census_2024.data;

import android.app.Application;

import com.geostat.census_2024.architecture.App;
import com.geostat.census_2024.architecture.future.CallbackFuture;
import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.UserDao;
import com.geostat.census_2024.data.request.User;
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

    public Long insert(com.geostat.census_2024.data.local.entities.User user) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> userDao.insert(user)).get();
    }

    @SuppressWarnings("unchecked")
    public Result<com.geostat.census_2024.data.model.User> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            com.geostat.census_2024.data.local.entities.User localeUser = localeAuth(username);
            if (localeUser != null) {

                com.geostat.census_2024.data.model.User user = new com.geostat.census_2024.data.model.User();
                user.setId(localeUser.getId());
                user.setUserName(localeUser.getUserName());
                user.setPassword(localeUser.getPassword());
                user.setToken(localeUser.getToken());
                user.setDistrictNum(localeUser.getUserName());

                return new Result.Success<com.geostat.census_2024.data.model.User>(user, "locale");

            } else {

                Response response = serverAuth(username, password);

                if (response.code() == 200) {
                    Gson gson = new Gson();
                    String userJson = response.header("user");

                    com.geostat.census_2024.data.model.User user = gson.fromJson(userJson, com.geostat.census_2024.data.model.User.class);
                    user.setToken(response.header("token"));
                    user.setPassword(password);
                    user.setDistrictNum(user.getUserName());

                    return new Result.Success<com.geostat.census_2024.data.model.User>(user, "server");
                } else {
                    return new Result.Error<Exception>(new IOException("Error logging in"));
                }
            }

        } catch (Exception e) {
            return new Result.Error<Exception>(new IOException("Error logging in", e));

        }
    }

    public com.geostat.census_2024.data.local.entities.User localeAuth (String username) throws ExecutionException, InterruptedException {
        return db.censusWriterExecutor.submit(() -> userDao.find(username)).get();
    }

    public Response serverAuth (String username, String password) throws ExecutionException, InterruptedException {

        Gson gson = new GsonBuilder().serializeNulls().create();
        String json = gson.toJson(new User(username, password));

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
package com.geostat.census_2024.data.repository;


import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.response.Result;
import com.geostat.census_2024.data.local.entities.UserEntity;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.utility.SharedPref;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private static UserModel userModel = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;

        String isUser = SharedPref.read("user", null);

        if (isUser != null && userModel == null) {
            userModel = new GsonBuilder().create().fromJson(isUser, UserModel.class);
        }
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public UserModel getUser() {
        if (!isLoggedIn()) return null;
        return userModel;
    }

    public boolean isLoggedIn() {
        return userModel != null && SharedPref.read("isLoggedIn", false);
    }

    public void logout() {
        userModel = null;

        SharedPref.remove("user");
        SharedPref.remove("isLoggedIn");
        dataSource.logout();
    }

    public UserModel updateUserProps (Map<String, Object> props) {

        if (isLoggedIn()) {

            userModel.setProperty("fid", props.get("fid"));
            userModel.setProperty("region_id", props.get("region_id"));
            userModel.setProperty("munic_id", props.get("munic_id"));
            userModel.setProperty("instr_id", props.get("instr_id"));
            userModel.setProperty("distr_id", props.get("distr_id"));

            setLoggedInUser(userModel);
        }

        return userModel;
    }

    private void setLoggedInUser(UserModel userModel) {

        SharedPref.write("isLoggedIn", true);
        SharedPref.write("user", new Gson().toJson(userModel));

        this.userModel = userModel;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<UserModel> login(String username, String password) {
        // handle login
        Result<UserModel> result = dataSource.login(username, password);

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<UserModel>) result).getData());
        }
        return result;
    }

    public Long insert(UserEntity userEntity) throws ExecutionException, InterruptedException {
        return dataSource.insert(userEntity);
    }
}
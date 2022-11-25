package com.geostat.census_2024.data;


import com.geostat.census_2024.data.model.User;
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
    private static User user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;

        String isUser = SharedPref.read("user", null);

        if (isUser != null && user == null) {
            user = new GsonBuilder().create().fromJson(isUser, User.class);
        }
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public User getUser() {
        if (!isLoggedIn()) return null;
        return user;
    }

    public boolean isLoggedIn() {
        return user != null && SharedPref.read("isLoggedIn", false);
    }

    public void logout() {
        user = null;

        SharedPref.remove("user");
        SharedPref.remove("isLoggedIn");
        dataSource.logout();
    }

    public User updateUserProps (Map<String, Object> props) {

        if (isLoggedIn()) {

            user.setProperty("fid", props.get("fid"));
            user.setProperty("region_id", props.get("region_id"));
            user.setProperty("munic_id", props.get("munic_id"));
            user.setProperty("instr_id", props.get("instr_id"));
            user.setProperty("distr_id", props.get("distr_id"));

            setLoggedInUser(user);
        }

        return user;
    }

    private void setLoggedInUser(User user) {

        SharedPref.write("isLoggedIn", true);
        SharedPref.write("user", new Gson().toJson(user));

        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<User> login(String username, String password) {
        // handle login
        Result<User> result = dataSource.login(username, password);

        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<User>) result).getData());
        }
        return result;
    }

    public Long insert(com.geostat.census_2024.data.local.entities.User user) throws ExecutionException, InterruptedException {
        return dataSource.insert(user);
    }
}
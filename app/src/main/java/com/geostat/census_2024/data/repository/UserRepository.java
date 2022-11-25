package com.geostat.census_2024.data.repository;

import android.app.Application;

import com.geostat.census_2024.data.local.CensusDatabase;
import com.geostat.census_2024.data.local.dai.UserDao;

public class UserRepository {

    public CensusDatabase db;
    private final UserDao userDao;

    public UserRepository(Application application) {
        db = CensusDatabase.getDatabase(application);

        userDao = db.userDao();
    }

}

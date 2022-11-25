package com.geostat.census_2024.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.geostat.census_2024.data.local.dai.AddressDao;
import com.geostat.census_2024.data.local.dai.AddressingDao;
import com.geostat.census_2024.data.local.dai.BuildingTypeDao;
import com.geostat.census_2024.data.local.dai.LivingStatusDao;
import com.geostat.census_2024.data.local.dai.UserDao;
import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.data.local.entities.Addressing;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.Holder;
import com.geostat.census_2024.data.local.entities.LivingStatus;
import com.geostat.census_2024.data.local.entities.SupervisionEntity;
import com.geostat.census_2024.data.local.entities.Tag;
import com.geostat.census_2024.data.local.entities.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = { User.class, Address.class, Addressing.class, Holder.class, SupervisionEntity.class, BuildingType.class, LivingStatus.class, Tag.class }, version = 2, exportSchema = true)
public abstract class CensusDatabase extends RoomDatabase {

    private static volatile CensusDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract AddressDao addressDao();
    public abstract AddressingDao addressingDao();

    public abstract BuildingTypeDao buildingTypeDao();
    public abstract LivingStatusDao livingStatusDao();

    public final ExecutorService censusWriterExecutor = Executors.newFixedThreadPool(4);

    public static CensusDatabase getDatabase(final Context rep) {
        if (INSTANCE == null) {
            synchronized (CensusDatabase.class) {
                if (INSTANCE == null) {

                    // rep.deleteDatabase("census_database");
                    INSTANCE = Room.databaseBuilder(rep.getApplicationContext(), CensusDatabase.class, "census_database").addCallback(new Callback() {
                        public void onCreate (@NonNull SupportSQLiteDatabase db) {
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.execute(() -> {

                            });
                        }
                        public void onOpen (@NonNull SupportSQLiteDatabase db) {

                            // do something every time database is open
                        }
                    }).build(); // fallbackToDestructiveMigration .allowMainThreadQueries().fallbackToDestructiveMigration()

                }
            }
        }
        if (!INSTANCE.isOpen()) {
            INSTANCE.getOpenHelper().getWritableDatabase();
        }

        return INSTANCE;
    }


}


//new Migration(3, 4) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//        database.execSQL("ALTER TABLE `addressings_table` ADD COLUMN mapId INTEGER DEFAULT 1 NOT NULL");
//        }
//        }, new Migration(4, 5) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//        database.execSQL("CREATE TABLE if not exists `tags_table` (id INTEGER PRIMARY KEY NOT NULL, type INTEGER NOT NULL, title TEXT NOT NUll)");
//        database.execSQL("CREATE UNIQUE INDEX index_tags_table_type_title on `tags_table` (`type` ASC, `title` ASC)");
//        }
//        }, new Migration(5, 6) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//        database.execSQL("CREATE TABLE if not exists `users` (id INTEGER PRIMARY KEY NOT NULL, firstName TEXT NOT NULL, lastName TEXT NOT NUll, email TEXT NOT NULL, encryptedPassword TEXT NOT NULL)");
//        database.execSQL("CREATE UNIQUE INDEX index_users_id on `users` (`id` ASC)");
//        }
//        }, new Migration(6, 7) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//        database.execSQL("ALTER TABLE `addressings_table` ADD COLUMN uuid TEXT NOT NULL");
//        }
//        }, new Migration(7, 8) {
//@Override
//public void migrate(@NonNull SupportSQLiteDatabase database) {
//        database.execSQL("ALTER TABLE `addressings_table` ADD COLUMN house_code TEXT DEFAULT ''");
//        }
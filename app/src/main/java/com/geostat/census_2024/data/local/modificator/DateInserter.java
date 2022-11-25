package com.geostat.census_2024.data.local.modificator;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateInserter {

    @TypeConverter
    public static Date toDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        return new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime();
    }
}

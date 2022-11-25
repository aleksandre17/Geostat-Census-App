package com.geostat.census_2024.utility;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InterDate {

    public static String[] lDate (Date date) {
        String[] dates = new String[2];

        @SuppressLint("SimpleDateFormat") String dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
        @SuppressLint("SimpleDateFormat") String timeFormat = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH).format(date);
        dates[0] = dateFormat;
        dates[1] = timeFormat;
        return dates;
    }


}

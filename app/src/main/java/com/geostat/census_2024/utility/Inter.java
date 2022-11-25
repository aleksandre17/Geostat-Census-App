package com.geostat.census_2024.utility;

import androidx.annotation.Nullable;
import androidx.databinding.InverseMethod;

public class Inter {

    @InverseMethod("toInt")
    @Nullable
    public static String toString(@Nullable Integer num) {
        return num == null || num == 0 ? null : String.valueOf(num);
    }

    @Nullable
    public static Integer toInt(@Nullable String integer) {
        if (integer == null || integer.isEmpty()) return null;

        Integer l = null;
        try{
            l = Integer.parseInt(integer);
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return l == null || l == 0 ? null : Integer.valueOf(integer);
    }


//    @InverseMethod("toInt")
//    public static String toString(Integer num) {
//        return num == null || num == 0 ? null : String.valueOf(num);
//    }
//
//    public static Integer toInt(String integer) {
//        Log.d("TRY", "toInt: " + integer);
//        Integer l = null;
//        try{
//            l = Integer.parseInt(integer);
//        }catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        return l == null || l == 0 ? 0 : Integer.valueOf(integer);
//    }
}

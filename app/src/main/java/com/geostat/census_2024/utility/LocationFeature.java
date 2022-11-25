package com.geostat.census_2024.utility;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationFeature {

    public static String TAG = LocationFeature.class.getName();

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location = null;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 1000;
    protected LocationManager locationManager;
    static Context mcontext;
    private static LocationFeature instance;


    public static synchronized LocationFeature getInstance(Context ctx) {
        mcontext = ctx;
        if (instance == null) {
            instance = new LocationFeature();
        }
        return instance;
    }

    myListener listener;
    public void start(myListener listener) {
        this.listener=listener;
        stopLocationUpdates();
        displayLocation();
    }

    private void displayLocation() {
        try {

            Location location = getLocation();
            if (location != null) {
                updateLattitudeLongitude(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            Log.d(TAG, "displayLocation: " + e.getMessage());
        }
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                this.canGetLocation = false;
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {

                    Log.d(TAG + "-->Network", "Network Enabled");

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationProviderListener);
                        return location;
                    }

                } else if (isGPSEnabled) {

                    Log.d(TAG + "-->GPS", "GPS Enabled");

                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationProviderListener);

                        return location;
                    }
                }
            }

        } catch (SecurityException e) {
            Log.d(TAG, "getLocation: " + e.getMessage());
        }
        return location;
    }

    public void updateLattitudeLongitude(double latitude, double longitude) {
        Log.d(TAG, "updated Lat == " + latitude + "  updated long == " + longitude);
//        SharedPreferenceManager sharedPreferenceManager = SharedPreferenceManager.getInstance();
//        sharedPreferenceManager.updateUserDeviceLatLong(latitude, longitude);


        listener.onUpdate(latitude, longitude);
    }

    public void stopLocationUpdates(){
        try {
            if (locationManager != null) {
                // Logger.i(TAG,"stopLocationUpdates");
                // Log.d(TAG, "stopLocationUpdates");
                locationManager.removeUpdates(locationProviderListener);
                locationManager = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public LocationListener locationProviderListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            try {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Location l = getLocation();
                if (l == null) {
                    updateLattitudeLongitude(latitude, longitude);
                }

            } catch (Exception e) {
                e.printStackTrace();
//                ExceptionHandler.printStackTrace(e);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {


        }

        @Override
        public void onProviderEnabled(String s) {


        }

        @Override
        public void onProviderDisabled(String s) {


        }
    };

    public interface myListener{
        void onUpdate(double latt,double longg);
    }
}

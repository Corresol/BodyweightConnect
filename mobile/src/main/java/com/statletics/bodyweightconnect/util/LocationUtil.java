package com.statletics.bodyweightconnect.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.statletics.bodyweightconnect.type.Location;

import java.util.List;

/**
 * Created by Tonni on 13.10.2016.
 */

public class LocationUtil implements LocationListener {

    private static LocationUtil instance;
    private double longitude;
    private double latitude;
    private int currentSatsInUse=0;
    private LocationManager locationManager;

    private LocationUtil(Activity activity) {
        Log.e("LocationUtil", "Check Permission....");
        Context context = ActivityManager.getInstance().getCurrentActivity().getApplicationContext();
        if ( ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationUtil", "Check Permission....DENIED");
            return  ;
        }
        Log.e("LocationUtil", "Check Permission....GRANTED");
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);
        for(String s:providers){
            Log.e("LocationUtil","Enabled Provider : "+s);
        }

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        }

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
        }

        GpsStatus status = locationManager.getGpsStatus(null);
        Log.e("LocationUtil", "GPSStatus:"+status.getTimeToFirstFix());
    }

    public static LocationUtil getInstance(Activity activity){
        if(instance==null){
            instance = new LocationUtil(activity);
        }
        return instance;
    }


    public Location getLocation(){
        return new Location(longitude,latitude);
    }

    public int getCurrentSats(){
        return currentSatsInUse;
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        //Log.e("LocationUtil","Location changed...."+location.getProvider()+" / "+location.getLatitude());

        longitude = location.getLongitude();
        latitude = location.getLatitude();
        if(location.getProvider().equals("gps")) {
            currentSatsInUse = location.getExtras().getInt("satellites");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //Log.e("LocationUtil","Location onStatusChanged...."+s+" / "+i);
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

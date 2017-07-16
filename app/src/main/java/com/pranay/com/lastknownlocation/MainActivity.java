package com.pranay.com.lastknownlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private int FASTEST_INTERVAL = 8 * 1000; // 8 SECOND
    private int UPDATE_INTERVAL = 2000; // 2 SECOND
    private int FINE_LOCATION_REQUEST = 888;
    private Toast toast;
    private LocationRequest locationRequest;

    private TextView tvLocationDetails;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViewsAndListener();
        if (checkPermissions()) {
            initLocationUpdate();
        }

    }

    private void initViewsAndListener() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        tvLocationDetails=findViewById(R.id.tvLocationDetails);
        mainLayout=findViewById(R.id.mainLayout);
        findViewById(R.id.btnGetLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    initLocationUpdate();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
        //Start Location update as define intervals
    private void initLocationUpdate() {

        // Check API revision for New Location Update
        //https://developers.google.com/android/guides/releases#june_2017_-_version_110

        //init location request to start retrieving location update
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval( UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        //Create LocationSettingRequest object using locationRequest
        LocationSettingsRequest.Builder locationSettingBuilder =new LocationSettingsRequest.Builder();
        locationSettingBuilder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSetting = locationSettingBuilder.build();

        //Need to check whether location settings are satisfied
        SettingsClient settingsClient= LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSetting);
        //More info :  // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient


        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
         FusedLocationProviderClient fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        //super.onLocationResult(locationResult);
                        if (locationResult != null) {
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    }

                    @Override
                    public void onLocationAvailability(LocationAvailability locationAvailability) {
                        super.onLocationAvailability(locationAvailability);
                    }
                },
        Looper.myLooper());

    }
    private void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                java.lang.Double.toString(location.getLatitude()) + "," +
                java.lang.Double.toString(location.getLongitude());
        tvLocationDetails.setText(msg);
        toast.setText(msg);
        toast.show();
    }


    private boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_REQUEST);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_REQUEST) {
            // Received permission result for Location permission.
            Log.i(TAG, "Received response for Location permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "Location permission has now been granted. Now call initLocationUpdate");
                initLocationUpdate();
            } else {
                Snackbar.make(mainLayout, R.string.rational_location_permission,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.ok), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions();
                            }
            }).show();

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    protected void onStop() {
        if (toast != null) {
            toast.cancel();
        }
        super.onStop();
    }


    @Override
    protected void onPause() {
        if (toast != null) {
            toast.cancel();
        }
        super.onPause();
        super.onPause();
    }

}

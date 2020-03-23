package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private SimpleLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = new SimpleLocation(this);

        findViewById(R.id.someView).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                // SimpleLocation.openSettings(this) does not work
//                // if we can't access the location yet
//                if (!location.hasLocationEnabled()) {
//                    // ask the user to enable location access
//                    SimpleLocation.openSettings(MainActivity.this);
//                }
                checkLocationPermission();
                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                // TODO
                Toast.makeText(MainActivity.this, "Latitude: " + latitude + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                getDistance(latitude, longitude);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        location.beginUpdates();

        // ...
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();

        // ...

        super.onPause();
    }

    private void getDistance(double latitude,double longitude) {
        // User location
        Location userLocation = new Location("");
        userLocation.setLatitude(latitude);
        userLocation.setLongitude(longitude);

        // TIEC Lab location
        Location targetLocation = new Location("");
        targetLocation.setLatitude(19.110851d);
        targetLocation.setLongitude(77.293924d);

        float distanceInMeters =  targetLocation.distanceTo(userLocation);
        Toast.makeText(this, "Distance: " + distanceInMeters, Toast.LENGTH_SHORT).show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                        recreate();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}

package com.example.android.sggstiec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "MainActivity";
    private SimpleLocation location;
    private Button scan_btn;
    public static  TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        location = new SimpleLocation(this);

        scan_btn = findViewById(R.id.scan_button);
        result = findViewById(R.id.result_text);

        marshmallowGPSPremissionCheck();

        scan_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                // SimpleLocation.openSettings(this) does not work
//                // if we can't access the location yet
//                if (!location.hasLocationEnabled()) {
//                    // ask the user to enable location access
//                    SimpleLocation.openSettings(MainActivity.this);
//                }
                final double latitude = location.getLatitude();
                final double longitude = location.getLongitude();

                // TODO
                Toast.makeText(MainActivity.this, "Latitude: " + latitude + "Longitude: " + longitude, Toast.LENGTH_SHORT).show();
                float distanceInMeters = getDistance(latitude, longitude);
                if (distanceInMeters <= 100) {
                    //TODO: CAMERA
                    startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "You are not near TIEC Lab", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
                }
            }

        });
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // make the device update its location
//        marshmallowGPSPremissionCheck();
//        location.beginUpdates();
//
//        // ...
//    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();

        // ...

        super.onPause();
    }

    private float getDistance(double latitude,double longitude) {
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

        return distanceInMeters;
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}
                            , MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            //   gps functions.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
        }
    }

}

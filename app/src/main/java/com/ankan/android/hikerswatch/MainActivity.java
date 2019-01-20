package com.ankan.android.hikerswatch;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView latTextView;
    TextView lonTextView;
    TextView accuracyTextView;
    TextView speedTextView;
    TextView addressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        accuracyTextView = findViewById(R.id.accuracyTextView);
        speedTextView = findViewById(R.id.speedTextView);
        addressTextView = findViewById(R.id.addressTextView);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.i("Status Changed", "Ok");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("Provider Enabled", "Ok");

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("Provider Disabled", "Ok");

            }
        };

        //asking for permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //if permission is not given
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //request for permission
        } else {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                if(lastKnownLocation != null) {
                    updateLocationInfo(lastKnownLocation);
                }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }

    public void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    public void updateLocationInfo(Location location) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(listAddress != null && listAddress.size() > 0) {
                String address = listAddress.get(0).getAddressLine(0);
                addressTextView.setText(address);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        latTextView.setText("Latitude: " + String.format("%.8f", location.getLatitude()));
        lonTextView.setText("Longitude: " + String.format("%.8f", location.getLongitude()));
        accuracyTextView.setText("Accuracy: " + String.format("%.8f", location.getAccuracy()));
        speedTextView.setText("Speed: " + String.format("%.2f", location.getSpeed() * 1.852) + "km/hr");

    }
}

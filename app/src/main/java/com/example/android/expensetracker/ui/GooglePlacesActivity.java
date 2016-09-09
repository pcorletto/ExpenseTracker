package com.example.android.expensetracker.ui;

// REFERENCES: Tutorial found at:
// http://javapapers.com/android/find-places-nearby-in-google-maps-using-google-places-apiandroid-app/

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.GooglePlacesReadTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.DecimalFormat;

public class GooglePlacesActivity extends FragmentActivity implements LocationListener {

    private static final String GOOGLE_API_KEY = "";
    GoogleMap googleMap;
    double currentLatitude = 0;
    double currentLongitude = 0;
    double placeLatitude = 0;
    double placeLongitude = 0;
    double distance = 0;
    private int PROXIMITY_RADIUS = 5000;
    String storeName;

    private Button previousActivityButton, returnMainActivityBtn, storePlaceButton, displayPlacesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_places);

        previousActivityButton = (Button) findViewById(R.id.previousActivityButton);
        returnMainActivityBtn = (Button) findViewById(R.id.returnMainActivityBtn);
        storePlaceButton = (Button) findViewById(R.id.storePlaceButton);
        displayPlacesButton = (Button) findViewById(R.id.displayPlacesButton);

        //show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        Intent intent = getIntent();
        storeName = intent.getStringExtra(getString(R.string.store_name));

        storeName = storeName.replaceAll(" ", "%20");

        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = fragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        Toast.makeText(GooglePlacesActivity.this, storeName, Toast.LENGTH_LONG).show();
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + currentLatitude + "," + currentLongitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&name=" + storeName);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                placeLatitude = marker.getPosition().latitude;

                placeLongitude = marker.getPosition().longitude;

                distance = calculateDistance(currentLatitude, currentLongitude, placeLatitude,
                        placeLongitude);

                DecimalFormat df = new DecimalFormat("#.#");

                Toast.makeText(GooglePlacesActivity.this, marker.getTitle().toString() +
                        "\n" + df.format(distance) + " miles away.", Toast.LENGTH_LONG).show();

                return true;
            }
        });

        previousActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        returnMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GooglePlacesActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public double calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2){

        int radius=6371; // radius of the Earth in Km
        double lat1 = latitude1;
        double lat2 = latitude2;
        double lon1 = longitude1;
        double lon2 = longitude2;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distInKms = radius * c;  // Distance in Kilometers
        double distInMiles = distInKms * 0.62137119;  // Distance converted to Miles

        return distInMiles;
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

}
package com.example.android.expensetracker.ui;

// REFERENCES: Tutorial found at:
// http://javapapers.com/android/find-places-nearby-in-google-maps-using-google-places-apiandroid-app/

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.expensetracker.BuildConfig;
import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.GooglePlacesReadTask;
import com.example.android.expensetracker.model.PlaceDbHelper;
import com.example.android.expensetracker.model.PlaceItem;
import com.example.android.expensetracker.model.PlaceList;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GooglePlacesActivity extends FragmentActivity implements LocationListener {

    // Data structures

    private PlaceItem mPlaceItem;
    private int mRowNumber;
    private PlaceList mPlaceList = new PlaceList();

    PlaceDbHelper placeDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    GoogleMap googleMap;
    double currentLatitude = 0;
    double currentLongitude = 0;
    double destLatitude = 0;
    double destLongitude = 0;
    private int PROXIMITY_RADIUS = 5000;
    String storeType;
    String storeNameAddress;
    double distanceKms = 0;
    double distanceMiles = 0;

    String response;

    private Button previousActivityButton, returnMainActivityBtn, displayPlacesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_places);

        mRowNumber = 0;

        previousActivityButton = (Button) findViewById(R.id.previousActivityButton);
        returnMainActivityBtn = (Button) findViewById(R.id.returnMainActivityBtn);
        displayPlacesButton = (Button) findViewById(R.id.displayPlacesButton);

        //show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        Intent intent = getIntent();
        storeType = intent.getStringExtra(getString(R.string.store_name));

        storeType = storeType.replaceAll(" ", "%20");

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

        Toast.makeText(GooglePlacesActivity.this, storeType, Toast.LENGTH_LONG).show();
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + currentLatitude + "," + currentLongitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&name=" + storeType);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + BuildConfig.GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);

        // Initialize place item

        mPlaceItem = new PlaceItem();

        //Initialize PlaceDbHelper and SQLiteDB

        placeDbHelper = new PlaceDbHelper(getApplicationContext());
        sqLiteDatabase = placeDbHelper.getReadableDatabase();

        cursor = placeDbHelper.getPlaceItem(sqLiteDatabase);

        // Initialize the Row Number

        mRowNumber = 0;

        if(cursor.moveToFirst()) {

            do {

                int place_ID;
                double latitude, longitude;
                String name_address;

                place_ID = cursor.getInt(0);
                latitude = cursor.getDouble(1);
                longitude = cursor.getDouble(2);
                name_address = cursor.getString(3);

                mPlaceItem = new PlaceItem(place_ID, latitude, longitude, name_address);

                mPlaceList.addPlaceItem(mPlaceItem, mRowNumber);

                mRowNumber++;

            }

            while (cursor.moveToNext());

        }

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                storeNameAddress = marker.getTitle().toString();

                destLatitude = marker.getPosition().latitude;

                destLongitude = marker.getPosition().longitude;

                distanceMiles = getDistance(currentLatitude, currentLongitude, destLatitude,
                        destLongitude);

                Intent intent = new Intent(GooglePlacesActivity.this, StorePlaceActivity.class);

                intent.putExtra(getString(R.string.store_name), storeNameAddress);
                intent.putExtra(getString(R.string.latitude), destLatitude);
                intent.putExtra(getString(R.string.longitude), destLongitude);
                intent.putExtra(getString(R.string.distance), distanceMiles);

                startActivity(intent);

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

        displayPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GooglePlacesActivity.this, DisplayPlaceActivity.class);

                // Next, I will pass in the array of place items, mPlaceList, a Placelist object
                // to DisplayPlaceActivity.java


                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.PLACE_LIST), mPlaceList.mPlaceItem);

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

    public double getDistance(final double lat1, final double lon1, final double lat2, final double lon2){

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");

                    distanceKms = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]","") );

                    distanceMiles = distanceKms * 0.62137119;  // Distance converted to Miles

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return distanceMiles;
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
package com.example.android.expensetracker.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.PlaceDbHelper;
import com.example.android.expensetracker.model.PlaceItem;
import com.example.android.expensetracker.model.PlaceList;

public class LocatorActivity extends ActionBarActivity {

    // Data structures

    private PlaceItem mPlaceItem;
    private int mRowNumber;
    private PlaceList mPlaceList = new PlaceList();

    PlaceDbHelper placeDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    // String array to hold the store names to pick from
    private String[] storeType = {"Pick a store type ...", "Pharmacy", "Supermarket", "Gas", "Clothing",
            "Restaurant", "Auto", "Laundry", "Hardware", "Electronics"};

    // Member variables

    private Spinner store_type_spinner;
    private String mStoreType;
    private Button mapButton, returnMainButton, displayPlacesButton;
    private EditText storeEditText;
    private boolean typedInStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        mRowNumber = 0;

        typedInStore = false;

        store_type_spinner = (Spinner) findViewById(R.id.storeTypeSpinner);
        storeEditText = (EditText) findViewById(R.id.storeEditText);
        mapButton = (Button) findViewById(R.id.mapButton);
        returnMainButton = (Button) findViewById(R.id.returnMainButton);
        displayPlacesButton = (Button) findViewById(R.id.displayPlacesButton);

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

                // These corresponds to the columns in the videoDbHelper: video_ID (column 0),
                // rank (col. 1), title (col. 2), author (col. 3), and year (col. 4)

                // See below:

                /*
                private static final String CREATE_QUERY = "CREATE TABLE " + VideoListDB.NewVideoItem.TABLE_NAME +
                "(" + VideoListDB.NewVideoItem.VIDEO_ID + " TEXT," +
                VideoListDB.NewVideoItem.RANK + " INTEGER," +
                VideoListDB.NewVideoItem.TITLE + " TEXT," +
                VideoListDB.NewVideoItem.AUTHOR + " TEXT," +
                VideoListDB.NewVideoItem.YEAR + " INTEGER);"; */

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

        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, storeType);

        store_type_spinner.setAdapter(stringArrayAdapter);

        // Code to extract the store name from the store_spinner and then store it into
        // the variable mStoreType, goes here:

        store_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                typedInStore = false;
                storeEditText.setText("");

                mStoreType = store_type_spinner.getSelectedItem().toString();

                Toast.makeText(LocatorActivity.this, mStoreType, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        storeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                typedInStore = true;

                // If the user had already picked a store type on the spinner,
                // reset the spinner back to the first possibility = none.

                store_type_spinner.setSelection(0);

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(typedInStore){

                    mStoreType = storeEditText.getText().toString();

                }

                // If the user has not typed in a store name, AND he/she has not picked
                // a store type from the spinner, don't map, alert about making a
                // selection

                if((!typedInStore)&&(store_type_spinner.getSelectedItemId()==0)){

                    Toast.makeText(LocatorActivity.this, "Pick a store type!", Toast.LENGTH_LONG).show();
                }

                else {

                    Intent intent = new Intent(LocatorActivity.this, GooglePlacesActivity.class);
                    intent.putExtra(getString(R.string.store_name), mStoreType);
                    startActivity(intent);
                }

            }
        });

        returnMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        displayPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LocatorActivity.this, DisplayPlaceActivity.class);

                // Next, I will pass in the array of place items, mPlaceList, a Placelist object
                // to DisplayPlaceActivity.java

                // Get the last value of mRowNumber stored in SharedPreferences file

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.PLACE_LIST), mPlaceList.mPlaceItem);

                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_locator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.android.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.PlaceDbHelper;
import com.example.android.expensetracker.model.PlaceItem;
import com.example.android.expensetracker.model.PlaceList;

import java.text.DecimalFormat;

public class StorePlaceActivity extends ActionBarActivity {

    // Data structures

    private PlaceItem mPlaceItem;
    private int mRowNumber;
    private PlaceList mPlaceList = new PlaceList();

    Context context;
    PlaceDbHelper placeDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    // Member variables

    private Toolbar toolbar;

    private int mPlaceID;
    private String mStoreNameAddress;
    private double mLatitude;
    private double mLongitude;
    private double mDistance;

    private TextView nameAddressTextView;
    private TextView distanceTextView;
    private Button storePlaceButton, displayPlacesButton, previousActivityButton, returnMainActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_place);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.ColorPrimaryDark));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRowNumber = 0;

        nameAddressTextView = (TextView) findViewById(R.id.nameAddressTextView);
        distanceTextView = (TextView) findViewById(R.id.distanceTextView);
        storePlaceButton = (Button) findViewById(R.id.storePlaceButton);
        displayPlacesButton = (Button) findViewById(R.id.displayPlacesButton);
        previousActivityButton = (Button) findViewById(R.id.previousActivityButton);
        returnMainActivityBtn = (Button) findViewById(R.id.returnMainActivityBtn);

        Intent intent = getIntent();

        mStoreNameAddress = intent.getStringExtra(getString(R.string.store_name));
        mLatitude = intent.getDoubleExtra(getString(R.string.latitude),0.0);
        mLongitude = intent.getDoubleExtra(getString(R.string.longitude),0.0);
        mDistance = intent.getDoubleExtra(getString(R.string.distance),0.0);

        nameAddressTextView.setText(mStoreNameAddress);

        DecimalFormat df = new DecimalFormat("#.#");

        distanceTextView.setText(df.format(mDistance));


        // Initialize place item

        mPlaceItem = new PlaceItem();

        //Initialize PlaceDbHelper and SQLiteDB

        placeDbHelper = new PlaceDbHelper(getApplicationContext());
        sqLiteDatabase = placeDbHelper.getReadableDatabase();

        cursor = placeDbHelper.getPlaceItem(sqLiteDatabase);

        // Initialize the Row Number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

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

            while(cursor.moveToNext());

        }


        storePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addPlace(v);

                // Play a beeping sound once the expense is successfully stored.

                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);


            }
        });

        displayPlacesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StorePlaceActivity.this, DisplayPlaceActivity.class);

                // Next, I will pass in the array of place items, mPlaceList, a Placelist object
                // to DisplayPlaceActivity.java

                // Get the last value of mRowNumber stored in SharedPreferences file

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.PLACE_LIST), mPlaceList.mPlaceItem);

                startActivity(intent);

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

                Intent intent = new Intent(StorePlaceActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_place, menu);
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

    public void addPlace(View view) {

        context = this;

        // Perform DB insertion...

        // Initialize placeDbhelper object and SQLiteDatabase object.

        placeDbHelper = new PlaceDbHelper(context);
        sqLiteDatabase = placeDbHelper.getWritableDatabase();


        // This block only applies if primary key goes from 000 to XXX counter
        // Before inserting the item, retrieve the last value of mPlaceID (if we have a primary key that
        // goes from 000 to XXX), which is stored in SharedPref file. If there isn't any previous value, assign zero.

        SharedPreferences sharedPreferences = StorePlaceActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
        mPlaceID = sharedPreferences.getInt(getString(R.string.PLACE_ID),0);

        // Add one to place ID

        mPlaceID = mPlaceID + 1;

        // If primary key goes from 000 to XXX counter. Block ends here.

        // Insert the item details in the database
        placeDbHelper.addItem(mPlaceID+"", mLatitude+"", mLongitude+"", mStoreNameAddress, sqLiteDatabase);

        Toast.makeText(StorePlaceActivity.this, "Place Item # " + mPlaceID + " Saved", Toast.LENGTH_LONG).show();

        // Optional block: applies if primary key goes from 000 to XXX counter.

        // Store new mPlaceID in SharedPrefs file

        sharedPreferences = StorePlaceActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.PLACE_ID), mPlaceID);
        editor.commit();

    // Optional block ends here.

        placeDbHelper.close();

        // Go back to Main Activity

        Intent intent = new Intent(StorePlaceActivity.this, MainActivity.class);
        startActivity(intent);

    }

}

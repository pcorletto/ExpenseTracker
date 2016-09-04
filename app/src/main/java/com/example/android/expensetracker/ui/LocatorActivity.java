package com.example.android.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.expensetracker.R;

public class LocatorActivity extends ActionBarActivity {

    // String array to hold the store names to pick from
    private String[] storeType = {"Pharmacy", "Supermarket", "Gas Station", "Clothing Store",
            "Restaurant", "Auto Parts"};

    // Member variables

    private Spinner store_type_spinner;
    private String mStoreType;
    private Button mapButton, returnMainButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        store_type_spinner = (Spinner) findViewById(R.id.storeTypeSpinner);

        mapButton = (Button) findViewById(R.id.mapButton);
        returnMainButton = (Button) findViewById(R.id.returnMainButton);

        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, storeType);



        store_type_spinner.setAdapter(stringArrayAdapter);

        // Code to extract the store name from the store_spinner and then store it into
        // the variable mStoreType, goes here:

        store_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mStoreType = store_type_spinner.getSelectedItem().toString();

                Toast.makeText(LocatorActivity.this, mStoreType, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LocatorActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });

        returnMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

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

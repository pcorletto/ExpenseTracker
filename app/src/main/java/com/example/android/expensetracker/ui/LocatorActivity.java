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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.expensetracker.R;

public class LocatorActivity extends ActionBarActivity {

    // String array to hold the store names to pick from
    private String[] storeType = {"Pick a store type ...", "Pharmacy", "Supermarket", "Gas", "Clothing",
            "Restaurant", "Auto", "Laundry"};

    // Member variables

    private Spinner store_type_spinner;
    private String mStoreType;
    private Button mapButton, returnMainButton;
    private EditText storeEditText;
    private boolean typedInStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        typedInStore = false;

        store_type_spinner = (Spinner) findViewById(R.id.storeTypeSpinner);
        storeEditText = (EditText) findViewById(R.id.storeEditText);
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

                if((!typedInStore)&&(store_type_spinner.getSelectedItemPosition()==0)){

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

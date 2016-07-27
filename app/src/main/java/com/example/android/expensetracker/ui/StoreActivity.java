package com.example.android.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.ExpenseDbHelper;
import com.example.android.expensetracker.model.ExpenseItem;
import com.example.android.expensetracker.model.ExpenseList;

import java.util.Calendar;

public class StoreActivity extends ActionBarActivity {

    // Views to be bound

    private EditText amountEditText, descriptionEditText;
    private Button storeButton, clearButton, displayButton;
    public static final String TAG = StoreActivity.class.getSimpleName();

    // Data structures

    private ExpenseItem mExpenseItem;
    private int mRowNumber;
    private ExpenseList mExpenseList = new ExpenseList();

    Context context;
    ExpenseDbHelper expenseDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    // String array to hold the store names to pick from
    private String[] storeSelection = {"Walmart","Sedanos", "Publix", "Tropical", "Aldi",
            "Winn Dixie", "BJ Wholesale Club", "Dollar Tree", "Dee Dee Discount",
            "CVS", "Walgreens", "Rite Aid", "McDonalds", "Burger King", "Wendys", "Subways",
            "Pollo Tropical", "City of Homestead", "Metro PCS", "Molina Health Care", "Wells Fargo",
            "Suntrust", "MoneyGram", "Church", "Badcock Furniture", "Brandsmart",
            "Current Landlord", "Office Max", "RaceTrac", "Shell", "Sun Pass", "Mechanic",
            "GEICO Insurance", "Vehicle Registration", "ASEA", "Gano Excel", "Shipping Company",
            "Jet Blue", "Spirit Airlines", "Mechanic", "AutoZone", "Amazon", "Public Bus or Train Fare",
            "Children School Expense", "Teacher Expense", "Beauty Salon", "Barber", "Flat/Tire Repair"};

    private Spinner store_spinner;

    // Member variables

    private int mExpenseID;
    private double mExpenseAmount;
    private String mDate, mCategory, mStore, mDescription;
    private RadioGroup group1, group2;

    // The following two variables, listener1 and listener 2, will be used in onCreate to
    // allow only one RadioGroup to be selected at one time.

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if(checkedId != -1){

                group2.setOnCheckedChangeListener(null); // remove the listener
                // before clearing so we don't throw the StackOverflow exception

                group2.clearCheck(); // clear the second RadioGroup!

                group2.setOnCheckedChangeListener(listener2); //reset the listener

            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if(checkedId != -1){

                group1.setOnCheckedChangeListener(null); // remove the listener
                // before clearing so we don't throw the StackOverflow exception

                group1.clearCheck(); // clear the second RadioGroup!

                group1.setOnCheckedChangeListener(listener1); //reset the listener


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, storeSelection);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        storeButton = (Button) findViewById(R.id.storeButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        displayButton = (Button) findViewById(R.id.displayButton);
        store_spinner = (Spinner) findViewById(R.id.store_spinner);
        group1 = (RadioGroup) findViewById(R.id.group1);
        group2 = (RadioGroup) findViewById(R.id.group2);

        group1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        group2.clearCheck();

        // We cannot click the radio buttons from both groups, from both row 1 and row 2 of the
        // TableLayout. If some radio buttons were already selected from one row, deselect
        // them so that we can select new ones, on the new row.

        // The following code prevents an infinite loop that could throw a
        // StackOverFlow exception


        group1.setOnCheckedChangeListener(listener1);
        group2.setOnCheckedChangeListener(listener2);

        store_spinner.setAdapter(stringArrayAdapter);

        // Code to extract the store name from the store_spinner and then store it into
        // the variable mStore, goes here:

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mStore = store_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addItem(v);
            }
        });

        // Initialize expense item

        mExpenseItem = new ExpenseItem();

        //Initialize ExpenseDbHelper and SQLiteDB

        expenseDbHelper = new ExpenseDbHelper(getApplicationContext());
        sqLiteDatabase = expenseDbHelper.getReadableDatabase();

        cursor = expenseDbHelper.getExpenseItem(sqLiteDatabase);

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                int expense_ID;
                String date, category, mstore, description;
                double expense_amount;

                // These corresponds to the columns in the videoDbHelper: expense_ID (column 0),
                // date (col. 1), expense_amount (col. 2), category (col. 3), store (col. 4),
                // and description (col. 5)

                // See sample below:

                /*
                private static final String CREATE_QUERY = "CREATE TABLE " + VideoListDB.NewVideoItem.TABLE_NAME +
                "(" + VideoListDB.NewVideoItem.VIDEO_ID + " TEXT," +
                VideoListDB.NewVideoItem.RANK + " INTEGER," +
                VideoListDB.NewVideoItem.TITLE + " TEXT," +
                VideoListDB.NewVideoItem.AUTHOR + " TEXT," +
                VideoListDB.NewVideoItem.YEAR + " INTEGER);"; */

                expense_ID = cursor.getInt(0);
                date = cursor.getString(1);
                expense_amount = cursor.getDouble(2);
                category = cursor.getString(3);
                mstore = cursor.getString(4);
                description = cursor.getString(5);

                mExpenseItem = new ExpenseItem(expense_ID, date, expense_amount, category, mstore, description);

                mExpenseList.addExpenseItem(mExpenseItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }

        // The following block of code clears the EditText when tapped, if there was already
        // some user input on it.

        amountEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                amountEditText.setTextColor(Color.BLACK);
                amountEditText.requestFocus();
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.showSoftInput(amountEditText, 0);
                }
                amountEditText.setText("");
                return true;
            }
        });

        // The following block of code clears the EditText when tapped, if there was already
        // some user input on it.

        descriptionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                descriptionEditText.setTextColor(Color.BLACK);
                descriptionEditText.requestFocus();
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.showSoftInput(descriptionEditText, 0);
                }
                descriptionEditText.setText("");
                return true;
            }
        });

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreActivity.this, DisplayByDateActivity.class);

                // When I fix up Display By Groceries, DisplayByGas, Display By Medicine, etc..

                // Then I will pass the intent from here to DisplayOptionsActivity.class. For now

                // just by-pass it by commenting it out.

                //Intent intent = new Intent(StoreActivity.this, DisplayOptionsActivity.class);

                // Next, I will pass in the array of expense items, mExpenseList, an ExpenseList object
                // to DisplayOptionsActivity.java

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.EXPENSE_LIST), mExpenseList.mExpenseItem);

                startActivity(intent);

            }
        });

    }

    public void addItem(View view) {

        context = this;

        // Perform DB insertion...

        // Initialize expenseDbhelper object and SQLiteDatabase object.

        expenseDbHelper = new ExpenseDbHelper(context);
        sqLiteDatabase = expenseDbHelper.getWritableDatabase();

        mDate = getCurrentDate(); // Get from system's date
        mExpenseAmount = Double.parseDouble(amountEditText.getText().toString());

        //mCategory = ""; // get category from Radio button

        mDescription = descriptionEditText.getText().toString();

        // Before inserting the item, retrieve the last value of mExpenseID, which is stored
        // in SharedPref file. If there isn't any previous value, assign zero.

        SharedPreferences sharedPreferences = StoreActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
        mExpenseID = sharedPreferences.getInt(getString(R.string.EXPENSE_ID),0);

        // Add one to expense ID

        mExpenseID = mExpenseID + 1;

        // Insert the item details in the database
        expenseDbHelper.addItem(mExpenseID, mDate, mExpenseAmount, mCategory, mStore,
                mDescription, sqLiteDatabase);

        Toast.makeText(StoreActivity.this, "Expense Item # "+ mExpenseID + " Saved.", Toast.LENGTH_LONG).show();

        // Store new mExpenseID in SharedPrefs file

        sharedPreferences = StoreActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.EXPENSE_ID), mExpenseID);
        editor.commit();

        expenseDbHelper.close();

    }

    public String getCurrentDate(){

        Calendar ci = Calendar.getInstance();

        // Add one to the number of the month, because in Java, January is represented
        // using zero.

        String formattedMonth = String.format("%02d", ci.get(Calendar.MONTH)+1 );
        String formattedDay = String.format("%02d", ci.get(Calendar.DAY_OF_MONTH));
        String formattedYear = String.format("%02d", ci.get(Calendar.YEAR));

        String ciMonthDayYear = formattedMonth + "/" + formattedDay

                + "/" + formattedYear.substring(2,4);

        return ciMonthDayYear;

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.groceryRadioButton:
                if (checked)
                    mCategory = getString(R.string.grocery_radio_btn);
                break;

            case R.id.dineoutRadioButton:
                if (checked)
                    mCategory = getString(R.string.dineout_radio_btn);
                break;

            case R.id.gasRadioButton:
                if (checked)
                    mCategory = getString(R.string.gas_radio_btn);
                break;

            case R.id.medicineRadioButton:
                if (checked)
                    mCategory = getString(R.string.medicine_radio_btn);
                break;

            case R.id.cosmeticsRadioButton:
                if (checked)
                    mCategory = getString(R.string.cosmetics_radio_btn);
                break;

            case R.id.donationsRadioButton:
                if (checked)
                    mCategory = getString(R.string.donations_radio_btn);
                break;

            case R.id.miscRadioButton:
                if (checked)
                    mCategory = getString(R.string.misc_radio_btn);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
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

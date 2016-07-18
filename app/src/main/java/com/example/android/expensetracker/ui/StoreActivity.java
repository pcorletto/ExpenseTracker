package com.example.android.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

    // Member variables

    private int mExpenseID;
    private double mExpenseAmount;
    private String mDate, mCategory, mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        storeButton = (Button) findViewById(R.id.storeButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        displayButton = (Button) findViewById(R.id.displayButton);

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

        // Make the row number equal to the last number stored on the Shared Preferences file

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0) ;

        if(cursor.moveToFirst()){

            do{

                int expense_ID;
                String date, category, description;
                double expense_amount;

                // These corresponds to the columns in the videoDbHelper: expense_ID (column 0),
                // date (col. 1), expense_amount (col. 2), category (col. 3), and description (col. 4)

                // See sample below:

                /*
                private static final String CREATE_QUERY = "CREATE TABLE " + VideoListDB.NewVideoItem.TABLE_NAME +
                "(" + VideoListDB.NewVideoItem.VIDEO_ID + " TEXT," +
                VideoListDB.NewVideoItem.RANK + " INTEGER," +
                VideoListDB.NewVideoItem.TITLE + " TEXT," +
                VideoListDB.NewVideoItem.AUTHOR + " TEXT," +
                VideoListDB.NewVideoItem.YEAR + " INTEGER);"; */

                expense_ID = mRowNumber;
                //expense_ID = cursor.getInt(0);
                date = cursor.getString(1);
                expense_amount = cursor.getDouble(2);
                category = cursor.getString(3);
                description = cursor.getString(4);

                mExpenseItem = new ExpenseItem(expense_ID, date, expense_amount, category, description);

                mExpenseList.addExpenseItem(mExpenseItem, mRowNumber);

                mRowNumber++;

                // Store the new row number on the Shared Preferences file.

                SharedPreferences sharedPreferences = StoreActivity.this
                        .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(getString(R.string.ROW_NUMBER), mRowNumber);
                editor.commit();

            }

            while(cursor.moveToNext());

        }

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreActivity.this, DisplayOptionsActivity.class);

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

        mExpenseID = mRowNumber;
        mDate = getCurrentDate(); // Get from system's date
        mExpenseAmount = Double.parseDouble(amountEditText.getText().toString());

        //mCategory = ""; // get category from Radio button

        mDescription = descriptionEditText.getText().toString();

        // Insert the item details in the database
        expenseDbHelper.addItem(mExpenseID, mDate, mExpenseAmount, mCategory, mDescription, sqLiteDatabase);

        Toast.makeText(StoreActivity.this, "Video Item Saved", Toast.LENGTH_LONG).show();

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

                + "/" + formattedYear;

        return ciMonthDayYear;

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.groceryRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.grocery_radio_btn);
                break;

            case R.id.dineoutRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.dineout_radio_btn);
                break;

            case R.id.gasRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.gas_radio_btn);
                break;

            case R.id.medicineRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.medicine_radio_btn);
                break;

            case R.id.cosmeticsRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.cosmetics_radio_btn);
                break;

            case R.id.donationsRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.donations_radio_btn);
                break;

            case R.id.miscRadioButton:
                if (checked)
                    mCategory = String.valueOf(R.string.misc_radio_btn);
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

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

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.ExpenseDbHelper;
import com.example.android.expensetracker.model.ExpenseItem;
import com.example.android.expensetracker.model.ExpenseList;


public class MainActivity extends ActionBarActivity {

    // Data structures

    private ExpenseItem mExpenseItem;
    private int mRowNumber=0;
    private ExpenseList mExpenseList = new ExpenseList();

    Context context;
    ExpenseDbHelper expenseDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    private Button storeExpenseButton, displayExpenseOptionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storeExpenseButton = (Button) findViewById(R.id.displayByDateButton);
        displayExpenseOptionsButton = (Button) findViewById(R.id.displayExpenseOptionsButton);

        // Retrieve any previous row number stored on the SharedPreferences file

        SharedPreferences sharedPreferences = MainActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
        mRowNumber = sharedPreferences.getInt(getString(R.string.ROW_NUMBER),0);

        // When the storeExpenseButton is clicked, the StoreActivity is invoked.

        storeExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, StoreActivity.class);
                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);
                startActivity(intent);

            }
        });

        // Initialize expense item

        mExpenseItem = new ExpenseItem();

        //Initialize ExpenseDbHelper and SQLiteDB

        expenseDbHelper = new ExpenseDbHelper(getApplicationContext());
        sqLiteDatabase = expenseDbHelper.getReadableDatabase();

        cursor = expenseDbHelper.getExpenseItem(sqLiteDatabase);

        // Initialize the Row Number

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                int expense_id;
                String date, category, mstore, description, receipt_pic_string;
                double expense_amount;

                // These corresponds to the columns in the videoDbHelper: expense_ID (column 0),
                // date (col. 1), expense_amount (col. 2), category (col. 3), store (col. 4),
                // and description (col. 5)

                // See below:

                /*
                private static final String CREATE_QUERY = "CREATE TABLE " + ExpenseListDB.NewExpenseItem.TABLE_NAME +
            "(" + ExpenseListDB.NewExpenseItem.EXPENSE_ID + " INTEGER," +
            ExpenseListDB.NewExpenseItem.DATE + " TEXT," +
            ExpenseListDB.NewExpenseItem.EXPENSE_AMOUNT + " REAL," +
            ExpenseListDB.NewExpenseItem.CATEGORY + " TEXT," +
            ExpenseListDB.NewExpenseItem.STORE + " TEXT," +
            ExpenseListDB.NewExpenseItem.DESCRIPTION + " TEXT);";*/

                expense_id = cursor.getInt(0);
                date = cursor.getString(1);
                expense_amount = cursor.getDouble(2);
                category = cursor.getString(3);
                mstore = cursor.getString(4);
                description = cursor.getString(5);
                receipt_pic_string = cursor.getString(6);

                mExpenseItem = new ExpenseItem(expense_id, date, expense_amount, category, mstore,
                        description, receipt_pic_string);

                mExpenseList.addExpenseItem(mExpenseItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }

        displayExpenseOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DisplayByDateActivity.class);

                // When I fix up Display By Groceries, DisplayByGas, Display By Medicine, etc..

                // Then I will pass the intent from here to DisplayOptionsActivity.class. For now

                // just by-pass it by commenting it out.

                //Intent intent = new Intent(MainActivity.this, DisplayOptionsActivity.class);

                // Next, I will pass in the array of expense items, mExpenseList, an ExpenseList object
                // to DisplayActivity.java

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.EXPENSE_LIST), mExpenseList.mExpenseItem);

                startActivity(intent);

            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

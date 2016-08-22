package com.example.android.expensetracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.ExpenseDbHelper;
import com.example.android.expensetracker.model.ExpenseItem;
import com.example.android.expensetracker.model.ExpenseItemAdapter;
import com.example.android.expensetracker.model.ExpenseList;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayByDateActivity extends AppCompatActivity {

    private ListView listview;
    private ExpenseItem[] mExpenseItems;
    private List<ExpenseItem> list = new ArrayList<>();

    // Need a newList, for when expenses are deleted, a new list is created
    private List<ExpenseItem> newList = new ArrayList<>();

    View footerView;

    private ExpenseItemAdapter mAdapter;
    ExpenseDbHelper expenseDbHelper;
    SQLiteDatabase sqLiteDatabase;
    private int mRowNumber;

    private double totalExpense;

    private ExpenseItem mExpenseItem;
    private ExpenseList mExpenseList = new ExpenseList();
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_by_date);

        totalExpense = 0;

        listview = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.EXPENSE_LIST));

        mExpenseItems = Arrays.copyOf(parcelables, mRowNumber, ExpenseItem[].class);

        for(int i=0; i<mRowNumber; i++){

            list.add(mExpenseItems[i]);

            totalExpense = mExpenseItems[i].getExpenseAmount() + totalExpense;

        }

        mAdapter = new ExpenseItemAdapter(DisplayByDateActivity.this, list);

        mAdapter.notifyDataSetChanged();

        listview.setAdapter(mAdapter);

        footerView = View.inflate(getBaseContext(), R.layout.footer_layout, null);

        listview.addFooterView(footerView);

        ViewHolder holder = new ViewHolder();

        holder.totalExpenseEditText = (TextView) footerView.findViewById(R.id.totalExpenseEditText);
        holder.deleteSelectedItemsBtn = (Button) footerView.findViewById(R.id.deleteSelectedItemsBtn);
        holder.returnToMainBtn = (Button) footerView.findViewById(R.id.returnToMainBtn);

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.totalExpenseEditText.setText(df.format(totalExpense));

        holder.deleteSelectedItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).isSelected()) {

                        // Update the last mExpenseID to one less than the previous one,
                        // because we are deleting an item

                        // Retrieve last expense_ID from Shared Prefs file

                        SharedPreferences sharedPreferences = DisplayByDateActivity.this
                                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
                        int mExpenseID = sharedPreferences.getInt(getString(R.string.EXPENSE_ID),0);

                        // Subtract one from expense ID

                        mExpenseID = mExpenseID - 1;

                        // Since the item is already marked for deletion, store the new value of
                        // expense ID in SharedPrefs file

                        sharedPreferences = DisplayByDateActivity.this
                                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(getString(R.string.EXPENSE_ID), mExpenseID);
                        editor.commit();

                        // For ListView: Skip checked or selected items. These will be deleted and will not
                        // be added to the new listview.

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = list.get(i).getExpenseID() + "";

                        // Initialize the expenseDbHelper object

                        expenseDbHelper = new ExpenseDbHelper(getApplicationContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = expenseDbHelper.getReadableDatabase();

                        // Delete the expense item from the SQLite database

                        expenseDbHelper.deleteExpenseItem(item_for_DB_deletion, sqLiteDatabase);


                    } else {

                        // Add the item to the listview, because it won't be deleted.

                        newList.add(list.get(i));

                    }


                }

                mAdapter.notifyDataSetChanged();

                mAdapter = new ExpenseItemAdapter(DisplayByDateActivity.this, newList);

                listview.setAdapter(mAdapter);

                // Add Footer

                listview.addFooterView(footerView);

            }

        });


        holder.returnToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }

        });

    }

    public final class ViewHolder{

        // This ViewHolder is for the footer, to hold the Delete selected expenses button and
        // return to main button

        public TextView totalExpenseEditText;

        public Button deleteSelectedItemsBtn, returnToMainBtn;

    }



    @Override
    public void onResume(){

        super.onResume();
        list.clear();

        // Reload the items from the database

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
                String date, category, mstore, description, receipt_pic_string;
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
                receipt_pic_string = cursor.getString(6);

                mExpenseItem = new ExpenseItem(expense_ID, date, expense_amount, category,
                        mstore, description, receipt_pic_string);

                list.add(mExpenseItem);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }

        // Done reloading items from the database

        mAdapter.refresh(list);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_by_date, menu);
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

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
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.PlaceDbHelper;
import com.example.android.expensetracker.model.PlaceItem;
import com.example.android.expensetracker.model.PlaceItemAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisplayPlaceActivity extends AppCompatActivity {

    private ListView listview;
    private PlaceItem[] mPlaceItems;
    private List<PlaceItem> list = new ArrayList<>();

    // Need a newList, for when places are deleted, a new list is created
    private List<PlaceItem> newList = new ArrayList<>();

    View footerView;

    private PlaceItemAdapter mAdapter;
    PlaceDbHelper placeDbHelper;
    SQLiteDatabase sqLiteDatabase;
    private int mRowNumber;

    private PlaceItem mPlaceItem;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_place);

        listview = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.PLACE_LIST));

        mPlaceItems = Arrays.copyOf(parcelables, mRowNumber, PlaceItem[].class);

        for(int i=0; i<mRowNumber; i++){

            list.add(mPlaceItems[i]);

        }

        mAdapter = new PlaceItemAdapter(DisplayPlaceActivity.this, list);

        mAdapter.notifyDataSetChanged();

        listview.setAdapter(mAdapter);

        footerView = View.inflate(getBaseContext(), R.layout.place_footer_layout, null);

        listview.addFooterView(footerView);

        ViewHolder holder = new ViewHolder();

        holder.deletePlaceButton = (Button) footerView.findViewById(R.id.deletePlaceButton);
        holder.previousActivityBtn = (Button) footerView.findViewById(R.id.previousActivityBtn);
        holder.returnMainActivityBtn = (Button) footerView.findViewById(R.id.returnMainActivityBtn);

        holder.deletePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).isSelected()) {

// Optional block: If primary key: 000 to XXX counter:
// Update the last mPlaceID to one less than the previous one,
                        // because we are deleting an item

                        // Retrieve last place_ID from Shared Prefs file

                        SharedPreferences sharedPreferences = DisplayPlaceActivity.this
                                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
                        int mPlaceID = sharedPreferences.getInt(getString(R.string.PLACE_ID),0);

                        // Subtract one from place ID

                        mPlaceID = mPlaceID - 1;

                        // Since the item is already marked for deletion, store the new value of
                        // place ID in SharedPrefs file

                        sharedPreferences = DisplayPlaceActivity.this
                                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(getString(R.string.PLACE_ID), mPlaceID);
                        editor.commit();

// Optional block ends here.

                        // For ListView: Skip checked or selected items. These will be deleted and will not
                        // be added to the new listview.

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = list.get(i).getPlaceID()+"";

                        // Initialize the placeDbHelper object

                        placeDbHelper = new PlaceDbHelper(getApplicationContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = placeDbHelper.getReadableDatabase();

// Before deleting the item, discount any numerical variables that go in the footer.
// For example: total Expense, total Price, etc.
                        //totalExpense = totalExpense - list.get(i).getExpenseAmount();

// Delete the item from the SQLite Database

                        placeDbHelper.deletePlaceItem(item_for_DB_deletion, sqLiteDatabase);

// Refresh the text views for the numerical items that go in the footer, discounted when the item was
// deleted.  Examples, total expense, total price, … :
                        //holder.totalExpenseEditText.setText(df.format(totalExpense));

                        Toast.makeText(getApplicationContext(), "Place item deleted", Toast.LENGTH_LONG).show();


                    } else {

                        // Add the item to the listview, because it won't be deleted.

                        newList.add(list.get(i));

                    }

                }

                mAdapter.notifyDataSetChanged();

                mAdapter = new PlaceItemAdapter(DisplayPlaceActivity.this, newList);

                listview.setAdapter(mAdapter);

                // Add Footer

                listview.addFooterView(footerView);

            }

        });

        holder.previousActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }

        });


        holder.returnMainActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayPlaceActivity.this, MainActivity.class);

                startActivity(intent);

            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_place, menu);
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

    public final class ViewHolder{

        // This ViewHolder is for the footer, to hold the Delete selected places button and
        // return to previous and main activity buttons

        public Button deletePlaceButton, previousActivityBtn, returnMainActivityBtn;

    }

    @Override
    public void onResume(){

        super.onResume();
        list.clear();

        // Reload the items from the database

        // Initialize place item

        mPlaceItem = new PlaceItem();

        //Initialize PlaceDbHelper and SQLiteDB

        placeDbHelper = new PlaceDbHelper(getApplicationContext());
        sqLiteDatabase = placeDbHelper.getReadableDatabase();

        cursor = placeDbHelper.getPlaceItem(sqLiteDatabase);

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                int place_ID;
                double latitude, longitude;
                String name_address;

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

                place_ID = cursor.getInt(0);
                latitude = cursor.getDouble(1);
                longitude = cursor.getDouble(2);
                name_address = cursor.getString(3);

                mPlaceItem = new PlaceItem(place_ID, latitude, longitude, name_address);

                list.add(mPlaceItem);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }

        // Done reloading items from the database

        mAdapter.refresh(list);

        // Refresh the footer: total expense, total price, total sales tax, etc. ….

        //TextView totalExpenseEditText = (TextView) footerView.findViewById(R.id.totalExpenseEditText);

        //final DecimalFormat df = new DecimalFormat("$0.00");

        //totalExpenseEditText.setText(df.format(newTotalExpense));

    }

}

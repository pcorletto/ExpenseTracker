package com.example.android.expensetracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hernandez on 9/10/2016.
 */
public class PlaceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PLACELIST.DB";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_QUERY = "CREATE TABLE " + PlaceListDB.NewPlaceItem.TABLE_NAME +
            "(" + PlaceListDB.NewPlaceItem.PLACE_ID + " INTEGER," +
            PlaceListDB.NewPlaceItem.LATITUDE + " REAL," +
            PlaceListDB.NewPlaceItem.LONGITUDE + " REAL," +
            PlaceListDB.NewPlaceItem.NAME_ADDRESS + " TEXT);";

    // Default Constructor:

    public PlaceDbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created / opened ...");

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created ...");

    }

    // Insert the item next. Method for inserting the place item.

    public void addItem(String id, String latitude, String longitude,
                        String name_address, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(PlaceListDB.NewPlaceItem.PLACE_ID, id);
        contentValues.put(PlaceListDB.NewPlaceItem.LATITUDE, latitude);
        contentValues.put(PlaceListDB.NewPlaceItem.LONGITUDE, longitude);
        contentValues.put(PlaceListDB.NewPlaceItem.NAME_ADDRESS, name_address);

        // Save all these into the database

        db.insert(PlaceListDB.NewPlaceItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public Cursor getPlaceItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"
        Cursor cursor;

        // Create projections, or the needed column names
        String[] projections = {PlaceListDB.NewPlaceItem.PLACE_ID,
                PlaceListDB.NewPlaceItem.LATITUDE,
                PlaceListDB.NewPlaceItem.LONGITUDE,
                PlaceListDB.NewPlaceItem.NAME_ADDRESS};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(PlaceListDB.NewPlaceItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }

    public void deletePlaceItem(String place_ID, SQLiteDatabase sqLiteDatabase){

        String selection = PlaceListDB.NewPlaceItem.PLACE_ID + " LIKE ?";

    // Use the primary key, or the Item ID (which is stored and retrieved from a SharedPreferences file), for
    // selecting the item to be deleted from the DB. This
    // will ensure that we will only delete the selected item(s) and not anything else.

        String[] selection_args = {place_ID};

        sqLiteDatabase.delete(PlaceListDB.NewPlaceItem.TABLE_NAME, selection, selection_args);

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

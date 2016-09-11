package com.example.android.expensetracker.model;

/**
 * Created by hernandez on 9/10/2016.
 */
public class PlaceListDB {

    // In the class PlaceListDB a user can store as many places as
    // he or she would like. However, PlaceList only holds 500
    // for the array size

    public static abstract class NewPlaceItem{

        // PLACE_ID is the primary key. Since each place does not have a unique identifier, we must use a Place
        // ID. Later, use the “ROW_NUMBER” incrementer to assign a value to this Place ID or primary key.
        // This “ROW_NUMBER” needs to be stored and retrieved from a SharedPreferences file.

        public static final String PLACE_ID = "place_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String  NAME_ADDRESS = "name_address";

        // Table name

        public static final String TABLE_NAME = "place_list";

    }

}



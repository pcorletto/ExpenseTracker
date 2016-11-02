package com.example.android.expensetracker.model;

/**
 * Created by hernandez on 9/10/2016.
 */
public class PlaceList {

    // The class PlaceList is only going to hold 500 places from the places
    // stored on the PlaceListDB, which is an
    // SQLite database of places you store. However, PlaceList is an array of PlaceItem
    // objects that only holds 500 places.

    public PlaceItem[] mPlaceItem = new PlaceItem[500];

    public void addPlaceItem(PlaceItem placeItem, int rowNumber){

        mPlaceItem[rowNumber] = placeItem;

    }

    public PlaceItem getPlaceItem(int i){
        return mPlaceItem[i];

    }

    public String getPlaceItemNameAddress(int i){
        return mPlaceItem[i].getNameAddress();

    }

    public double getPlaceItemLatitude(int i){
        return mPlaceItem[i].getLatitude();

    }

    public double getPlaceItemLongitude(int i){
        return mPlaceItem[i].getLongitude();

    }

}

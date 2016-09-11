package com.example.android.expensetracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hernandez on 9/9/2016.
 */
public class PlaceItem implements Parcelable {

    // Private member variables

    private int mPlaceID;
    private double mLatitude;
    private double mLongitude;
    private String mNameAddress;
    private boolean isSelected;

    // Constructors

    public PlaceItem(){

    }

    public PlaceItem(int placeID, double latitude, double longitude, String nameAddress){

        this.mPlaceID = placeID;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mNameAddress = nameAddress;
        this.isSelected = false;

    }

    // Accessor and mutator methods


    public int getPlaceID() {
        return mPlaceID;
    }

    public void setPlaceID(int placeID) {
        mPlaceID = placeID;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getNameAddress() {
        return mNameAddress;
    }

    public void setNameAddress(String nameAddress) {
        mNameAddress = nameAddress;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mPlaceID);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mNameAddress);

    }

    private PlaceItem(Parcel in){

        mPlaceID = in.readInt();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mNameAddress = in.readString();

    }

    public static final Creator<PlaceItem>CREATOR = new Creator<PlaceItem>() {

        @Override
        public PlaceItem createFromParcel(Parcel source) {
            return new PlaceItem(source);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

}

package com.example.android.expensetracker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hernandez on 7/15/2016.
 */
public class ExpenseItem implements Parcelable {

    // Private member variables

    private int mExpenseID;
    private String mDate;
    private double mExpenseAmount;
    private String mCategory;
    private String mDescription;
    private boolean isSelected;

    // Constructors

    public ExpenseItem(){

    }

    public ExpenseItem(int expenseID, String date, double expenseAmount, String category,
                       String description){

        this.mExpenseID = expenseID;
        this.mDate = date;
        this.mExpenseAmount = expenseAmount;
        this.mCategory = category;
        this.mDescription = description;
        this.isSelected = false;

    }

    // Accessor and mutator methods


    public int getExpenseID() {
        return mExpenseID;
    }

    public void setExpenseID(int expenseID) {
        mExpenseID = expenseID;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public double getExpenseAmount() {
        return mExpenseAmount;
    }

    public void setExpenseAmount(double expenseAmount) {
        mExpenseAmount = expenseAmount;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
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

        dest.writeInt(mExpenseID);
        dest.writeString(mDate);
        dest.writeDouble(mExpenseAmount);
        dest.writeString(mCategory);
        dest.writeString(mDescription);

    }

    private ExpenseItem(Parcel in){

        mExpenseID = in.readInt();
        mDate = in.readString();
        mExpenseAmount = in.readDouble();
        mCategory = in.readString();
        mDescription = in.readString();

    }

    public static final Creator<ExpenseItem>CREATOR = new Creator<ExpenseItem>() {
        @Override
        public ExpenseItem createFromParcel(Parcel source) {
            return new ExpenseItem(source);
        }

        @Override
        public ExpenseItem[] newArray(int size) {
            return new ExpenseItem[size];
        }

    };

}

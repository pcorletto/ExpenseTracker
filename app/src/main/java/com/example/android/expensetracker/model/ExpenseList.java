package com.example.android.expensetracker.model;

/**
 * Created by hernandez on 7/16/2016.
 */
public class ExpenseList {

    // The class ExpenseList is only going to hold up to 500 expenses
    // stored by the user on the ExpenseListDB, which is an
    // SQLite database of expenses you store. In ExpenseListDB, a user can store
    // as many expenses as he or she likes. However, ExpenseList is an array of ExpenseItem
    // objects that only holds 500 expenses for a month.

    public ExpenseItem[] mExpenseItem = new ExpenseItem[500];

    public void addExpenseItem(ExpenseItem expenseItem, int rowNumber){

        mExpenseItem[rowNumber] = expenseItem;

    }

    public String getExpenseItem(int i){

        return mExpenseItem[i].getDate() +
                " " + mExpenseItem[i].getExpenseAmount() +
                " " + mExpenseItem[i].getCategory() +
                "\n\n";
    }

}

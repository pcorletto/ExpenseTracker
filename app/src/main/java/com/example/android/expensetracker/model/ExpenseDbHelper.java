package com.example.android.expensetracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by hernandez on 7/16/2016.
 */
public class ExpenseDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EXPENSELIST.DB";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_QUERY = "CREATE TABLE " + ExpenseListDB.NewExpenseItem.TABLE_NAME +
            "(" + ExpenseListDB.NewExpenseItem.EXPENSE_ID + " INTEGER," +
            ExpenseListDB.NewExpenseItem.DATE + " TEXT," +
            ExpenseListDB.NewExpenseItem.EXPENSE_AMOUNT + " REAL," +
            ExpenseListDB.NewExpenseItem.CATEGORY + " TEXT," +
            ExpenseListDB.NewExpenseItem.PSTORE + " TEXT," +
            ExpenseListDB.NewExpenseItem.DESCRIPTION + " TEXT," +
            ExpenseListDB.NewExpenseItem.RECEIPT_PIC_STRING + " TEXT);";

    // Default Constructor:

    public ExpenseDbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created / opened ...");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created ...");

    }

    // Insert the item next. Method for inserting the expense item.

    public void addItem(int expenseID, String date, double expenseAmount, String category,
            String store, String description, String receipt_pic_string, SQLiteDatabase db){

        // Map key-values

        ContentValues contentValues = new ContentValues();
        contentValues.put(ExpenseListDB.NewExpenseItem.EXPENSE_ID, expenseID);
        contentValues.put(ExpenseListDB.NewExpenseItem.DATE, date);
        contentValues.put(ExpenseListDB.NewExpenseItem.EXPENSE_AMOUNT, expenseAmount);
        contentValues.put(ExpenseListDB.NewExpenseItem.CATEGORY, category);
        contentValues.put(ExpenseListDB.NewExpenseItem.PSTORE, store);
        contentValues.put(ExpenseListDB.NewExpenseItem.DESCRIPTION, description);
        contentValues.put(ExpenseListDB.NewExpenseItem.RECEIPT_PIC_STRING, receipt_pic_string);

        // Save all these into the database

        db.insert(ExpenseListDB.NewExpenseItem.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "One row is inserted ...");

    }

    public Cursor getExpenseItem(SQLiteDatabase db){

        // The return type of Object is "Cursor"

        Cursor cursor;

        // Create projections, or the needed column names

        String[] projections = {ExpenseListDB.NewExpenseItem.EXPENSE_ID,
                ExpenseListDB.NewExpenseItem.DATE,
                ExpenseListDB.NewExpenseItem.EXPENSE_AMOUNT,
                ExpenseListDB.NewExpenseItem.CATEGORY,
                ExpenseListDB.NewExpenseItem.PSTORE,
                ExpenseListDB.NewExpenseItem.DESCRIPTION,
                ExpenseListDB.NewExpenseItem.RECEIPT_PIC_STRING};

        // We only need the table name and projection parameters. No conditions will be specified,
        // so, we will pass in null for the last five parameters.

        cursor = db.query(ExpenseListDB.NewExpenseItem.TABLE_NAME, projections, null, null, null, null, null);

        return cursor;

    }

    public void deleteExpenseItem(String expenseID, SQLiteDatabase sqLiteDatabase){

        String selection = ExpenseListDB.NewExpenseItem.EXPENSE_ID + " LIKE ?";

        String[] selection_args = {expenseID};

        sqLiteDatabase.delete(ExpenseListDB.NewExpenseItem.TABLE_NAME, selection, selection_args);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

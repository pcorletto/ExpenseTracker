package com.example.android.expensetracker.model;

/**
 * Created by hernandez on 7/16/2016.
 */
public class ExpenseListDB {

    // In the class ExpenseListDB a user can store as many expenses as
    // he or she would like. However, ExpenseList only holds up to 500 for one month.

    public static abstract class NewExpenseItem{

        // Primary key is "EXPENSE_ID"

        public static final String EXPENSE_ID = "expense_id";
        public static final String DATE = "date";
        public static final String EXPENSE_AMOUNT = "expense_amount";
        public static final String CATEGORY = "category";
        public static final String PSTORE = "store";
        public static final String DESCRIPTION = "description";
        public static final String RECEIPT_PIC_STRING ="receipt_pic_string";

        // Table name

        public static final String TABLE_NAME = "expense_list";

    }

}

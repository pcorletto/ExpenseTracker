package com.example.android.expensetracker.ui;

import android.content.Intent;
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
import com.example.android.expensetracker.model.ExpenseDbHelper;
import com.example.android.expensetracker.model.ExpenseItem;
import com.example.android.expensetracker.model.ExpenseItemAdapter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_by_date);

        listview = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.EXPENSE_LIST));

        mExpenseItems = Arrays.copyOf(parcelables, mRowNumber, ExpenseItem[].class);

        for(int i=0; i<mRowNumber; i++){

            list.add(mExpenseItems[i]);

        }

        mAdapter = new ExpenseItemAdapter(DisplayByDateActivity.this, list);

        mAdapter.notifyDataSetChanged();

        listview.setAdapter(mAdapter);

        footerView = View.inflate(getBaseContext(), R.layout.footer_layout, null);

        listview.addFooterView(footerView);

        ViewHolder holder = new ViewHolder();

        holder.deleteSelectedItemsBtn = (Button) footerView.findViewById(R.id.deleteSelectedItemsBtn);
        holder.returnToMainBtn = (Button) footerView.findViewById(R.id.returnToMainBtn);

        holder.deleteSelectedItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).isSelected()) {

                        // For ListView: Skip checked or selected items. These will be deleted and will not
                        // be added to the new listview.

                        // For SQLiteDatabase: Delete this item here, if checked.

                        String item_for_DB_deletion = list.get(i).getExpenseID() + "";

                        // Initialize the expenseDbHelper object

                        expenseDbHelper = new ExpenseDbHelper(getApplicationContext());

                        // Initialize the SQLiteDatabase object

                        sqLiteDatabase = expenseDbHelper.getReadableDatabase();

                        expenseDbHelper.deleteExpenseItem(item_for_DB_deletion, sqLiteDatabase);

                        Toast.makeText(getApplicationContext(), "Expense item deleted", Toast.LENGTH_LONG).show();


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

                Intent intent = new Intent(DisplayByDateActivity.this, MainActivity.class);

                startActivity(intent);

            }

        });

    }

    public final class ViewHolder{

        // This ViewHolder is for the footer, to hold the Delete selected expenses button and
        // return to main button

        public Button deleteSelectedItemsBtn, returnToMainBtn;

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

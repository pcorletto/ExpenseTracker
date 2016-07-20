package com.example.android.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.android.expensetracker.R;

public class DisplayOptionsActivity extends ActionBarActivity {

    private Button displayByDateButton, displayByGroceryShoppingButton, displayByDineOutButton,
        displayByMedicineButton, displayByDonationsButton, displayByMiscButton;

    private int mRowNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_options);

        displayByDateButton = (Button) findViewById(R.id.displayByDateButton);

        Intent intent = getIntent();

        mRowNumber = intent.getIntExtra(getString(R.string.ROW_NUMBER), 0);

        final Parcelable[] parcelables = intent.getParcelableArrayExtra(getString(R.string.EXPENSE_LIST));

        displayByDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DisplayOptionsActivity.this, DisplayByDateActivity.class);

                // Next, I will pass in the array of expense items, mExpenseList, an ExpenseList object
                // to DisplayActivity.java

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.EXPENSE_LIST), parcelables);

                startActivity(intent);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_options, menu);
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

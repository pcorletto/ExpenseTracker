package com.example.android.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.expensetracker.R;

import java.io.FileInputStream;

public class DisplayReceiptActivity extends ActionBarActivity {

    private ImageView receiptImageView;
    private Button returnPreviousBtn, returnMainBtn;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_receipt);

        receiptImageView = (ImageView) findViewById(R.id.receiptImageView);
        returnPreviousBtn = (Button) findViewById(R.id.returnPreviousBtn);
        returnMainBtn = (Button) findViewById(R.id.returnMainBtn);

        // Get the receipt number passed via Intent

        Intent intent = getIntent();

        int expenseID = intent.getIntExtra(getString(R.string.EXPENSE_ID), 0);

        Toast.makeText(DisplayReceiptActivity.this, "PICTURE"+expenseID, Toast.LENGTH_LONG).show();

        bm = getImageBitmap(this, "PICTURE"+expenseID, "BMP");

        // Set the ImageView image to the receipt image pulled from internal storage

        receiptImageView.setImageBitmap(bm);

        returnPreviousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        returnMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayReceiptActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

    }

    public Bitmap getImageBitmap(Context context,String name,String extension){
        name=name+"."+extension;
        try{
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_receipt, menu);
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

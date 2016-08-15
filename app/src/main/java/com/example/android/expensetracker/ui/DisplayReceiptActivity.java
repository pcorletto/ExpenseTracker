package com.example.android.expensetracker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.expensetracker.R;

public class DisplayReceiptActivity extends ActionBarActivity {

    private ImageView receiptImageView;
    private Button returnPreviousBtn, returnMainBtn;
    private Bitmap bm;
    private String receiptPicString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_receipt);

        receiptImageView = (ImageView) findViewById(R.id.receiptImageView);
        returnPreviousBtn = (Button) findViewById(R.id.returnPreviousBtn);
        returnMainBtn = (Button) findViewById(R.id.returnMainBtn);

        // Get the receipt image string passed via Intent

        Intent intent = getIntent();

        receiptPicString = intent.getStringExtra(getString(R.string.RECEIPT_PIC_STRING));

        // Convert this string into a BitMap image

        bm = convertToBitmap(receiptPicString);

        // Set the ImageView image to this converted bitmap

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

    public Bitmap convertToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmapResult;
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

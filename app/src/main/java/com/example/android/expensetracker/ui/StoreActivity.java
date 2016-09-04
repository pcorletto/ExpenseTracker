package com.example.android.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.model.ExpenseDbHelper;
import com.example.android.expensetracker.model.ExpenseItem;
import com.example.android.expensetracker.model.ExpenseList;
import com.example.android.expensetracker.model.UserPicture;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class StoreActivity extends ActionBarActivity {

    // Views to be bound

    private EditText amountEditText, descriptionEditText;
    private Button storeButton, clearButton, displayButton, returnMainBtn;
    public static final String TAG = StoreActivity.class.getSimpleName();

    // Data structures

    private ExpenseItem mExpenseItem;
    private int mRowNumber;
    private ExpenseList mExpenseList = new ExpenseList();

    Context context;
    ExpenseDbHelper expenseDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    // String array to hold the store names to pick from
    private String[] storeSelection = {"Walmart","Sedanos", "Publix", "Tropical", "Aldi",
            "Winn Dixie", "BJ Wholesale Club", "Other Supermarket", "Dollar Tree", "Dee Dee Discount",
            "CVS", "Walgreens", "Rite Aid", "McDonalds", "Burger King", "Wendys", "Subways",
            "Pollo Tropical", "Other Restaurant", "City of Homestead", "Metro PCS", "Molina Health Care", "Wells Fargo",
            "Suntrust", "MoneyGram", "Church", "Badcock Furniture", "Brandsmart",
            "Current Landlord", "Office Max", "RaceTrac", "Shell", "Other Gas Station", "Sun Pass", "Mechanic",
            "GEICO Insurance", "Vehicle Registration", "ASEA", "Gano Excel", "Shipping Company",
            "Jet Blue", "Spirit Airlines", "Mechanic", "AutoZone", "Amazon", "Public Bus or Train Fare",
            "Children School Expense", "Teacher Expense", "Beauty Salon", "Barber", "Flat/Tire Repair"};

    private Spinner store_spinner;

    // Member variables

    private int mExpenseID;
    public double mExpenseAmount;
    private String mDate, mCategory, mStore, mDescription;
    private RadioGroup group1, group2;

    // The following code is for the Gallery Image picker

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private static final int SELECT_SINGLE_PICTURE = 101;

    private static final int SELECT_MULTIPLE_PICTURE = 201;

    public static final String IMAGE_TYPE = "image/*";

    private ImageView selectedImagePreview;

    private String receiptNumber;

    // The following two variables, listener1 and listener 2, will be used in onCreate to
    // allow only one RadioGroup to be selected at one time.

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if(checkedId != -1){

                group2.setOnCheckedChangeListener(null); // remove the listener
                // before clearing so we don't throw the StackOverflow exception

                group2.clearCheck(); // clear the second RadioGroup!

                group2.setOnCheckedChangeListener(listener2); //reset the listener

            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if(checkedId != -1){

                group1.setOnCheckedChangeListener(null); // remove the listener
                // before clearing so we don't throw the StackOverflow exception

                group1.clearCheck(); // clear the second RadioGroup!

                group1.setOnCheckedChangeListener(listener1); //reset the listener


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        ArrayAdapter<String> stringArrayAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, storeSelection);

        amountEditText = (EditText) findViewById(R.id.amountEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        storeButton = (Button) findViewById(R.id.storeButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        displayButton = (Button) findViewById(R.id.displayButton);
        returnMainBtn = (Button) findViewById(R.id.returnMainBtn);



        store_spinner = (Spinner) findViewById(R.id.store_spinner);
        group1 = (RadioGroup) findViewById(R.id.group1);
        group2 = (RadioGroup) findViewById(R.id.group2);

        group1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        group2.clearCheck();

        // We cannot click the radio buttons from both groups, from both row 1 and row 2 of the
        // TableLayout. If some radio buttons were already selected from one row, deselect
        // them so that we can select new ones, on the new row.

        // The following code prevents an infinite loop that could throw a
        // StackOverFlow exception


        group1.setOnCheckedChangeListener(listener1);
        group2.setOnCheckedChangeListener(listener2);

        store_spinner.setAdapter(stringArrayAdapter);

        // Code to extract the store name from the store_spinner and then store it into
        // the variable mStore, goes here:

        store_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mStore = store_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String expenseAmountString = amountEditText.getText().toString();

                // Check if the user did not enter anything. If no entry, then alert
                if(TextUtils.isEmpty(expenseAmountString)){
                    amountEditText.setError(getString(R.string.empty_expense_amount_alert));
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }

                String descriptionString = descriptionEditText.getText().toString();

                // Check if the user did not enter anything. If no entry, then alert
                if(TextUtils.isEmpty(descriptionString)){
                    descriptionEditText.setError(getString(R.string.empty_description_alert));
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_SUP_CONGESTION, 200);
                    return;
                }


                addItem(v);

                // Play a beeping sound once the expense is successfully stored.

                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);

            }
        });

        // Initialize expense item

        mExpenseItem = new ExpenseItem();

        //Initialize ExpenseDbHelper and SQLiteDB

        expenseDbHelper = new ExpenseDbHelper(getApplicationContext());
        sqLiteDatabase = expenseDbHelper.getReadableDatabase();

        cursor = expenseDbHelper.getExpenseItem(sqLiteDatabase);

        mRowNumber = 0;

        if(cursor.moveToFirst()){

            do{

                int expense_ID;
                String date, category, mstore, description;
                double expense_amount;

                // These corresponds to the columns in the videoDbHelper: expense_ID (column 0),
                // date (col. 1), expense_amount (col. 2), category (col. 3), store (col. 4),
                // and description (col. 5)

                // See sample below:

                /*
                private static final String CREATE_QUERY = "CREATE TABLE " + VideoListDB.NewVideoItem.TABLE_NAME +
                "(" + VideoListDB.NewVideoItem.VIDEO_ID + " TEXT," +
                VideoListDB.NewVideoItem.RANK + " INTEGER," +
                VideoListDB.NewVideoItem.TITLE + " TEXT," +
                VideoListDB.NewVideoItem.AUTHOR + " TEXT," +
                VideoListDB.NewVideoItem.YEAR + " INTEGER);"; */

                expense_ID = cursor.getInt(0);
                date = cursor.getString(1);
                expense_amount = cursor.getDouble(2);
                category = cursor.getString(3);
                mstore = cursor.getString(4);
                description = cursor.getString(5);

                mExpenseItem = new ExpenseItem(expense_ID, date, expense_amount, category,
                        mstore, description);

                mExpenseList.addExpenseItem(mExpenseItem, mRowNumber);

                mRowNumber++;

            }

            while(cursor.moveToNext());

        }


        // The following block of code clears the EditText when tapped, if there was already
        // some user input on it.

        amountEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                amountEditText.setTextColor(Color.BLACK);
                amountEditText.requestFocus();
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.showSoftInput(amountEditText, 0);
                }
                amountEditText.setText("");
                return true;
            }
        });

        // The following block of code clears the EditText when tapped, if there was already
        // some user input on it.

        descriptionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                descriptionEditText.setTextColor(Color.BLACK);
                descriptionEditText.requestFocus();
                InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.showSoftInput(descriptionEditText, 0);
                }
                descriptionEditText.setText("");
                return true;
            }
        });

        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreActivity.this, DisplayByDateActivity.class);

                // When I fix up Display By Groceries, DisplayByGas, Display By Medicine, etc..

                // Then I will pass the intent from here to DisplayOptionsActivity.class. For now

                // just by-pass it by commenting it out.

                //Intent intent = new Intent(StoreActivity.this, DisplayOptionsActivity.class);

                // Next, I will pass in the array of expense items, mExpenseList, an ExpenseList object
                // to DisplayOptionsActivity.java

                intent.putExtra(getString(R.string.ROW_NUMBER), mRowNumber);

                intent.putExtra(getString(R.string.EXPENSE_LIST), mExpenseList.mExpenseItem);

                startActivity(intent);

            }
        });

        returnMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        // The following code is for the button to get images from the Gallery

        // no need to cast to button view here since we can add a listener to any view, this
        // is the single image selection

        findViewById(R.id.getReceiptPicButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // in onCreate or any event where your want the user to
                // select a file
                Intent intent = new Intent();
                intent.setType(IMAGE_TYPE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
            }
        });

        selectedImagePreview = (ImageView)findViewById(R.id.image_preview);

        findViewById(R.id.startCameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

    }

    /* / ******************

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bp = (Bitmap) data.getExtras().get("data");
        selectedImagePreview.setImageBitmap(bp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    */

    public void addItem(View view) {

        context = this;

        // Perform DB insertion...

        // Initialize expenseDbhelper object and SQLiteDatabase object.

        expenseDbHelper = new ExpenseDbHelper(context);
        sqLiteDatabase = expenseDbHelper.getWritableDatabase();

        mDate = getCurrentDate(); // Get from system's date

        mExpenseAmount = Double.parseDouble(amountEditText.getText().toString());

        //mCategory = ""; // get category from Radio button

        mDescription = descriptionEditText.getText().toString();

        // Before inserting the item, retrieve the last value of mExpenseID, which is stored
        // in SharedPref file. If there isn't any previous value, assign zero.

        SharedPreferences sharedPreferences = StoreActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
        mExpenseID = sharedPreferences.getInt(getString(R.string.EXPENSE_ID),0);

        // Add one to expense ID

        mExpenseID = mExpenseID + 1;

        // Insert the item details in the database
        expenseDbHelper.addItem(mExpenseID, mDate, mExpenseAmount, mCategory, mStore,
                mDescription, sqLiteDatabase);

        Toast.makeText(StoreActivity.this, "Expense Item # "+ mExpenseID + " Saved.", Toast.LENGTH_LONG).show();

        // Store new mExpenseID in SharedPrefs file

        sharedPreferences = StoreActivity.this
                .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.EXPENSE_ID), mExpenseID);
        editor.commit();

        expenseDbHelper.close();

        finish();

    }

    public String getCurrentDate(){

        Calendar ci = Calendar.getInstance();

        // Add one to the number of the month, because in Java, January is represented
        // using zero.

        String formattedMonth = String.format("%02d", ci.get(Calendar.MONTH)+1 );
        String formattedDay = String.format("%02d", ci.get(Calendar.DAY_OF_MONTH));
        String formattedYear = String.format("%02d", ci.get(Calendar.YEAR));

        String ciMonthDayYear = formattedMonth + "/" + formattedDay

                + "/" + formattedYear.substring(2,4);

        return ciMonthDayYear;

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.groceryRadioButton:
                if (checked)
                    mCategory = getString(R.string.grocery_radio_btn);
                break;

            case R.id.dineoutRadioButton:
                if (checked)
                    mCategory = getString(R.string.dineout_radio_btn);
                break;

            case R.id.gasRadioButton:
                if (checked)
                    mCategory = getString(R.string.gas_radio_btn);
                break;

            case R.id.medicineRadioButton:
                if (checked)
                    mCategory = getString(R.string.medicine_radio_btn);
                break;

            case R.id.cosmeticsRadioButton:
                if (checked)
                    mCategory = getString(R.string.cosmetics_radio_btn);
                break;

            case R.id.donationsRadioButton:
                if (checked)
                    mCategory = getString(R.string.donations_radio_btn);
                break;

            case R.id.miscRadioButton:
                if (checked)
                    mCategory = getString(R.string.misc_radio_btn);
                break;

        }
    }

    // The following sections of code are for the Gallery image picker

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    selectedImagePreview.setImageBitmap(new UserPicture(selectedImageUri, getContentResolver()).getBitmap());

                    Bitmap bm = new UserPicture(selectedImageUri, getContentResolver()).getBitmap();

                    // Trying to store the picture at the position of the last mExpenseID plus one
                    // Get the number of the last mExpenseID stored from Shared Preferences File

                    SharedPreferences sharedPreferences = StoreActivity.this
                            .getSharedPreferences(getString(R.string.ET_PREF_FILE), MODE_PRIVATE);
                    mExpenseID = sharedPreferences.getInt(getString(R.string.EXPENSE_ID),0);

                    Toast.makeText(StoreActivity.this, "PICTURE" + (mExpenseID + 1), Toast.LENGTH_LONG).show();


                    saveImage(this, bm, "PICTURE" + (mExpenseID + 1), "BMP");


                } catch (IOException e) {
                    Log.e(MainActivity.class.getSimpleName(), "Failed to load image", e);
                }

                // original code
//                String selectedImagePath = getPath(selectedImageUri);
//                selectedImagePreview.setImageURI(selectedImageUri);
            }

        } else {
            // report failure
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_intent_data, Toast.LENGTH_LONG).show();
            Log.d(MainActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {

        // just some safety built in
        if( uri == null ) {
            // perform some logging or show user feedback
            Toast.makeText(getApplicationContext(), R.string.msg_failed_to_get_picture, Toast.LENGTH_LONG).show();
            Log.d(MainActivity.class.getSimpleName(), "Failed to parse image path from image URI " + uri);
            return null;
        }

        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here, thanks to the answer from @mad indicating this is needed for
        // working code based on images selected using other file managers
        return uri.getPath();

    }

    // The Gallery image picker helper code ends here.

    public void saveImage(Context context, Bitmap b,String name,String extension){
        name=name+"."+extension;
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store, menu);
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

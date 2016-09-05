package com.example.android.expensetracker.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.expensetracker.R;
import com.example.android.expensetracker.ui.DisplayReceiptActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernandez on 7/18/2016.
 */
public class ExpenseItemAdapter extends ArrayAdapter<ExpenseItem> {

    private List<ExpenseItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;

    public ExpenseItemAdapter(Context context, List<ExpenseItem> list) {
        super(context, R.layout.expense_list_item_expanded, list);
        this.mContext = context;
        this.list = list;

        positionArray = new ArrayList<Boolean>(list.size());
        for(int i = 0; i < list.size();i++){
            positionArray.add(false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ExpenseItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        ExpenseItem expenseItem = list.get(position);

        if(convertView == null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expense_list_item_expanded, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.amountEditText = (TextView) convertView.findViewById(R.id.amountEditText);
            holder.dateEditText = (TextView) convertView.findViewById(R.id.dateEditText);
            holder.expandItemButton = (ImageButton) convertView.findViewById(R.id.expandItemButton);
            holder.displayReceiptButton = (ImageButton) convertView.findViewById(R.id.displayReceiptButton);
            holder.storeEditText = (TextView) convertView.findViewById(R.id.storeEditText);

            holder.layout2 = (LinearLayout) convertView.findViewById(R.id.layout2);

            holder.categoryImageView = (ImageView) convertView.findViewById(R.id.categoryImageView);
            holder.descriptionEditText = (TextView) convertView.findViewById(R.id.descriptionEditText);

            convertView.setTag(holder);

        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        // Now, set the data:

        holder.expenseID = this.getItem(position).getExpenseID();
        holder.expenseAmount = this.getItem(position).getExpenseAmount();
        holder.date = this.getItem(position).getDate();
        holder.category = this.getItem(position).getCategory();
        holder.pstore = this.getItem(position).getStore();
        holder.description = this.getItem(position).getDescription();

        // Set the text views for the list view

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.amountEditText.setText(df.format(holder.expenseAmount));
        holder.dateEditText.setText(holder.date);
        holder.categoryImageView.setImageResource(getCategoryIconID(holder.category));
        holder.storeEditText.setText(holder.pstore);
        holder.descriptionEditText.setText(holder.description);

        holder.layout2.setVisibility(View.INVISIBLE);

        holder.checkBox.setFocusable(false);
        holder.checkBox.setChecked(positionArray.get(position));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked ){

                    positionArray.add(position, true);
                    list.get(position).setSelected(true);

                }else {

                    positionArray.add(position, false);
                    list.get(position).setSelected(false);

                }

            }
        });

        holder.expandItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When clicked, hide or un-hide the category and the description details

                if(holder.layout2.getVisibility()==View.VISIBLE){

                    holder.layout2.setVisibility(View.INVISIBLE);

                }

                else {

                    holder.layout2.setVisibility(View.VISIBLE);

                }

            }
        });

        holder.displayReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), DisplayReceiptActivity.class);
                intent.putExtra(getContext().getString(R.string.EXPENSE_ID),
                        holder.expenseID);
                getContext().startActivity(intent);

            }
        });

        return convertView;
    }

    private int getCategoryIconID(String category) {

        //Initialize categoryIconId to misc
        int categoryIconId = R.drawable.miscicon;

        if(category.equals("Grocery Shopping")){
            categoryIconId = R.drawable.groceryicon;
        }

        else if(category.equals("Dine Out")){
            categoryIconId = R.drawable.dineouticon;
        }

        else if(category.equals("Gas")){
            categoryIconId = R.drawable.gasicon;
        }

        else if(category.equals("Medicine")){
            categoryIconId = R.drawable.medicineicon;
        }

        else if(category.equals("Cosmetics/Personal Hygiene")){
            categoryIconId = R.drawable.cosmeticsicon;
        }

        else if(category.equals("Donations")){
            categoryIconId = R.drawable.donationicon;
        }

        else if(category.equals("Misc./Other")){
            categoryIconId = R.drawable.miscicon;
        }

        return categoryIconId;
    }

    public void refresh(List<ExpenseItem> list){

        this.list = list;
        notifyDataSetChanged();

    }

    private static class ViewHolder{

        int expenseID;
        double expenseAmount;
        String date;
        String pstore;
        String category;
        String description;

        CheckBox checkBox;
        TextView amountEditText;
        TextView dateEditText;
        ImageButton expandItemButton,displayReceiptButton;
        TextView storeEditText;
        ImageView categoryImageView;
        //TextView categoryEditText;
        TextView descriptionEditText;

        LinearLayout layout2;

    }

}




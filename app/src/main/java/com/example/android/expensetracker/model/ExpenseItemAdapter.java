package com.example.android.expensetracker.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.android.expensetracker.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernandez on 7/18/2016.
 */
public class ExpenseItemAdapter extends ArrayAdapter<ExpenseItem> {

    private final List<ExpenseItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;

    public ExpenseItemAdapter(Context context, List<ExpenseItem> list) {
        super(context, R.layout.expense_list_item, list);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.expense_list_item, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            holder.amountEditText = (TextView) convertView.findViewById(R.id.amountEditText);
            holder.dateEditText = (TextView) convertView.findViewById(R.id.dateEditText);
            holder.categoryEditText = (TextView) convertView.findViewById(R.id.categoryEditText);
            holder.storeEditText = (TextView) convertView.findViewById(R.id.storeEditText);
            holder.descriptionEditText = (TextView) convertView.findViewById(R.id.descriptionEditText);

            convertView.setTag(holder);

        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        // Now, set the data:

        holder.expenseAmount = this.getItem(position).getExpenseAmount();
        holder.date = this.getItem(position).getDate();
        holder.category = this.getItem(position).getCategory();
        holder.pstore = this.getItem(position).getStore();
        holder.description = this.getItem(position).getDescription();

        // Set the text views for the list view

        DecimalFormat df = new DecimalFormat("$0.00");

        holder.amountEditText.setText(df.format(holder.expenseAmount));
        holder.dateEditText.setText(holder.date);
        holder.categoryEditText.setText(holder.category);
        holder.storeEditText.setText(holder.pstore);
        holder.descriptionEditText.setText(holder.description);

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

        return convertView;
    }

    private static class ViewHolder{

        double expenseAmount;
        String date;
        String category;
        String pstore;
        String description;

        CheckBox checkBox;
        TextView amountEditText;
        TextView dateEditText;
        TextView categoryEditText;
        TextView storeEditText;
        TextView descriptionEditText;

    }

}




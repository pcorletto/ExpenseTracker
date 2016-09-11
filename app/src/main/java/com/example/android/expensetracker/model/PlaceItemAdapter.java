package com.example.android.expensetracker.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.example.android.expensetracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernandez on 9/11/2016.
 */
public class PlaceItemAdapter extends ArrayAdapter <PlaceItem> {

    private List<PlaceItem> list;

    ArrayList<Boolean> positionArray;

    private Context mContext;

    public PlaceItemAdapter(Context context, List<PlaceItem> list) {
        super(context, R.layout.place_list_item, list);
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
    public PlaceItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        PlaceItem placeItem = list.get(position);

        if(convertView == null){
            //brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.place_list_item, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.placeCheckBox);
            holder.nameAddressListLabel = (EditText) convertView.findViewById(R.id.nameAddressListLabel);
            holder.mapListButton = (Button) convertView.findViewById(R.id.mapListButton);

            convertView.setTag(holder);

        }

        else{
            // We have these views set up.
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);

        }

        // Now, set the data:

        holder.placeID = this.getItem(position).getPlaceID();
        holder.nameAddress = this.getItem(position).getNameAddress();
        holder.latitude = this.getItem(position).getLatitude();
        holder.longitude = this.getItem(position).getLongitude();

        holder.nameAddressListLabel.setText(holder.nameAddress);

        holder.mapListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When the user clicks on the button for a place item,
                // show the map associated map ID

                // Send the videoID for this video item via Intent to another
                // activity called PlayVideoActivity, which contains
                // a YouTube player

                // I adapted the following block of code from this
                // SOURCE: http://stackoverflow.com/questions/13053352/
                // android-how-to-launch-google-map-intent-in-android-app-with-certain-location

                String uriBegin = "geo:" + holder.latitude + "," + holder.longitude;
                String query = holder.latitude + "," + holder.longitude + "(" + holder.nameAddress + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);

            }

        });

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

    public void refresh(List<PlaceItem> list){

        this.list = list;
        notifyDataSetChanged();

    }

    private static class ViewHolder{

        int placeID;
        String nameAddress;
        double latitude, longitude;

        CheckBox checkBox;
        EditText nameAddressListLabel;
        Button mapListButton;

    }

}

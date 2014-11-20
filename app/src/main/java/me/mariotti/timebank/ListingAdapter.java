package me.mariotti.timebank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;


public class ListingAdapter extends ArrayAdapter {
    ArrayList<Listing> data;
    Context context;

    public ListingAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context=context;
        data=(ArrayList<Listing>)objects;
    }






    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item_main, null);
        }
        Listing mListing = (Listing) getItem(position);
        TextView description= (TextView) convertView.findViewById(R.id.row_descriptionText);
        TextView category= (TextView) convertView.findViewById(R.id.row_categoryText);
        TextView date = (TextView) convertView.findViewById(R.id.row_dateText);
        description.setText(mListing.description);
        category.setText(mListing.categoryName);
        date.setText(mListing.dateFormatter.format(mListing.dateCreation));

        return convertView;
    }

}

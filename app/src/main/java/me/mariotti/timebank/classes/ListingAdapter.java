package me.mariotti.timebank.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import me.mariotti.timebank.R;

import java.util.ArrayList;


public class ListingAdapter extends ArrayAdapter<Listing> {
    ArrayList<Listing> data;
    Context context;

    public ListingAdapter(Context context, int resource, ArrayList<Listing> objects) {
        super(context, resource, objects);
        this.context = context;
        data = objects;
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public Listing getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).id;
    }

    @Override
    public View getView(int position, View mView, ViewGroup parent) {
        if (mView == null) {
            mView = LayoutInflater.from(context).inflate(R.layout.row_item_main, null);
        }
        Listing mListing = getItem(position);
        TextView description = (TextView) mView.findViewById(R.id.row_descriptionText);
        TextView category = (TextView) mView.findViewById(R.id.row_categoryText);
        TextView date = (TextView) mView.findViewById(R.id.row_dateText);

        description.setText(mListing.description.length() > 200 ? mListing.description.substring(0, 200) + "..." : mListing.description);
        category.setText(mListing.categoryName);
        date.setText(Listing.dateFormatter.format(mListing.dateCreation));

        return mView;
    }
}

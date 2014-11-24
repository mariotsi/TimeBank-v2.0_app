package me.mariotti.timebank;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import me.mariotti.timebank.classes.Listing;


public class SingleListingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_listing_activity);
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.LISTING_OBJECT);
        //((TextView)findViewById(R.id.descriptionText)).setText(message);
        // TODO hide or show fields based on ownership
        Bundle data= getIntent().getExtras();
        Listing mListing = data.getParcelable(MainActivity.LISTING_OBJECT);
        ((TextView)findViewById(R.id.descriptionText)).setText(mListing.description);
        ((TextView)findViewById(R.id.categoryText)).setText(mListing.categoryName+mListing.categoryId);
        ((TextView)findViewById(R.id.dateText)).setText(Listing.dateFormatter.format(mListing.dateCreation));
        ((CheckBox)findViewById(R.id.checkBox_requested)).setChecked(!mListing.requested);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_listing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_menu_item) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editListing(MenuItem item) {
    }

    public void deleteListing(MenuItem item) {
    }
}

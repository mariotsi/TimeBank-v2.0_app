package me.mariotti.timebank;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.ListingAdapter;
import me.mariotti.timebank.classes.RESTCaller;
import me.mariotti.timebank.classes.User;

import java.util.ArrayList;


public class MainActivity extends Activity {
    public static final String LISTING_OBJECT = "me.mariotti.timebank.listing_object";
    private String[] values;
    private ListView mListView;
    private ArrayList<Listing> mList;
    public ListingAdapter mListingAdapter;
    public ProgressDialog progress;
    public static User loggedUser=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mListView = (ListView) findViewById(R.id.listView);
        progress = new ProgressDialog(this);
        progress.setMessage("Loading listings");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        //mList = new ArrayList<String>();
        mList = new ArrayList<Listing>();
        /*
        values = new String[]{"Android", "iPhone", "WindowsMobile",
                              "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                              "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                              "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                              "Android", "iPhone", "WindowsMobile"};
        */
        values = new String[]{"No listings available"};
        //Collections.addAll(mList, values);
        mListingAdapter = new ListingAdapter(this, 0, mList);
        mListView.setAdapter(mListingAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(parent.getContext(),SingleListingActivity.class);
                intent.putExtra(LISTING_OBJECT, mListingAdapter.getItem(position));
                startActivity(intent);
            }
        });
        refreshListings();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onResume() {
        super.onResume();


    }

    public void refreshListings() {
        new RESTCaller(this).execute();
    }

    public void refresh(MenuItem item) {
        refreshListings();
    }
}

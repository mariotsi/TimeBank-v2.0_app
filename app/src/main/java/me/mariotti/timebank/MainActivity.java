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
import me.mariotti.timebank.RESTWorkers.ListingWorker;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.ListingAdapter;
import me.mariotti.timebank.classes.RESTCaller;
import me.mariotti.timebank.classes.User;
import me.mariotti.timebank.profile.ProfileActivity;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private String[] values;
    private ListView mListView;
    private ArrayList<Listing> mList;
    public ListingAdapter mListingAdapter;
    public ProgressDialog progress;
    public static User loggedUser = null;
    private Menu mOptionsMenu;
    private static boolean areListingsOutdated = true;

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
                Intent intent = new Intent(parent.getContext(), ListingDetailActivity.class);
                intent.putExtra(ListingDetailActivity.LISTING_OBJECT, mListingAdapter.getItem(position));
                startActivity(intent);
            }
        });
        refreshListings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem LogInOut = mOptionsMenu.findItem(R.id.menu__main_activity__log_in_out);
        Intent intent = (new Intent(this, LoginActivity.class));
        if (User.isLogged) {
            LogInOut.setTitle(R.string.log_out);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
        } else {
            LogInOut.setTitle(R.string.log_in);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
        }
        LogInOut.setIntent(intent);
        mOptionsMenu.findItem(R.id.menu__main_activity__profile).setIntent(new Intent(this, ProfileActivity.class));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem profile = mOptionsMenu.findItem(R.id.menu__main_activity__profile);
        MenuItem add = mOptionsMenu.findItem(R.id.menu__main_activity__new);
        if (User.isLogged) {
            profile.setVisible(true);
            add.setVisible(true);
        } else {
            profile.setVisible(false);
            add.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu__main_activity__new) {
            Intent intent = new Intent(this, NewEditActivity.class);
            intent.putExtra(NewEditActivity.ACTION, NewEditActivity.NEW);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (mOptionsMenu != null) {
            MenuItem LogInOut = mOptionsMenu.findItem(R.id.menu__main_activity__log_in_out);
            Intent intent = (new Intent(this, LoginActivity.class));
            if (User.isLogged) {
                LogInOut.setTitle(R.string.log_out);
                intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
            } else {
                LogInOut.setTitle(R.string.log_in);
                intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
            }
            LogInOut.setIntent(intent);
        }
        if (areListingsOutdated) {
            refreshListings();
            areListingsOutdated = false;
        }
    }

    public void refreshListings() {
        new ListingWorker(this, RESTCaller.GET_LISTING_LIST).execute();
    }

    public void refresh(MenuItem item) {
        refreshListings();
    }

    public static void markListingsAsOutdated() {
        areListingsOutdated = true;
    }
}

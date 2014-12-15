package me.mariotti.timebank.profile;

import java.util.ArrayList;
import java.util.Locale;

import android.app.*;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.mariotti.timebank.LoginActivity;
import me.mariotti.timebank.NewEditActivity;
import me.mariotti.timebank.R;
import me.mariotti.timebank.RESTWorkers.UserWorker;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.ListingAdapter;
import me.mariotti.timebank.classes.RESTCaller;
import me.mariotti.timebank.classes.User;


public class ProfileActivity extends Activity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public ArrayList<Listing> mRequestedListingsArray;
    public ArrayList<Listing> mMyListingsArray;
    public ListingAdapter mRequestedListingsAdapter;
    public ListingAdapter mMyListingsAdapter;
    public ProgressDialog progress;
    public ArrayList<Listing> mListingsArray;
    private Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progress = new ProgressDialog(this);
        progress.setMessage("Loading listings");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mListingsArray = new ArrayList<>();
        mMyListingsArray = new ArrayList<>();
        mRequestedListingsArray = new ArrayList<>();
        mRequestedListingsAdapter = new ListingAdapter(this, 0, mRequestedListingsArray);
        mMyListingsAdapter = new ListingAdapter(this, 0, mMyListingsArray);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem add = mOptionsMenu.findItem(R.id.menu__profile_activity__new);
        if (User.isLogged) {
            add.setVisible(true);
        } else {
            add.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        MenuItem LogInOut = mOptionsMenu.findItem(R.id.menu__profile_activity__log_in_out);
        Intent intent = (new Intent(this, LoginActivity.class));
        if (User.isLogged) {
            LogInOut.setTitle(R.string.log_out);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
        } else {
            LogInOut.setTitle(R.string.log_in);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
        }
        LogInOut.setIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu__profile_activity__refresh) {
            refreshListings();
        }
        if (id == R.id.menu__profile_activity__new) {
            Intent intent= new Intent(this,NewEditActivity.class);
            intent.putExtra(NewEditActivity.ACTION,NewEditActivity.NEW);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshListings() {
        new UserWorker(this, RESTCaller.GET_MY_PROFILE).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListings();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MyListings.newInstance("My Listings");
                case 1:
                    return ListingsRequested.newInstance("Listings Requested");
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);

            }
            return null;
        }
    }



}

package me.mariotti.timebank;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import me.mariotti.timebank.RESTWorkers.CategoryWorker;
import me.mariotti.timebank.RESTWorkers.CityWorker;
import me.mariotti.timebank.RESTWorkers.ListingWorker;
import me.mariotti.timebank.classes.*;
import me.mariotti.timebank.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    private String[] values;
    private ListView mListView;
    private ArrayList<Listing> mList;
    public ListingAdapter mListingAdapter;
    public ProgressDialog progress;
    public static User loggedUser = null;
    private Menu mOptionsMenu;
    private static boolean areListingsOutdated = true;
    public HashMap<String, Integer> categoryMap;
    public EditText descriptionSearch;
    public ArrayList<Category> categoryList;
    public ArrayList<String> provinceList;
    public ArrayList<City> cityList;
    public ArrayAdapter<Category> categorySpinnerAdapter;
    public ArrayAdapter<String> provinceSpinnerAdapter;
    public ArrayAdapter<City> citySpinnerAdapter;
    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private Spinner categorySpinner;
    private boolean searchPanelIsVisible = false;

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
        //Search UI
        categoryMap = new HashMap<>();//maintain link between name and id
        categoryList = new ArrayList<>();
        categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        provinceList = new ArrayList<>();
        provinceSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceList);
        provinceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cityList = new ArrayList<>();
        cityList.add(new City("City"));
        cityList.add(new City("Select a province first"));
        citySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Initialize references to UI views
        categorySpinner = (Spinner) findViewById(R.id.main_category_spinner);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        provinceSpinner = (Spinner) findViewById(R.id.main_province_spinner);
        provinceSpinner.setAdapter(provinceSpinnerAdapter);
        citySpinner = (Spinner) findViewById(R.id.main_city_spinner);
        citySpinner.setAdapter(citySpinnerAdapter);
        descriptionSearch = (EditText) findViewById(R.id.description_search_editText);

        //Hide all search UI
        citySpinner.setVisibility(View.GONE);
        categorySpinner.setVisibility(View.GONE);
        provinceSpinner.setVisibility(View.GONE);
        descriptionSearch.setVisibility(View.GONE);

        initializeListeners();

        new CategoryWorker(this, RESTCaller.GET_CATEGORIES_FOR_SEARCH).execute();
        new CityWorker(this, RESTCaller.GET_PROVINCES).execute();
        refreshListings();
    }

    private void initializeListeners() {
        descriptionSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshSearchResults();
            }
        });
        categorySpinner.setOnItemSelectedListener(new SpinnerListener());
        provinceSpinner.setOnItemSelectedListener(new SpinnerListener());
        citySpinner.setOnItemSelectedListener(new SpinnerListener());
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
        MenuItem searchButton = mOptionsMenu.findItem(R.id.menu__main_activity__search);
        searchButton.setIcon(searchPanelIsVisible?R.drawable.ic_action_close_search:R.drawable.ic_action_search);
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
        if (searchPanelIsVisible) {
            refreshSearchResults();
        } else {
            new ListingWorker(this, RESTCaller.SEARCH_LISTINGS).execute();
        }
    }

    public void refresh(MenuItem item) {
        refreshListings();
    }

    public static void markListingsAsOutdated() {
        areListingsOutdated = true;
    }

    public void toggleSearch(MenuItem item) {
        if (searchPanelIsVisible) {
            citySpinner.setVisibility(View.GONE);
            categorySpinner.setVisibility(View.GONE);
            provinceSpinner.setVisibility(View.GONE);
            descriptionSearch.setVisibility(View.GONE);
        } else {
            citySpinner.setVisibility(View.VISIBLE);
            categorySpinner.setVisibility(View.VISIBLE);
            provinceSpinner.setVisibility(View.VISIBLE);
            descriptionSearch.setVisibility(View.VISIBLE);
        }
        invalidateOptionsMenu();
        searchPanelIsVisible = !searchPanelIsVisible;
    }

    private void refreshSearchResults() {
        if (searchPanelIsVisible) {
            HashMap<String, Object> data = new HashMap<>(4);
            String description = descriptionSearch.getText().toString();

            int category = categorySpinner.getCount() > 0 ? ((Category) categorySpinner.getSelectedItem()).category_id : -1;
            String province = provinceSpinner.getCount() > 0 ? provinceSpinner.getSelectedItem().toString() : "All";
            int city = ((City) citySpinner.getSelectedItem()).id;
            if (description.length() > 0) {
                data.put("description", description);
                if (description.equals("TUTTI")){
                    new ListingWorker(this, RESTCaller.GET_LISTING_LIST).execute();
                    return;
                }
            }
            if (category > -1) {
                data.put("category", category);
            }
            if (!province.equals("All")) {
                data.put("province", province);
            }
            if (city > -1) {
                data.put("city", city);
            }
            new ListingWorker(this, RESTCaller.SEARCH_LISTINGS, data).execute();
        }
    }

    private void updateCities(String province) {
        new CityWorker(this, RESTCaller.GET_CITIES_BY_PROVINCE, province).execute();
        citySpinner.setSelection(0);
    }

    class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId() == R.id.main_province_spinner && !((TextView) view).getText().toString().equals("All")) {
                updateCities(((TextView) view).getText().toString());
            }
            refreshSearchResults();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
}

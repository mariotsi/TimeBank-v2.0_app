package me.mariotti.timebank;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import me.mariotti.timebank.RESTWorkers.CategoryWorker;
import me.mariotti.timebank.RESTWorkers.ListingWorker;
import me.mariotti.timebank.classes.Category;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.RESTCaller;

import java.util.ArrayList;
import java.util.HashMap;


public class NewEditActivity extends Activity {

    public static final String ACTION = "me.mariotti.timebank.NewEditActivity.ACTION";
    public static final int NEW = 1;
    public static final int EDIT = 2;
    public static final String LISTING_OBJECT = "me.mariotti.timebank.NewEditActivity.LISTING_OBJECT";
    private int action;
    private Spinner categorySpinner;
    public ArrayAdapter<Category> categorySpinnerAdapter;
    public ArrayList<Category> categoryList;
    public ProgressDialog progress;
    private EditText descriptionText;
    public Listing mListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        setContentView(R.layout.activity_new_edit);
        action = getIntent().getExtras().getInt(ACTION);
        categoryList = new ArrayList<>();
        categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        //Initialize references to UI views
        categorySpinner = (Spinner) findViewById(R.id.new_edit__category_spinner);
        categorySpinner.setAdapter(categorySpinnerAdapter);
        descriptionText = (EditText) findViewById(R.id.new_edit__description_editText);
        new CategoryWorker(this, RESTCaller.GET_CATEGORIES).execute();
        if (action == NEW) {
            setTitle(getString(R.string.title__create_listing));
        } else if (action == EDIT) {
            setTitle(getString(R.string.title__edit_listing));
            Bundle data = getIntent().getExtras();
            mListing = data.getParcelable(LISTING_OBJECT);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu__newedit_activity__save) {
            if (action == NEW) {
                createListing();
            } else if (action == EDIT) {
                editListing();
            }
        }
        return true;
    }

    private void editListing() {
        if (descriptionText.getText().toString().length() > 0) {
            int category = ((Category) categorySpinner.getSelectedItem()).category_id;
            HashMap<String, Object> tempMap = new HashMap<>(2);
            tempMap.put("category", category);
            tempMap.put("description", descriptionText.getText().toString());
            new ListingWorker(this, RESTCaller.EDIT_LISTING, tempMap, String.valueOf(mListing.id)).execute();
        }
    }

    private void createListing() {
        if (descriptionText.getText().toString().length() > 0) {
            int category = ((Category) categorySpinner.getSelectedItem()).category_id;
            HashMap<String, Object> tempMap = new HashMap<>(2);
            tempMap.put("category", category);
            tempMap.put("description", descriptionText.getText().toString());
            new ListingWorker(this, RESTCaller.CREATE_LISTING, tempMap).execute();
        }
    }

    /**
     * Updated description and category based on current mListing instance in ListingDetailActivity
     */
    public void updateUI() {
        //This is called only when categories are downloaded and work only on EDIT cause of mListing!=null
        if (mListing != null) {
            int categoryIndex=0;
            for (Category category:categoryList){
                if (category.category_id==mListing.categoryId){
                    categoryIndex=categoryList.indexOf(category);
                }
            }
            categorySpinner.setSelection(categoryIndex);
            descriptionText.setText(mListing.description);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        progress.dismiss();
    }



}

package me.mariotti.timebank;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends Activity {
    private String[] values;
    private ListView mListView;
    private ArrayList<String> mList;
    public ArrayAdapter<String> mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<String>();
        /*
        values = new String[]{"Android", "iPhone", "WindowsMobile",
                              "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                              "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                              "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                              "Android", "iPhone", "WindowsMobile"};
        */
        values = new String[]{"No listings available"};
        Collections.addAll(mList, values);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(mAdapter);
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
        if (id == R.id.action_settings) {
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

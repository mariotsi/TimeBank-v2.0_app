package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import me.mariotti.timebank.MainActivity;
import me.mariotti.timebank.NewEditActivity;
import me.mariotti.timebank.classes.Category;
import me.mariotti.timebank.classes.RESTCaller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CategoryWorker extends RESTCaller {

    public CategoryWorker(Activity mActivity, int command, String... params) {
        super(mActivity, command, null, params);
    }

    protected void onPreExecute() {
        switch (command) {
            case GET_CATEGORIES:
                mResourceUrl = "categories/";
                ((NewEditActivity) mActivity).progress.setMessage("Loading categories list");
                ((NewEditActivity) mActivity).progress.show();
                break;
            case GET_CATEGORIES_FOR_SEARCH:
                mResourceUrl = "categories/";
                break;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        String message = "Generic Error";
        switch (command) {
            case GET_CATEGORIES:
                int responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
                        //Initialize reference to Activity fields
                        ArrayAdapter categorySpinnerAdapter = ((NewEditActivity) this.mActivity).categorySpinnerAdapter;
                        ArrayList<Category> categoryList = ((NewEditActivity) this.mActivity).categoryList;
                        JSONArray body = s.getJSONArray("responseBody");
                        categoryList.clear();
                        Category tempCategory;
                        for (int i = 0; i < body.length(); i++) {
                           tempCategory=new Category(body.getJSONObject(i));
                            categoryList.add(tempCategory);
                        }
                        Collections.sort(categoryList);
                        categorySpinnerAdapter.notifyDataSetChanged();
                    } else {
                        switch (s.getInt("responseCode")) {
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    if (responseCode != 200)
                        Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                    ((NewEditActivity) mActivity).progress.dismiss();
                    ((NewEditActivity) mActivity).updateUI();
                }
                break;
            case GET_CATEGORIES_FOR_SEARCH:
               responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
                        //Initialize reference to Activity fields
                        ArrayAdapter categorySpinnerAdapter = ((MainActivity) this.mActivity).categorySpinnerAdapter;
                        //HashMap<String, Integer> categoryMap = ((MainActivity) this.mActivity).categoryMap;
                        ArrayList<Category> categoryList = ((MainActivity) this.mActivity).categoryList;
                        JSONArray body = s.getJSONArray("responseBody");
                        categoryList.clear();
                        Category tempCategory;
                        for (int i = 0; i < body.length(); i++) {
                            tempCategory=new Category(body.getJSONObject(i));
                            categoryList.add(tempCategory);
                        }
                        Collections.sort(categoryList);
                        categoryList.add(0,new Category("All"));
                        categorySpinnerAdapter.notifyDataSetChanged();
                    } else {
                        switch (s.getInt("responseCode")) {
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    if (responseCode != 200)
                        Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}

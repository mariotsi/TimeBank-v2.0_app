package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import me.mariotti.timebank.NewEditActivity;
import me.mariotti.timebank.classes.RESTCaller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CategoryWorker extends RESTCaller {
    public CategoryWorker(Activity mActivity, int command, HashMap<String, Object> outDataMap, String... params) {
        super(mActivity, command, outDataMap, params);
    }
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
                        ArrayAdapter categorySpinnerAdapter = ((NewEditActivity) this.mActivity).categorySpinnerAdapter;
                        HashMap<String, Integer> categoryMap = ((NewEditActivity) this.mActivity).categoryMap;
                        ArrayList<String> categoryList = ((NewEditActivity) this.mActivity).categoryList;
                        JSONArray body = s.getJSONArray("responseBody");
                        categoryList.clear();
                        String tempName;
                        int tempId;
                        for (int i = 0; i < body.length(); i++) {
                            tempId = body.getJSONObject(i).getInt("category_id");
                            tempName = body.getJSONObject(i).getString("name");
                            categoryMap.put(tempName, tempId);
                            categoryList.add(tempName);
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
                    ((NewEditActivity) mActivity).progress.hide();
                    ((NewEditActivity) mActivity).updateUI();
                }
                break;
        }
    }
}

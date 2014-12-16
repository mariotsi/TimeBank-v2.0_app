package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import me.mariotti.timebank.MainActivity;
import me.mariotti.timebank.MainActivity;
import me.mariotti.timebank.classes.City;
import me.mariotti.timebank.classes.RESTCaller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class CityWorker extends RESTCaller{

    public CityWorker(Activity mActivity, int command, String... params) {
        super(mActivity, command, null, params);
    }

    protected void onPreExecute() {
        switch (command) {
            case GET_PROVINCES:
                mResourceUrl = "cities/provinces/";
                                break;
            case GET_CITIES_BY_PROVINCE:
                mResourceUrl = "cities/get_cities_by_province/?province="+params[0];
                break;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        String message = "Generic Error";
        switch (command) {
            case GET_PROVINCES:
                int responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
                        //Initialize reference to Activity fields
                        ArrayAdapter provinceSpinnerAdapter = ((MainActivity) this.mActivity).provinceSpinnerAdapter;
                        ArrayList<String> provinceList = ((MainActivity) this.mActivity).provinceList;
                        JSONArray body = s.getJSONArray("responseBody");
                        for (int i = 0; i < body.length(); i++) {
                            provinceList.add(body.get(i).toString());
                        }
                        Collections.sort(provinceList);
                        provinceList.add(0,"All");
                        provinceSpinnerAdapter.notifyDataSetChanged();
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
            case GET_CITIES_BY_PROVINCE:
                responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
                        //Initialize reference to Activity fields
                        ArrayAdapter citySpinnerAdapter = ((MainActivity) this.mActivity).citySpinnerAdapter;
                        ArrayList<City> cityList = ((MainActivity) this.mActivity).cityList;
                        JSONArray body = s.getJSONArray("responseBody");
                        cityList.clear();
                        City tempCity;
                        for (int i = 0; i < body.length(); i++) {
                            tempCity = new City(body.getJSONArray(i));
                            cityList.add(tempCity);
                        }
                        Collections.sort(cityList);
                        cityList.add(0,new City("All"));
                        citySpinnerAdapter.notifyDataSetChanged();
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

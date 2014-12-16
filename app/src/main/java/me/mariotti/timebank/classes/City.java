package me.mariotti.timebank.classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class City implements Comparable<City> {
    String area_code;
    String cadastral_code;
    String cap;
    public int id;
    int inhabitants;
    String name;
    String province;
    String region;

    public City(JSONObject json) {
        try {
            area_code = json.getString("area_code");
            cadastral_code = json.getString("cadastral_code");
            cap = json.getString("cap");
            id = json.getInt("id");
            inhabitants = json.getInt("inhabitants");
            name = json.getString("name");
            province = json.getString("province");
            region = json.getString("region");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public City(String name) {
        area_code = "";
        cadastral_code = "";
        cap = "";
        id = -1;
        inhabitants = -1;
        this.name = name;
        province = "";
        region = "";
    }

    public City(JSONArray city) {
        try {
            area_code = "";
            cadastral_code = "";
            cap = "";
            id = city.getInt(1);
            inhabitants = -1;
            name = city.getString(0);
            province = "";
            region = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public int compareTo(City city) {
        return name.compareTo(city.name);
    }
}

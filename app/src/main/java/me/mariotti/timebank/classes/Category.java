package me.mariotti.timebank.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Category implements Comparable<Category> {
    public String name;
    public int category_id;

    public Category(JSONObject json) {
        try {
            category_id = json.getInt("category_id");
            name = json.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Category(String name) {
        this.name = name;
        category_id = -1;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Category category) {
        return name.compareTo(category.name);
    }
}

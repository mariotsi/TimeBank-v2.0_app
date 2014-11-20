package me.mariotti.timebank;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Listing {
    public int id;
    public String description;
    public int categoryId;
    public String categoryName;
    public Date dateCreation;
    public int applicant;
    public boolean requested;
    // TODO add owner class and an owner field
    DateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public Listing(JSONObject json) {
        try {
            description = json.getString("description");
            categoryId = json.getInt("category");
            dateCreation = mDateFormat.parse(json.getString("creation_date"));
            applicant = json.getInt("applicant");
            requested = json.getBoolean("requested");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

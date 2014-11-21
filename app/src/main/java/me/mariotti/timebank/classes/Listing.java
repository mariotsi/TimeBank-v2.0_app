package me.mariotti.timebank.classes;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Listing{
    public int id;
    public String description;
    public int categoryId;
    public String categoryName;
    public Date dateCreation;
    public int applicant;
    public boolean requested;
    // TODO add owner class and an owner field
    DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    public Listing(JSONObject json) {
        try {
            id = json.getInt("id");
            description = json.getString("description");
            categoryId = json.getInt("category");
            dateCreation = dateParser.parse(json.getString("creation_date"));
            applicant = json.getInt("applicant");
            requested = json.getBoolean("requested");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "Description: " + description + "\t-\t" + "Category id: " + categoryId + "\t-\t" + "Category name: " +
               categoryName + "\t-\t" + "Pubblishing Date: " + dateFormatter.format(dateCreation) + "\t-\t" + "Apllicant: " +
               applicant + "\t-\t" + "Requested: " + requested;
    }
}

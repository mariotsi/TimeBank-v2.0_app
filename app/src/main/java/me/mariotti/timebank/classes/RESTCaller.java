package me.mariotti.timebank.classes;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class RESTCaller extends AsyncTask<String, Integer, JSONObject> {

    public static final int GET_LISTING_LIST = 1;

    public static final String mServerUrl = "https://agile-headland-8492.herokuapp.com/";
    protected String mResourceUrl;
    final String TAG = "RESTCaller";
    protected ListingAdapter mListingAdapter;
    protected Activity mActivity;
    protected int command;
    protected int[] params;

    public RESTCaller(Activity mActivity, int command, int... params) {
        this.mActivity = mActivity;
        this.command = command;
        this.params = params;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject mJSONObject = new JSONObject();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mServerUrl + mResourceUrl); //TODO shuold be listings/search to omit requested listings
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
            mJSONObject = JsonUtils.urlResponseToJson(in, urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (Exception e) {//TODO errors 4xx should be used
            e.printStackTrace();
            try {
                mJSONObject.put("hasErrors", true);
                mJSONObject.put("errorMessage", e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return mJSONObject;
    }

}




package me.mariotti.timebank.classes;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class RESTCaller extends AsyncTask<String, Integer, JSONObject> {

    private String mServerUrl = "https://agile-headland-8492.herokuapp.com/";
    final String TAG = "RESTCaller";
    private ListingAdapter mListingAdapter;
    private MainActivity mMainActivity;

    @Override
    protected void onPreExecute() {
        mListingAdapter.clear();
        super.onPreExecute();
        mMainActivity.progress.show();
    }

    public RESTCaller(MainActivity mMainActivity) {
        mListingAdapter = mMainActivity.mListingAdapter;
        this.mMainActivity = mMainActivity;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);
        try {
            if (!s.getBoolean("hasErrors")) {
                JSONArray body = s.getJSONArray("responseBody");
                ArrayList<Listing> listingsArray = new ArrayList<Listing>();
                for (int i = 0; i < body.length(); i++) {
                    listingsArray.add(new Listing(body.getJSONObject(i)));
                    mListingAdapter.add(listingsArray.get(i));
                }

            } else {
                Toast.makeText(mMainActivity.getBaseContext(), s.getString("errorMessage"), Toast.LENGTH_LONG).show();
//                mListingAdapter.add("An error has occurred while downloading the listings list. Retry");
            }
        } catch (JSONException e) {
            Log.e("RESTCaller", e.getMessage());
        } finally {
            mMainActivity.progress.hide();
        }
        mListingAdapter.notifyDataSetChanged();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject mJSONObject = new JSONObject();
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mServerUrl + "listings/"); //TODO shuold be listings/search to omit requested listings
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
            mJSONObject = responseToJson(in, urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (Exception e) {
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

    public JSONObject responseToJson(InputStreamReader mInputStreamReader, int responseCode, String responseMessage) {
        String line;
        StringBuilder mStringBuilder = new StringBuilder("");
        String serverResponseMessage = "";
        try {
            BufferedReader br = new BufferedReader(mInputStreamReader);
            while ((line = br.readLine()) != null)
                mStringBuilder.append(line);
            mInputStreamReader.close();
            serverResponseMessage = mStringBuilder.toString();
            Log.i(TAG, serverResponseMessage);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        JSONObject response = new JSONObject();
        try {
            response.put("hasErrors", false);
            response.put("responseCode", responseCode);
            response.put("responseMessage", responseMessage);
            response.put("responseBody", new JSONArray(serverResponseMessage));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}




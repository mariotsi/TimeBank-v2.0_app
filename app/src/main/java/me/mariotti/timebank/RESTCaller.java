package me.mariotti.timebank;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Simone on 09/11/2014.
 */
public class RESTCaller extends AsyncTask<String, Integer, JSONObject> {

    private String mServerUrl = "http://10.0.3.2:8000/";
    final String TAG = "RESTCaller";
    private ArrayAdapter<String> mAdapter;
    private MainActivity mMainActivity;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public RESTCaller(MainActivity mMainActivity) {
        mAdapter = mMainActivity.mAdapter;
        this.mMainActivity = mMainActivity;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);

        try {
            if (!s.getBoolean("hasErrors")) {
                mAdapter.clear();
                mAdapter.add(s.getString("responseBody"));
            } else {
                Toast.makeText(mMainActivity.getBaseContext(), s.getString("errorMessage"), Toast.LENGTH_LONG).show();
                mAdapter.clear();
                //mAdapter.addAll("ciao","prova");
                mAdapter.add("An error has occurred while downloading the listings list. Retry");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject response = new JSONObject();

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mServerUrl + "listisngs/");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
            response = responseToJson(in, urlConnection.getResponseCode(), urlConnection.getResponseMessage());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            try {
                response.put("hasErrors", true);
                response.put("errorMessage", e.toString());

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.put("hasErrors", true);
                response.put("errorMessage", e.toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
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




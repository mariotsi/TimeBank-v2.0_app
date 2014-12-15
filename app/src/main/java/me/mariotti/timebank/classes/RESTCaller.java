package me.mariotti.timebank.classes;

import android.app.Activity;
import android.os.AsyncTask;
import me.mariotti.timebank.MainActivity;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RESTCaller extends AsyncTask<String, Integer, JSONObject> {

    public static final int GET_LISTING_LIST = 1;
    public static final int REQUEST_LISTING = 2;
    public static final int UNREQUEST_LISTING = 3;
    public static final int GET_SINGLE_LISTING = 4;
    public static final int GET_MY_PROFILE = 5;

    public static final String mServerUrl = "https://agile-headland-8492.herokuapp.com/";
    protected String mResourceUrl;
    final String TAG = "RESTCaller";
    protected ListingAdapter mListingAdapter;
    protected Activity mActivity;
    protected int command;
    protected int[] params;
    protected String HttpMethod = "GET";

    public RESTCaller(Activity mActivity, int command, int... params) {
        this.mActivity = mActivity;
        this.command = command;
        this.params = params;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject mJSONObject = new JSONObject();
        HttpURLConnection urlConnection = null;
        int responseCode = 1000;
        String responseMessage = "Unhandled error";
        InputStreamReader in = null;
        try {
            URL url = new URL(mServerUrl + mResourceUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(HttpMethod);
            if (User.isLogged) {
                urlConnection.setRequestProperty("Authorization", "Basic " + MainActivity.loggedUser.userCredentials);
            }
            in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
            responseCode = urlConnection.getResponseCode();
            responseMessage = urlConnection.getResponseMessage();
        } catch (Exception e) {
            /*An error 4xx throw an exception. Here catch the error number after the exception is fired. If
            urlConnection is not null but accessing it throws an IOException uses the default error code and get the
             exception toString() as error message. If urlConnection is null uses the default error code and message. */
            try {
                responseCode = urlConnection != null ? urlConnection.getResponseCode() : responseCode;
                responseMessage = urlConnection != null ? urlConnection.getResponseMessage() : responseMessage;
            } catch (IOException e1) {
                responseMessage = e1.toString();
            }
        } finally {
            mJSONObject = JsonUtils.urlResponseToJson(in, responseCode, responseMessage);
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return mJSONObject;
    }
}




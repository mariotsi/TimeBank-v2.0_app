package me.mariotti.timebank.classes;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Simone on 27/11/2014.
 */
public class JSONUtils {

    private static final String TAG = "JSON Utils";

    public static JSONObject urlResponseToJson(InputStreamReader mInputStreamReader, int responseCode, String responseMessage) {
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

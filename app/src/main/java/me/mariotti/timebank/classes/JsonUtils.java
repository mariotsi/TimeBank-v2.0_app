package me.mariotti.timebank.classes;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class JsonUtils {

    private static final String TAG = "JSON Utils";

    public static JSONObject urlResponseToJson(InputStreamReader mInputStreamReader, int responseCode, String responseMessage) {
        String line;
        StringBuilder mStringBuilder = new StringBuilder("");
        String serverResponseMessage = "";
        JSONObject response = new JSONObject();

        if (mInputStreamReader != null) {//This could be null if in RESTCaller we had and exception (also for 4xx)
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
        }

        try {
            if (responseCode < 1000) {//1000 and more are the only real error codes, 4xx should be handled in rest app
                response.put("hasErrors", false);
                response.put("responseCode", responseCode);
                response.put("responseMessage", responseMessage);
                //Checks if in the body there is an array or an object
                if (serverResponseMessage.startsWith("[") && serverResponseMessage.endsWith("]")) {
                    response.put("responseBody", new JSONArray(serverResponseMessage));
                } else {
                    response.put("responseBody", new JSONObject(serverResponseMessage));
                }
            } else {
                response.put("hasErrors", true);
                response.put("errorCode", responseCode);
                response.put("errorMessage", responseMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}

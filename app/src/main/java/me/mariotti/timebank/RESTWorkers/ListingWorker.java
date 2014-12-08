package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.MainActivity;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.RESTCaller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListingWorker extends RESTCaller {



    public ListingWorker(Activity mActivity, int command, int... params) {
        super(mActivity, command, params);
    }

    @Override
    protected void onPreExecute() {
        switch (command) {
            case GET_LISTING_LIST:
                mResourceUrl="listings/";//TODO shuold be listings/search to omit requested listings
                ((MainActivity) mActivity).progress.show();
                mListingAdapter = ((MainActivity) this.mActivity).mListingAdapter;
                mListingAdapter.clear();
                break;
        }
    }
    @Override
    protected void onPostExecute(JSONObject s) {
        switch (command){
            case GET_LISTING_LIST:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode")==200) {
                        JSONArray body = s.getJSONArray("responseBody");
                        ArrayList<Listing> listingsArray = new ArrayList<Listing>();
                        for (int i = 0; i < body.length(); i++) {
                            listingsArray.add(new Listing(body.getJSONObject(i)));
                            mListingAdapter.add(listingsArray.get(i));
                        }
                    } else {
                        Toast.makeText(mActivity.getBaseContext(), s.getString("errorMessage"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    ((MainActivity)mActivity).progress.hide();
                }
                mListingAdapter.notifyDataSetChanged();
                break;
        }
    }
}

package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.ListingAdapter;
import me.mariotti.timebank.classes.RESTCaller;
import me.mariotti.timebank.profile.ProfileActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class UserWorker extends RESTCaller {
    public UserWorker(Activity mActivity, int command, HashMap<String, Object> outDataMap, String... params) {
        super(mActivity, command, outDataMap, params);
    }
    public UserWorker(Activity mActivity, int command, String... params) {
        super(mActivity, command, null, params);
    }

    protected void onPreExecute() {
        switch (command) {
            case GET_MY_PROFILE:
                mResourceUrl = "users/my_profile/";
                ((ProfileActivity) mActivity).progress.show();
                break;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        String message = "Generic Error";
        switch (command) {
            case GET_MY_PROFILE:
                int responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
                        ListingAdapter mMyListingsAdapter = ((ProfileActivity) this.mActivity).mMyListingsAdapter;
                        ListingAdapter mRequestedListingsAdapter = ((ProfileActivity) this.mActivity).mRequestedListingsAdapter;
                        ArrayList<Listing> mListingsArray = ((ProfileActivity) this.mActivity).mListingsArray;
                        ArrayList<Listing> mMyListingsArray = ((ProfileActivity) this.mActivity).mMyListingsArray;
                        ArrayList<Listing> mRequestedListingsArray = ((ProfileActivity) this.mActivity).mRequestedListingsArray;
                        JSONArray body = s.getJSONArray("responseBody");
                        Listing tempListing;
                        mListingsArray.clear();
                        mMyListingsArray.clear();
                        mRequestedListingsArray.clear();
                        for (int i = 0; i < body.length(); i++) {
                            tempListing = new Listing(body.getJSONObject(i));
                            mListingsArray.add(tempListing);
                            if (tempListing.imOwner()) {
                                mMyListingsArray.add(tempListing);
                            } else if (tempListing.imTheApplicant()) {
                                mRequestedListingsArray.add(tempListing);
                            }
                        }
                        mRequestedListingsAdapter.notifyDataSetChanged();
                        mMyListingsAdapter.notifyDataSetChanged();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 401:
                                message = "Invalid authentication";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    if (responseCode != 200)
                        Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                    ((ProfileActivity) mActivity).progress.hide();
                }
                break;
        }
    }
}

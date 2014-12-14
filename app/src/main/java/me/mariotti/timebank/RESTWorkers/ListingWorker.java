package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.ListingDetailActivity;
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
                mResourceUrl = "listings/";//TODO shuold be listings/search to omit requested listings
                ((MainActivity) mActivity).progress.show();
                mListingAdapter = ((MainActivity) this.mActivity).mListingAdapter;
                mListingAdapter.clear();
                break;
            case REQUEST_LISTING:
                mResourceUrl = "listings/" + params[0] + "/claim/";
                HttpMethod = "PUT";
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case UNREQUEST_LISTING:
                mResourceUrl = "listings/" + params[0] + "/unclaim/";
                HttpMethod = "DELETE";
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case GET_SINGLE_LISTING:
                mResourceUrl = "listings/" + params[0] + "/";
                ((ListingDetailActivity) mActivity).progress.show();
                break;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        String message = "Generic Error";
        switch (command) {
            case GET_LISTING_LIST:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 200) {
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
                    ((MainActivity) mActivity).progress.hide();
                }
                mListingAdapter.notifyDataSetChanged();
                break;
            case REQUEST_LISTING:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 201) {
                        message = "Listing successfully requested";
                        //update the listing stored in the Activity with the updated version from server, then updateUI
                        //and force Listings Refresh next time the Main Activity is shown
                        new ListingWorker(mActivity, ListingWorker.GET_SINGLE_LISTING,  ((ListingDetailActivity) mActivity).getListingId()).execute();
                        ((ListingDetailActivity) mActivity).updateUI();
                        MainActivity.markListingsAsOutdated();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 404:
                                message = "Listing not found";
                                break;
                            case 403:
                                message = "Listing's owner cannot request it";
                                break;
                            case 406:
                                message = "Listing already requested";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    ((ListingDetailActivity) mActivity).progress.hide();
                    Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
                break;
            case UNREQUEST_LISTING:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 410) {
                        message = "Listing successfully unrequested";
                        //update the listing stored in the Activity with the updated version from server, then updateUI
                        //and force Listings Refresh next time the Main Activity is shown
                        new ListingWorker(mActivity, ListingWorker.GET_SINGLE_LISTING,  ((ListingDetailActivity) mActivity).getListingId()).execute();
                        ((ListingDetailActivity) mActivity).updateUI();
                        MainActivity.markListingsAsOutdated();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 404:
                                message = "Listing not found";
                                break;
                            case 403:
                                message = "You have not requested this listing (or the listing is not requested at all)";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {

                    ((ListingDetailActivity) mActivity).progress.hide();
                    Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
                break;
            case GET_SINGLE_LISTING:
                int responseCode=0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode=s.getInt("responseCode")) == 200) {
                        ((ListingDetailActivity) mActivity).setListing(new Listing(s.getJSONObject("responseBody")));
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 404:
                                message = "Listing not found";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    ((ListingDetailActivity) mActivity).updateUI();
                    MainActivity.markListingsAsOutdated();
                    ((ListingDetailActivity) mActivity).progress.hide();
                    if (responseCode!=200)
                    Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
}

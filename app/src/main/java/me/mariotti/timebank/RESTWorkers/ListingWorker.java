package me.mariotti.timebank.RESTWorkers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import me.mariotti.timebank.ListingDetailActivity;
import me.mariotti.timebank.MainActivity;
import me.mariotti.timebank.NewEditActivity;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.ListingAdapter;
import me.mariotti.timebank.classes.RESTCaller;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListingWorker extends RESTCaller {
    protected ListingAdapter mListingAdapter;

    /**
     * Used to instantiate a ListingWorker that will be used to output data to the webservice
     *
     * @param mActivity  the Activity that create the worker, usually this
     * @param command    action to do, see RESTCaller constants
     * @param outDataMap map containing data to output in format
     * @param params     additional String parameters
     */
    public ListingWorker(Activity mActivity, int command, HashMap<String, Object> outDataMap, String... params) {
        super(mActivity, command, outDataMap, params);
    }

    /**
     * Used to instantiate a ListingWorker that will not be used to output data to the webservice
     *
     * @param mActivity the Activity that create the worker, usually this
     * @param command   action to do, see RESTCaller constants
     * @param params    additional String parameters
     */
    public ListingWorker(Activity mActivity, int command, String... params) {
        super(mActivity, command, null, params);
    }

    @Override
    protected void onPreExecute() {
        switch (command) {
            case GET_LISTING_LIST:
                mResourceUrl = "listings/";//TODO shuold be listings/search to omit requested listings
                ((MainActivity) mActivity).progress.setMessage("Loading listings");
                ((MainActivity) mActivity).progress.show();
                break;
            case REQUEST_LISTING:
                mResourceUrl = "listings/" + params[0] + "/claim/";
                HttpMethod = "PUT";
                ((ListingDetailActivity) mActivity).progress.setMessage("Requesting listing");
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case UNREQUEST_LISTING:
                mResourceUrl = "listings/" + params[0] + "/unclaim/";
                HttpMethod = "DELETE";
                ((ListingDetailActivity) mActivity).progress.setMessage("Unrequesting listing");
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case GET_SINGLE_LISTING:
                mResourceUrl = "listings/" + params[0] + "/";
                ((ListingDetailActivity) mActivity).progress.setMessage("Retrieving listing");
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case CREATE_LISTING:
                HttpMethod = "POST";
                doOutput = true;
                doInput = false;//Ignore the returned new listing
                mResourceUrl = "listings/";
                ((NewEditActivity) mActivity).progress.setMessage("Creating listing");
                ((NewEditActivity) mActivity).progress.show();
                break;
            case EDIT_LISTING:
                HttpMethod = "PUT";
                doOutput = true;
                doInput = true;
                mResourceUrl = "listings/" + params[0] + "/";
                ((NewEditActivity) mActivity).progress.setMessage("Editing listing");
                ((NewEditActivity) mActivity).progress.show();
                break;
            case DELETE_LISTING:
                HttpMethod = "DELETE";
                mResourceUrl = "listings/" + params[0] + "/";
                ((ListingDetailActivity) mActivity).progress.setMessage("Deleting listing");
                ((ListingDetailActivity) mActivity).progress.show();
                break;
            case SEARCH_LISTINGS:
                doInput = true;
                StringBuilder postData = new StringBuilder();
                if (outDataMap != null) {
                    try {
                        for (Map.Entry<String, Object> param : outDataMap.entrySet()) {
                            if (postData.length() != 0) postData.append('&');
                            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                            postData.append('=');
                            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                mResourceUrl = "listings/search/?" + postData;
                break;
        }
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        String message = "Generic Error";
        switch (command) {
            case GET_LISTING_LIST:
                mListingAdapter = ((MainActivity) this.mActivity).mListingAdapter;
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 200) {
                        JSONArray body = s.getJSONArray("responseBody");
                        ArrayList<Listing> listingsArray = new ArrayList<>();
                        mListingAdapter.clear();
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
                        new ListingWorker(mActivity, ListingWorker.GET_SINGLE_LISTING, String.valueOf(((ListingDetailActivity) mActivity).getListingId())).execute();
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
                        new ListingWorker(mActivity, ListingWorker.GET_SINGLE_LISTING, String.valueOf(((ListingDetailActivity) mActivity).getListingId())).execute();
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
                int responseCode = 0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode = s.getInt("responseCode")) == 200) {
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
                    if (responseCode != 200)
                        Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                }
                break;
            case CREATE_LISTING:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 200) {//TODO change to 201 on server
                        message = "Listing successfully created";
                        MainActivity.markListingsAsOutdated();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 204:
                                message = "Empty description";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    ((NewEditActivity) mActivity).progress.hide();
                    Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                    mActivity.finish();
                }
                break;
            case EDIT_LISTING:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 200) {//TODO change to 201 on server
                        message = "Listing successfully edited";
                        MainActivity.markListingsAsOutdated();
                        //Force ListingDetailActivity to fetch updated version of listing just after I call finish()
                        //in the finally block. Note: i can open the edit Activity only from ListingDetailActivity
                        ListingDetailActivity.markListingsAsOutdated();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 204:
                                message = "Empty description";
                                break;
                            case 404:
                                message = "Listing not found";
                                break;
                            case 403:
                                message = "You are not owner of given listing";
                                break;
                            default:
                                message = s.getString("errorMessage");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    ((NewEditActivity) mActivity).progress.hide();
                    Toast.makeText(mActivity.getBaseContext(), message, Toast.LENGTH_LONG).show();
                    mActivity.finish();
                }
                break;
            case DELETE_LISTING:
                try {
                    if (!s.getBoolean("hasErrors") && s.getInt("responseCode") == 410) {
                        message = "Listing successfully deleted";
                        MainActivity.markListingsAsOutdated();
                    } else {
                        switch (s.getInt("responseCode")) {
                            case 404:
                                message = "Listing not found";
                                break;
                            case 403:
                                message = "You are not the owner";
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
                    mActivity.finish();
                }
                break;
            case SEARCH_LISTINGS:
                mListingAdapter = ((MainActivity) this.mActivity).mListingAdapter;
                responseCode=0;
                try {
                    if (!s.getBoolean("hasErrors") && (responseCode=s.getInt("responseCode")) == 200) {
                        JSONArray body = s.getJSONArray("responseBody");
                        ArrayList<Listing> listingsArray = new ArrayList<>();

                        mListingAdapter.clear();
                        for (int i = 0; i < body.length(); i++) {
                            listingsArray.add(new Listing(body.getJSONObject(i)));
                            mListingAdapter.add(listingsArray.get(i));
                        }
                    } else if (responseCode==204){
                        mListingAdapter.clear();
                    } else {
                        Toast.makeText(mActivity.getBaseContext(), s.getString("errorMessage"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("RESTCaller", e.getMessage());
                } finally {
                    if (((MainActivity) this.mActivity).progress.isShowing()){
                        ((MainActivity) this.mActivity).progress.dismiss();
                    }
                    mListingAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
